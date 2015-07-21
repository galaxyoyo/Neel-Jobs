package galaxyoyo.minecraft.bukkit.jobs;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class LocationTypeAdapter extends TypeAdapter<Location>
{
	@Override
	public Location read(JsonReader r) throws IOException
	{
		if (r.peek() == JsonToken.NULL)
		{
			r.nextNull();
			return null;
		}
		
		String str = r.nextString();
		String[] split = str.split(":");
		World world = Bukkit.getServer().getWorld(split[0]);
		double x = Double.parseDouble(split[1]);
		double y = Double.parseDouble(split[2]);
		double z = Double.parseDouble(split[3]);
		float yaw = Float.parseFloat(split[4]);
		float pitch = Float.parseFloat(split[5]);
		return new Location(world, x, y, z, yaw, pitch);
	}

	@Override
	public void write(JsonWriter w, Location loc) throws IOException
	{
		if (loc == null)
		{
			w.nullValue();
			return;
		}
		
		String str = loc.getWorld().getName() + ":";
		str += loc.getX() + ":";
		str += loc.getY() + ":";
		str += loc.getZ() + ":";
		str += loc.getYaw() + ":";
		str += loc.getPitch();
		w.value(str);
	}
}
