package galaxyoyo.minecraft.bukkit.jobs;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack>
{
	@SuppressWarnings({"deprecation", "unchecked"})
	@Override
	public ItemStack read(JsonReader r) throws IOException
	{
		r.beginObject();
		r.nextName();
		int typeId = r.nextInt();
		r.nextName();
		int amount = r.nextInt();
		r.nextName();
		short durability = (short) r.nextInt();
		r.nextName();
		Class<? extends MaterialData> dataClass = MaterialData.class;
		try
		{
			dataClass = (Class<? extends MaterialData>) Class.forName(r.nextString());
		}
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		r.nextName();
		MaterialData data = ((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getGson().fromJson(r, dataClass);
		r.nextName();
		Map<String, Object> map = ((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getGson()
						.fromJson(r, new TypeToken<HashMap<String, Object>>() {}.getType());
		ItemMeta meta = getItemMeta(map);
		r.endObject();
		ItemStack stack = new ItemStack(typeId, amount, durability);
		stack.setData(data);
		stack.setItemMeta(meta);
		return stack;
	}

	private ItemMeta getItemMeta(Map<String, Object> map)
	{
		try
		{
			Class<?> clazz = Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaItem$SerializableMeta", true, ItemMeta.class.getClassLoader());
			Method m = clazz.getDeclaredMethod("deserialize", Map.class);
			m.setAccessible(true);
			return (ItemMeta) m.invoke(null, map);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void write(JsonWriter w, ItemStack stack) throws IOException
	{
		w.beginObject();
		w.name("type");
		w.value(stack.getTypeId());
		w.name("amount");
		w.value(stack.getAmount());
		w.name("durability");
		w.value(stack.getDurability());
		w.name("dataClass");
		w.value(stack.getData().getClass().getName());
		w.name("data");
		((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getGson().toJson(stack.getData(), MaterialData.class, w);
		w.name("meta");
		((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getGson()
						.toJson(stack.getItemMeta().serialize(), Map.class, w);
		w.endObject();
	}
	
}
