package galaxyoyo.minecraft.bukkit.jobs;

import static org.bukkit.event.Event.Result.ALLOW;
import static org.bukkit.event.Event.Result.DEFAULT;
import static org.bukkit.event.Event.Result.DENY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class Zone
{
	private static transient final JobsPlugin PLUGIN = (JobsPlugin) Bukkit.getPluginManager().getPlugin("Jobs-RPG");
	private static final AtomicInteger cursorId = new AtomicInteger(0);
	
	private final int id;
	private final Location a, b;
	private final VisedProtection visedProtec;
	private final HashMap<Protection, Result> protections = new HashMap<Protection, Result>();
	private final Object secure;
	
	/**
	 * @param a Premier point de la zone
	 * @param b Second point de la zone
	 * @param protec Type de protection
	 * @param args Permission si justifiée, joueurs autorisés si justifié
	 */
	public Zone(Location a, Location b, VisedProtection visedProtec, @Nullable Object ... args)
	{
		a = Validate.notNull(a);
		b = Validate.notNull(b);
		visedProtec = Validate.notNull(visedProtec);
		if (a.getWorld() != b.getWorld())
			throw new IllegalArgumentException("Zone must be in a same world");
		int x1 = Math.min(a.getBlockX(), b.getBlockX()), y1 = Math.min(a.getBlockY(), b.getBlockY()), z1 = Math.min(a.getBlockZ(), b.getBlockZ());
		int x2 = Math.max(a.getBlockX(), b.getBlockX()), y2 = Math.max(a.getBlockY(), b.getBlockY()), z2 = Math.max(a.getBlockZ(), b.getBlockZ());
		this.a = new Location(a.getWorld(), x1, y1, z1);
		this.b = new Location(b.getWorld(), x2, y2, z2);
		this.visedProtec = visedProtec;
		switch (this.visedProtec)
		{
			case JOB :
				List<Job> list = new ArrayList<Job>();
				for (Object o : args)
				{
					if (o instanceof String)
					{
						String arg = (String) o;
						try
						{
							list.add(Job.valueOf(arg));
						}
						catch (IllegalArgumentException ex)
						{
							PLUGIN.getLogger().warning("'" + arg + "' is not a job");
						}
					}
				}
				secure = list;
				break;
			case OP :
				secure = null;
				break;
			case PERMISSION :
				secure = Validate.notEmpty(args)[0];
				break;
			case PLAYERS :
				List<String> players = new ArrayList<String>();
				for (Object o : args)
					if (o instanceof String)
						players.add((String) o);
				secure = players;
				break;
			default :
				secure = null;
				break;
		}
		
		for (Protection protec : Protection.values())
		{
			protections.put(protec, protec.getDefaultValue());
		}
		
		int i = -1;
		for (Object o : args)
		{
			if (o instanceof Integer)
			{
				i = (int) o;
				break;
			}
		}
		id = i >= 0 ? i : cursorId.getAndIncrement();
	}
	
	/**
	 * @param x1 Abscisse X du point de départ de la zone
	 * @param y1 Ordonnée Y du point de départ de la zone
	 * @param z1 Profondeur Z du point de départ de la zone
	 * @param x2 Abscisse X du point d'arrivée de la zone
	 * @param y2 Ordonnée Y du point d'arrivée de la zone
	 * @param z2 Profondeur Z du point d'arrivée de la zone
	 * @param protec Type de protection
	 * @param args Permission si justifiée, joueurs autorisés si justifié
	 */
	public Zone(World world, int x1, int y1, int z1, int x2, int y2, int z2, VisedProtection visedProtec, @Nullable String ... args)
	{
		world = Validate.notNull(world);
		int x3 = Math.min(x1, x2), y3 = Math.min(y1, y2), z3 = Math.min(z1, z2);
		int x4 = Math.max(x1, x2), y4 = Math.max(y1, y2), z4 = Math.max(z1, z2);
		this.a = new Location(world, x3, y3, z3);
		this.b = new Location(world, x4, y4, z4);
		this.visedProtec = visedProtec;
		switch (this.visedProtec)
		{
			case JOB :
				List<Job> list = new ArrayList<Job>();
				for (String arg : args)
				{
					try
					{
						list.add(Job.valueOf(arg));
					}
					catch (IllegalArgumentException ex)
					{
						PLUGIN.getLogger().warning("'" + arg + "' is not a job");
					}
				}
				secure = list;
				break;
			case OP :
				secure = null;
				break;
			case PERMISSION :
				secure = Validate.notEmpty(args)[0];
				break;
			case PLAYERS :
				secure = new ArrayList<String>(Validate.notEmpty(Arrays.asList(Validate.notNull(args))));
				break;
			default :
				secure = null;
				break;
		}
		
		for (Protection protec : Protection.values())
		{
			setProtection(protec, protec.getDefaultValue());
		}
		
		id = cursorId.getAndIncrement();
	}
	
	public void appendArgs(String ... args)
	{
		if (visedProtec == VisedProtection.JOB)
		{
			for (String arg : args)
			{
				try
				{
					Job job = Job.valueOf(arg.toUpperCase());
					getJobs().add(job);
				}
				catch (IllegalArgumentException ex)
				{
					PLUGIN.getLogger().warning("'" + arg + "' is not a job");
				}
			}
		}
		else if (visedProtec == VisedProtection.PLAYERS)
		{
			List<String> argsList = Arrays.asList(args);
			if (argsList.remove("remove"))
				getAllowedPlayers().removeAll(argsList);
			else
				getAllowedPlayers().addAll(argsList);
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public Location getFirstPoint()
	{
		return a.clone();
	}
	
	public void setFirstPoint(Location point)
	{
		a.setX(point.getBlockX());
		a.setY(point.getBlockY());
		a.setZ(point.getBlockZ());
	}
	
	public Location getLastPoint()
	{
		return b.clone();
	}
	
	public void setLastPoint(Location point)
	{
		b.setX(point.getBlockX());
		b.setY(point.getBlockY());
		b.setZ(point.getBlockZ());
	}
	
	public VisedProtection getVisedProtection()
	{
		return visedProtec;
	}
	
	public Result getProtection(Protection protec)
	{
		return protections.get(protec);
	}
	
	public Result getProtection(String protec)
	{
		return getProtection(Protection.valueOf(protec.toUpperCase()));
	}
	
	public void setProtection(Protection protec, Result result)
	{
		protections.put(protec, result);
	}
	
	public Result onPlayerMove(PlayerMoveEvent event)
	{
		if (event.isCancelled())
			return DENY;
		
		Result result = getProtection(Protection.ENTRANCE);
		if (result != ALLOW)
		{
			if (!isInZone(event.getFrom()) && isInZone(event.getTo()))
			{
				if (result == DEFAULT && isAllowed(event.getPlayer()))
					return ALLOW;
				else
					return DENY;
			}
		}
		
		result = getProtection(Protection.EXIT);
		if (result != ALLOW)
		{
			if (isInZone(event.getFrom()) && !isInZone(event.getTo()))
			{
				if (result == DEFAULT && isAllowed(event.getPlayer()))
					return ALLOW;
				else
					return DENY;
			}
		}
		
		return ALLOW;
	}
	
	public Result onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return DENY;
		
		Result result = getProtection(Protection.PLACE);
		if (isInZone(event.getBlock().getLocation()))
		{
			if (result != ALLOW)
			{
				if (result == DEFAULT && isAllowed(event.getPlayer()))
					return ALLOW;
				else
					return DENY;
			}
			return ALLOW;
		}
		
		return null;
	}
	
	public Result onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled())
			return DENY;
		
		Result result = getProtection(Protection.BREAK);
		if (isInZone(event.getBlock().getLocation()))
		{
			if (result != ALLOW)
			{
				if (result == DEFAULT && isAllowed(event.getPlayer()))
					return ALLOW;
				else
					return DENY;
			}
			return ALLOW;
		}
		
		return null;
	}
	
	public Result onEntityDamage(EntityDamageEvent event)
	{
		if (event.isCancelled())
			return DENY;
		
		Result result = getProtection(Protection.DAMAGE);
		if (isInZone(event.getEntity().getLocation()))
		{
			if (result == DENY)
				return DENY;
			else
				return ALLOW;
		}
		return null;
	}
	
	public Result onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.isCancelled())
			return DENY;
		
		Result result = getProtection(Protection.INTERACT);
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (isInZone(event.getClickedBlock().getLocation()))
			{
				if (result != ALLOW)
				{
					if (result == DEFAULT && isAllowed(event.getPlayer()))
						return ALLOW;
					else
						return DENY;
				}
				return ALLOW;
			}
		}
		
		return null;
	}
	
	public boolean isInZone(Location loc)
	{
		int x1 = a.getBlockX(), y1 = a.getBlockY(), z1 = a.getBlockZ();
		int x2 = b.getBlockX(), y2 = b.getBlockY(), z2 = b.getBlockZ();
		int x3 = loc.getBlockX(), y3 = loc.getBlockY(), z3 = loc.getBlockZ();
		return x1 <= x3 && x3 <= x2 && y1 <= y3 && y3 <= y2 && z1 <= z3 && z3 <= z2;
	}
	
	public boolean isAllowed(Player player)
	{
		switch (visedProtec)
		{
			case JOB :
				List<Job> jobs = Arrays.asList(PLUGIN.getJobs(player));
				for (Job job : getJobs())
				{
					if (jobs.contains(job))
						return true;
				}
				return false;
			case OP :
				return player.isOp();
			case PERMISSION :
				return player.hasPermission(getPermission());
			case PLAYERS :
				return getAllowedPlayers().contains(player.getName());
		}
		
		return false;
	}
	
	/**
	 * @return Le job requis pour effectuer l'action
	 * @throws IllegalArgumentException si la sécurité n'est pas réservée à ceux possédant un job
	 */
	@SuppressWarnings("unchecked")
	public List<Job> getJobs() throws IllegalArgumentException
	{
		if (visedProtec != VisedProtection.JOB)
			throw new IllegalArgumentException("Secure type != job");
		return (List<Job>) secure;
	}
	
	/**
	 * @return La permission requise pour effectuer l'action
	 * @throws IllegalArgumentException si la sécurité n'est pas réservée à ceux possédant une permission
	 */
	public String getPermission() throws IllegalArgumentException
	{
		if (visedProtec != VisedProtection.PERMISSION)
			throw new IllegalArgumentException("Secure type != permission");
		return (String) secure;
	}
	
	/**
	 * @return Les joueurs autorisés pour effectuer l'action
	 * @throws IllegalArgumentException si la sécurité n'est pas réservée à une liste de joueurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAllowedPlayers() throws IllegalArgumentException
	{
		if (visedProtec != VisedProtection.PLAYERS)
			throw new IllegalArgumentException("Secure type != players");
		return (List<String>) secure;
	}
	
	public static final AtomicInteger getCursorId()
	{
		return cursorId;
	}
	
	public static enum VisedProtection
	{
		@Deprecated OP, PERMISSION, JOB, PLAYERS;
	}
	
	public static enum Protection
	{
		ENTRANCE(ALLOW), EXIT(ALLOW), PLACE(DEFAULT), BREAK(DEFAULT), INTERACT(DEFAULT), DAMAGE(ALLOW);
		
		private Result defaultValue;
		
		Protection(Result defaultValue)
		{
			this.defaultValue = defaultValue;
		}
		
		public Result getDefaultValue()
		{
			return defaultValue;
		}
	}
	
	public static class ZoneTypeAdapter extends TypeAdapter<Zone>
	{
		@Override
		public Zone read(JsonReader r) throws IOException
		{
			if (r.peek() == JsonToken.NULL)
				return null;
			int x1 =  -1, y1 = -1, z1 = -1, x2 = -1, y2 = -1, z2 = -1;
			World world = null;
			VisedProtection visedProtec = null;
			HashMap<Protection, Result> protections = new HashMap<Protection, Result>();
			String[] secure = new String[0];
			
			r.beginObject();
			while (r.peek() != JsonToken.END_OBJECT)
			{
				String name = r.nextName();
				if (name.equalsIgnoreCase("world"))
				{
					world = Bukkit.getWorld(r.nextString());
				}
				else if (name.equalsIgnoreCase("firstPoint"))
				{
					r.beginArray();
					x1 = r.nextInt();
					y1 = r.nextInt();
					z1 = r.nextInt();
					r.endArray();
				}
				else if (name.equalsIgnoreCase("lastPoint"))
				{
					r.beginArray();
					x2 = r.nextInt();
					y2 = r.nextInt();
					z2 = r.nextInt();
					r.endArray();
				}
				else if (name.equalsIgnoreCase("visedProtection"))
				{
					visedProtec = VisedProtection.valueOf(r.nextString().toUpperCase());
				}
				else if (name.equalsIgnoreCase("protections"))
				{
					r.beginArray();
					while (r.peek() != JsonToken.END_ARRAY)
					{
						r.beginObject();
						String protecName = r.nextName().toUpperCase(), resultName = r.nextString().toUpperCase();
						try
						{
							Protection protec = Protection.valueOf(protecName);
							protections.put(protec, Result.valueOf(resultName));
						}
						catch (IllegalArgumentException ex)
						{
							PLUGIN.getLogger().warning(ex.getMessage());
						}
						r.endObject();
					}
					r.endArray();
				}
				else if (name.equalsIgnoreCase("secure"))
				{
					if (r.peek() == JsonToken.STRING)
					{
						secure = new String[] {r.nextString()};
					}
					else
					{
						r.beginArray();
						ArrayList<String> list = new ArrayList<String>();
						while (r.peek() != JsonToken.END_ARRAY)
							list.add(r.nextString());
						secure = list.toArray(new String[secure.length]);
						r.endArray();
					}
				}
			}
			r.endObject();
			
			Zone zone = new Zone(world, x1, y1, z1, x2, y2, z2, visedProtec, secure);
			zone.protections.putAll(protections);
			return zone;
		}
		
		@Override
		public void write(JsonWriter w, Zone zone) throws IOException
		{
			if (zone == null)
			{
				System.err.println("zone == null");
				return;
			}
			
			w.beginObject();
			w.name("world");
			w.value(zone.getFirstPoint().getWorld().getName());
			w.name("firstPoint");
			w.beginArray();
			w.value(zone.getFirstPoint().getBlockX());
			w.value(zone.getFirstPoint().getBlockY());
			w.value(zone.getFirstPoint().getBlockZ());
			w.endArray();
			w.name("lastPoint");
			w.beginArray();
			w.value(zone.getLastPoint().getBlockX());
			w.value(zone.getLastPoint().getBlockY());
			w.value(zone.getLastPoint().getBlockZ());
			w.endArray();
			w.name("visedProtection");
			w.value(zone.getVisedProtection().name().toLowerCase());
			w.name("protections");
			w.beginArray();
			for (Entry<Protection, Result> entry : zone.protections.entrySet())
			{
				w.beginObject();
				w.name(entry.getKey().name().toLowerCase());
				w.value(entry.getValue().name().toLowerCase());
				w.endObject();
			}
			w.endArray();
			if (zone.secure != null)
			{
				w.name("secure");
				if (zone.secure instanceof String)
				{
					w.value((String) zone.secure);
				}
				else if (zone.secure instanceof Iterable)
				{
					w.beginArray();
					for (Object o : (Iterable<?>) zone.secure)
					{
						if (o != null)
							w.value(o.toString());
					}
					w.endArray();
				}
			}
			w.endObject();
		}
	}
}
