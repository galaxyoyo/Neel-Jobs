package galaxyoyo.minecraft.bukkit.jobs;

import galaxyoyo.minecraft.bukkit.jobs.executors.ChatCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.CompetencesCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.ForgeCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.IllegalityCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.InfinityItemsCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.JobCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.MineCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.PatternCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.ProdCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.ProtectionsCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.executors.WorkbenchCommandExecutor;
import galaxyoyo.minecraft.bukkit.jobs.skills.PhysicalSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JobsPlugin extends JavaPlugin
{
	private final Gson gson;
	private final Map<String, Job[]> jobsByPlayer = new HashMap<String, Job[]>();
	public final Map<Location, Integer> pepitesPerDay = new HashMap<Location, Integer>();
	public final Map<Location, InfinityItem> infinityItems = new HashMap<Location, InfinityItem>();
	public final Map<Integer, Zone> protectionZones = new HashMap<Integer, Zone>();
	public final JobsPluginListener listener;
	public final CompetencesCommandExecutor competencesListener;
	public final Mine mine = new Mine();
	
	public JobsPlugin() throws Throwable
	{
		this.listener = new JobsPluginListener(this);
		this.competencesListener = new CompetencesCommandExecutor(this);
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		builder.setPrettyPrinting();
		builder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
		builder.registerTypeAdapter(Location.class, new LocationTypeAdapter());
		builder.registerTypeAdapter(Skill.class, new SkillTypeAdapter());
		builder.registerTypeAdapter(PhysicalSkill.class, new PhysicalSkillTypeAdapter());
		builder.registerTypeAdapter(PotionEffectType.class, new PotionEffectTypeAdapter());
		builder.registerTypeAdapter(Zone.class, new Zone.ZoneTypeAdapter());
		gson = builder.create();
	}
	
	@Override
	public void onEnable()
	{		
		try
		{
			refreshConfig();
			refreshPlayerJobs();
			refreshPepitesPerDay();
			refreshInfinityItems();
			CompetenceTree.read();
			refreshProtections();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		getServer().getPluginManager().registerEvents(listener, this);
		getServer().getPluginManager().registerEvents(competencesListener, this);
		getCommand("job").setExecutor(new JobCommandExecutor(this));
		getCommand("illegality").setExecutor(new IllegalityCommandExecutor());
		getCommand("production").setExecutor(new ProdCommandExecutor(this));
		getCommand("competences").setExecutor(competencesListener);
		getCommand("forge").setExecutor(new ForgeCommandExecutor());
		getCommand("pattern").setExecutor(new PatternCommandExecutor());
		getCommand("chuchotte").setExecutor(new ChatCommandExecutor(this));
		getCommand("hurle").setExecutor(new ChatCommandExecutor(this));
		getCommand("horsrp").setExecutor(new ChatCommandExecutor(this));
		getCommand("workbench").setExecutor(new WorkbenchCommandExecutor());
		getCommand("infinityitems").setExecutor(new InfinityItemsCommandExecutor(this));
		getCommand("mine").setExecutor(new MineCommandExecutor(this));
		getCommand("protections").setExecutor(new ProtectionsCommandExecutor(this));
		new NightEffectsRunnable().runTaskTimer(this, 0L, 1L);
		
	/*	Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_WEEK, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.WEEK_OF_MONTH, 1);
		Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable()
		{
			@Override
			public void run()
			{
				mine.generate();
			}
		}, (cal.getTimeInMillis() - System.currentTimeMillis()) / 50, 7 * 24 * 60 * 60 * 20);*/
	}
	
	@Override
	public void onDisable()
	{
		try
		{
			writeConfig();
			savePlayerJobs();
			savePepitesPerDay();
			saveInfinityItems();
			CompetenceTree.write();
			saveProtections();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Gson getGson()
	{
		return gson;
	}
	
	public boolean hasJob(Player player)
	{
		if (isCrackedVersion(player))
			return jobsByPlayer.containsKey(player.getName());
		else
			return jobsByPlayer.containsKey(player.getUniqueId().toString());
	}
	
	public boolean hasJob(OfflinePlayer player)
	{
		if (isCrackedVersion(player))
			return jobsByPlayer.containsKey(player.getName());
		else
			return jobsByPlayer.containsKey(player.getUniqueId().toString());
	}
	
	public Job[] getJobs(Player player)
	{
		if (isCrackedVersion(player))
			return jobsByPlayer.get(player.getName());
		else
			return jobsByPlayer.get(player.getUniqueId().toString());
	}
	
	public Job[] getJobs(OfflinePlayer player)
	{
		if (isCrackedVersion(player))
			return jobsByPlayer.get(player.getName());
		else
			return jobsByPlayer.get(player.getUniqueId().toString());
	}
	
	public void setJobs(Player player, Job[] jobs)
	{
		jobs = Arrays.copyOf(jobs, 2);
		if (isCrackedVersion(player))
			jobsByPlayer.put(player.getName(), jobs);
		else
			jobsByPlayer.put(player.getUniqueId().toString(), jobs);
	}
	
	public void setJobs(OfflinePlayer player, Job[] jobs)
	{
		jobs = Arrays.copyOf(jobs, 2);
		if (isCrackedVersion(player))
			jobsByPlayer.put(player.getName(), jobs);
		else
			jobsByPlayer.put(player.getUniqueId().toString(), jobs);
	}
	
	public boolean containsZone(int id)
	{
		return protectionZones.containsKey(Integer.valueOf(id));
	}
	
	public boolean containsZone(Zone zone)
	{
		return containsZone(Validate.notNull(zone).getId());
	}
	
	public Zone getZone(int id)
	{
		return Validate.notNull(protectionZones.get(id));
	}
	
	public Zone removeZone(int id)
	{
		if (!containsZone(id))
			throw new IllegalArgumentException(String.format("Zone id #%d doesn't exist !", id));
		return protectionZones.remove(Integer.valueOf(id));
	}
	
	public Zone removeZone(Zone zone)
	{
		return removeZone(Validate.notNull(zone).getId());
	}
	
	public void addZone(Zone zone)
	{
		if (containsZone(zone))
			throw new IllegalArgumentException(String.format("Zone id #%d already occupied !", zone.getId()));
		protectionZones.put(zone.getId(), zone);
	}
	
	public List<Zone> getZonesInPlace(Location loc)
	{
		List<Zone> zones = new ArrayList<Zone>();
		for (Zone zone : protectionZones.values())
		{
			if (zone.isInZone(loc))
				zones.add(zone);
		}
		return zones;
	}
	
	public List<Zone> getZones()
	{
		return new ArrayList<Zone>(protectionZones.values());
	}
	
	private void refreshConfig()
	{
		reloadConfig();
		YamlConfiguration config = (YamlConfiguration) getConfig();
		ConfigurationSection mineSec = config.getConfigurationSection("mine");
		if (mineSec != null)
		{
			World world = Bukkit.getServer().getWorld(mineSec.getString("world", MinecraftServer.getServer().U()));
			int minX = mineSec.getInt("minX", -1);
			int minY = mineSec.getInt("minY", -1);
			int minZ = mineSec.getInt("minZ", -1);
			int maxX = mineSec.getInt("maxX", -1);
			int maxY = mineSec.getInt("maxY", -1);
			int maxZ = mineSec.getInt("maxZ", -1);
			Location locA = new Location(world, minX, minY, minZ);
			Location locB = new Location(world, maxX, maxY, maxZ);		
			if (!locA.equals(locB) && minY > 0 && maxY > 0)
			{
				mine.setLocA(locA);
				mine.setLocB(locB);
			}
			
			ConfigurationSection signSec;
			if ((signSec = mineSec.getConfigurationSection("coalSign")) != null)
			{
				int x = signSec.getInt("x", -1);
				int y = signSec.getInt("y", -1);
				int z = signSec.getInt("z", -1);
				if (x != -1 && y != -1 && z != -1)
				{
					Location loc = new Location(world, x, y, z);
					mine.setCoalSignLocation(loc);
				}
			}
			
			if ((signSec = mineSec.getConfigurationSection("ironSign")) != null)
			{
				int x = signSec.getInt("x", -1);
				int y = signSec.getInt("y", -1);
				int z = signSec.getInt("z", -1);
				if (x != -1 && y != -1 && z != -1)
				{
					Location loc = new Location(world, x, y, z);
					mine.setIronSignLocation(loc);
				}
			}
			
			if ((signSec = mineSec.getConfigurationSection("goldSign")) != null)
			{
				int x = signSec.getInt("x", -1);
				int y = signSec.getInt("y", -1);
				int z = signSec.getInt("z", -1);
				if (x != -1 && y != -1 && z != -1)
				{
					Location loc = new Location(world, x, y, z);
					mine.setGoldSignLocation(loc);
				}
			}
			
			if ((signSec = mineSec.getConfigurationSection("lapisSign")) != null)
			{
				int x = signSec.getInt("x", -1);
				int y = signSec.getInt("y", -1);
				int z = signSec.getInt("z", -1);
				if (x != -1 && y != -1 && z != -1)
				{
					Location loc = new Location(world, x, y, z);
					mine.setLapisSignLocation(loc);
				}
			}
			
			if ((signSec = mineSec.getConfigurationSection("redstoneSign")) != null)
			{
				int x = signSec.getInt("x", -1);
				int y = signSec.getInt("y", -1);
				int z = signSec.getInt("z", -1);
				if (x != -1 && y != -1 && z != -1)
				{
					Location loc = new Location(world, x, y, z);
					mine.setRedstoneSignLocation(loc);
				}
			}
		}
		
		Zone.getCursorId().set(config.getInt("protections.curosrID", 0));
	}
	
	private void writeConfig()
	{
		YamlConfiguration config = (YamlConfiguration) getConfig();
		ConfigurationSection mineSec = config.createSection("mine");
		if (mine.getLocA() != null && mine.getLocB() != null)
		{
			Location a = mine.getLocA();
			Location b = mine.getLocB();
			mineSec.set("world", a.getWorld().getName());
			mineSec.set("minX", a.getBlockX());
			mineSec.set("minY", a.getBlockY());
			mineSec.set("minZ", a.getBlockZ());
			mineSec.set("maxX", b.getBlockX());
			mineSec.set("maxY", b.getBlockY());
			mineSec.set("maxZ", b.getBlockZ());
			
			ConfigurationSection signSec;
			Location loc;
			if ((loc = mine.getCoalSignLocation()) != null)
			{
				signSec = mineSec.createSection("coalSign");
				signSec.set("x", loc.getBlockX());
				signSec.set("y", loc.getBlockY());
				signSec.set("z", loc.getBlockZ());
			}
			
			if ((loc = mine.getIronSignLocation()) != null)
			{
				signSec = mineSec.createSection("ironSign");
				signSec.set("x", loc.getBlockX());
				signSec.set("y", loc.getBlockY());
				signSec.set("z", loc.getBlockZ());
			}
			
			if ((loc = mine.getGoldSignLocation()) != null)
			{
				signSec = mineSec.createSection("goldSign");
				signSec.set("x", loc.getBlockX());
				signSec.set("y", loc.getBlockY());
				signSec.set("z", loc.getBlockZ());
			}
			
			if ((loc = mine.getLapisSignLocation()) != null)
			{
				signSec = mineSec.createSection("lapisSign");
				signSec.set("x", loc.getBlockX());
				signSec.set("y", loc.getBlockY());
				signSec.set("z", loc.getBlockZ());
			}
			
			if ((loc = mine.getRedstoneSignLocation()) != null)
			{
				signSec = mineSec.createSection("redstoneSign");
				signSec.set("x", loc.getBlockX());
				signSec.set("y", loc.getBlockY());
				signSec.set("z", loc.getBlockZ());
			}
		}
		config.set("protections.cursorID", Zone.getCursorId().get());
		saveConfig();
	}
	
	@SuppressWarnings("deprecation")
	private void refreshPlayerJobs() throws IOException
	{
		File configFile = new File("plugins" + File.separator + "Jobs-RPG", "users.yml");
		if (!configFile.isFile())
		{
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			return;
		}
		if (configFile.length() == 0)
			return;
		YamlConfiguration config = new YamlConfiguration();
		config.options().indent(4);
		config.options().pathSeparator('~');
		try
		{
			config.load(configFile);
		}
		catch (InvalidConfigurationException e)
		{
			e.printStackTrace();
			return;
		}
		if (config.getKeys(false) == null)
			return;
		for (String player : config.getKeys(false))
		{
			String[] arr = config.getStringList(player).toArray(new String[2]);
			if (arr.length > 2)
			{
				arr = Arrays.copyOf(arr, 2);
			}
			else if (arr.length == 1)
			{
				arr = new String[] {arr[0], Job.CHOMEUR.name().toLowerCase()};
			}
			else if (arr.length == 0)
			{
				arr = new String[] {Job.CHOMEUR.name().toLowerCase(), Job.CHOMEUR.name().toLowerCase()};
			}
			
			Job job1 = null, job2 = null;

			try
			{

				job1 = Job.valueOf(arr[0].toUpperCase());
				job2 = Job.valueOf(arr[1].toUpperCase());
			}
			catch (Exception ex)
			{
				throw new IllegalArgumentException("Un job ou les deux sont invalides pour le joueur '" + player + "'", ex);
			}
			
			if (Bukkit.getOfflinePlayer(player) != null)
			{
				try
				{
					UUID uuid = UUID.fromString(player);
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if (isCrackedVersion(op))
					{
						getLogger().warning("'" + op.getName() + "' (UUID : " + uuid + ") got a cracked version, returning to the player name key");
						player = op.getName();
					}
				}
				catch (Throwable t)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(player);
					if (!isCrackedVersion(op.getUniqueId(), op.getName()))
					{
						player = op.getUniqueId().toString();
						getLogger().warning("'" + player + "' got a billed version, using UUID");
					}
				}
			}
			this.jobsByPlayer.put(player, new Job[] {job1, job2});
		}
	}
	
	private void savePlayerJobs() throws IOException
	{
		File configFile = new File("plugins" + File.separator + "Jobs-RPG", "users.yml");
		if (!configFile.isFile())
		{
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			return;
		}
		YamlConfiguration config = new YamlConfiguration();
		config.options().indent(4);
		config.options().pathSeparator('~');
		for (Entry<String, Job[]> player : jobsByPlayer.entrySet())
		{
			List<String> list = new ArrayList<String>();
			list.add(player.getValue()[0].name().toLowerCase());
			list.add(player.getValue()[1].name().toLowerCase());
			config.set(player.getKey(), list);
		}
		config.save(configFile);
	}
	
	private void refreshPepitesPerDay() throws IOException
	{
		File file = new File(MinecraftServer.getServer().U(), "home-money.gz");
		if (!file.isFile())
		{
			file.createNewFile();
			return;
		}
		FileInputStream fis = new FileInputStream(file);
		NBTTagCompound nbt = NBTCompressedStreamTools.a(fis);
		fis.close();
		for (String posStr : nbt.c())
		{
			String[] posSplit = posStr.split(":");
			String worldName = posSplit[0];
			int x = Integer.parseInt(posSplit[1]);
			int y = Integer.parseInt(posSplit[2]);
			int z = Integer.parseInt(posSplit[3]);
			int pepites = nbt.getInt(posStr);
			World world = Bukkit.getServer().getWorld(worldName);
			Location loc = new Location(world, x, y, z);
			pepitesPerDay.put(loc, Integer.valueOf(pepites));
		}
		
		Date now = new Date();
		Calendar midnightCal = new GregorianCalendar();
		midnightCal.setTime(now);
		midnightCal.set(Calendar.HOUR_OF_DAY, 0);
		midnightCal.set(Calendar.MINUTE, 0);
		midnightCal.set(Calendar.SECOND, 0);
		midnightCal.set(Calendar.MILLISECOND, 0);
		midnightCal.add(Calendar.DAY_OF_MONTH, 1);
		Date tomorrow = midnightCal.getTime();
		long time = tomorrow.getTime() - now.getTime();
		time /= 50;
		ProductionRunnable run = new ProductionRunnable(this);
		run.runTaskLater(this, time);
	}
	
	private void savePepitesPerDay() throws IOException
	{
		NBTTagCompound nbt = new NBTTagCompound();
		for (Entry<Location, Integer> entry : pepitesPerDay.entrySet())
		{
			Location loc = entry.getKey();
			String locStr = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
			nbt.setInt(locStr, entry.getValue().intValue());
		}
		FileOutputStream fos = new FileOutputStream(new File(MinecraftServer.getServer().U(), "home-money.gz"));
		NBTCompressedStreamTools.a(nbt, fos);
	}
	
	private void refreshInfinityItems() throws IOException
	{
		File file = new File(MinecraftServer.getServer().U(), "infinity-items.gz");
		if (!file.isFile())
		{
			file.createNewFile();
			return;
		}
		GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
		String json = IOUtils.toString(gis, StandardCharsets.UTF_8);
		gis.close();
		@SuppressWarnings("unchecked")
		Map<String, ?> map = gson.fromJson(json, HashMap.class);
		for (Entry<String, ?> entry : map.entrySet())
		{
			Location key = gson.fromJson(entry.getKey(), Location.class);
			InfinityItem value = gson.fromJson(gson.toJson(entry.getValue()), InfinityItem.class);
			infinityItems.put(key, value);
		}
	}
	
	private void saveInfinityItems() throws IOException
	{
		File file = new File(MinecraftServer.getServer().U(), "infinity-items.gz");
		if (!file.isFile())
			file.createNewFile();
		Map<String, InfinityItem> map = new HashMap<String, InfinityItem>();
		for (Entry<Location, InfinityItem> entry : infinityItems.entrySet())
			map.put(gson.toJson(entry.getKey()), entry.getValue());
		String json = gson.toJson(map);
		GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		gos.write(json.getBytes(StandardCharsets.UTF_8));
		gos.close();
	}
	
	public void refreshProtections() throws IOException
	{
		File file = new File(MinecraftServer.getServer().U(), "zone-protections.gz");
		if (!file.isFile())
			file.createNewFile();
		GZIPInputStream gis = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
		String json = IOUtils.toString(gis, StandardCharsets.UTF_8);
		gis.close();
		Map<Integer, Zone> map = gson.fromJson(json, new TypeToken<HashMap<Integer, Zone>>() {}.getType());
		protectionZones.clear();
		protectionZones.putAll(map);
	}
	
	public  void saveProtections() throws IOException
	{
		String json = gson.toJson(protectionZones);
		File file = new File(MinecraftServer.getServer().U(), "zone-protections.gz");
		if (!file.isFile())
			file.createNewFile();
		GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		gos.write(json.getBytes(StandardCharsets.UTF_8));
		gos.close();
	}
	
	public static boolean isCrackedVersion(Player player)
	{
		Validate.notNull(player);
		return isCrackedVersion(player.getUniqueId(), player.getName());
	}
	
	public static boolean isCrackedVersion(OfflinePlayer player)
	{
		Validate.notNull(player);
		return isCrackedVersion(player.getUniqueId(), player.getName());
	}
	
	public static boolean isCrackedVersion(UUID uuid, String playerName)
	{
		Validate.noNullElements(Arrays.asList(uuid, playerName));
		UUID ifCracked = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
		return uuid.equals(ifCracked);
	}
}
