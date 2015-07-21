package galaxyoyo.minecraft.bukkit.jobs;

import java.io.IOException;

import org.bukkit.potion.PotionEffectType;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class PotionEffectTypeAdapter extends TypeAdapter<PotionEffectType>
{
	@Override
	public PotionEffectType read(JsonReader r) throws IOException
	{
		return PotionEffectType.getByName(r.nextString());
	}
	
	@Override
	public void write(JsonWriter w, PotionEffectType type) throws IOException
	{
		w.value(type.getName());
	}
}
