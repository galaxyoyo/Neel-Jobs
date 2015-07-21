package galaxyoyo.minecraft.bukkit.jobs;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.primitives.Ints;

public class Mine implements Runnable
{
	private static transient final Logger LOGGER = (Logger) LogManager.getLogger();
	private Location a, b;
	private Location coalSign, ironSign, goldSign, lapisSign, redstoneSign;
	private transient boolean generating;
	
	public Location getLocA()
	{
		return a.clone();
	}
	
	public void setLocA(Location a)
	{
		this.a = a.clone();
	}
	
	public Location getLocB()
	{
		return b.clone();
	}
	
	public void setLocB(Location b)
	{
		this.b = b.clone();
	}
	
	public Location getCoalSignLocation()
	{
		return coalSign == null ? null : coalSign.clone();
	}
	
	public void setCoalSignLocation(Location coalSign)
	{
		coalSign = new Location(coalSign.getWorld(), coalSign.getBlockX(), coalSign.getBlockY(),
						coalSign.getBlockZ());
		if (containsSignLocation(coalSign))
			throw new IllegalArgumentException("Cette pancarte contient d\u00e9j\u00e0 une information");
		this.coalSign = coalSign;
	}
	
	public Location getIronSignLocation()
	{
		return ironSign == null ? null : ironSign.clone();
	}
	
	public void setIronSignLocation(Location ironSign)
	{
		ironSign = new Location(ironSign.getWorld(), ironSign.getBlockX(), ironSign.getBlockY(),
						ironSign.getBlockZ());
		if (containsSignLocation(ironSign))
			throw new IllegalArgumentException("Cette pancarte contient d\u00e9j\u00e0 une information");
		this.ironSign = ironSign;
	}
	
	public Location getGoldSignLocation()
	{
		return goldSign == null ? null :goldSign.clone();
	}
	
	public void setGoldSignLocation(Location goldSign)
	{
		goldSign = new Location(goldSign.getWorld(), goldSign.getBlockX(), goldSign.getBlockY(),
						goldSign.getBlockZ());
		if (containsSignLocation(goldSign))
			throw new IllegalArgumentException("Cette pancarte contient d\u00e9j\u00e0 une information");
		this.goldSign = goldSign;
	}
	
	public Location getLapisSignLocation()
	{
		return lapisSign == null ? null : lapisSign.clone();
	}
	
	public void setLapisSignLocation(Location lapisSign)
	{
		lapisSign = new Location(lapisSign.getWorld(), lapisSign.getBlockX(), lapisSign.getBlockY(),
						lapisSign.getBlockZ());
		if (containsSignLocation(lapisSign))
			throw new IllegalArgumentException("Cette pancarte contient d\u00e9j\u00e0 une information");
		this.lapisSign = lapisSign;
	}
	
	public Location getRedstoneSignLocation()
	{
		return redstoneSign == null ? null : redstoneSign.clone();
	}
	
	public void setRedstoneSignLocation(Location redstoneSign)
	{
		redstoneSign = new Location(redstoneSign.getWorld(), redstoneSign.getBlockX(), redstoneSign.getBlockY(),
						redstoneSign.getBlockZ());
		if (containsSignLocation(redstoneSign))
			throw new IllegalArgumentException("Cette pancarte contient d\u00e9j\u00e0 une information");
		this.redstoneSign = redstoneSign;
	}
	
	public void removeSignLocation(Location loc)
	{
		if (containsSignLocation(loc))
		{
			if (loc.equals(coalSign))
				coalSign = null;
			else if (loc.equals(ironSign))
				ironSign = null;
			else if (loc.equals(goldSign))
				goldSign = null;
			else if (loc.equals(lapisSign))
				lapisSign = null;
			else if (loc.equals(redstoneSign))
				redstoneSign = null;
		}
		else
		{
			throw new IllegalArgumentException("Cette pancarte ne contient aucune information");
		}
	}
	
	public boolean containsSignLocation(Location loc)
	{
		return loc.equals(coalSign) || loc.equals(ironSign) || loc.equals(goldSign) || loc.equals(lapisSign)
						|| loc.equals(redstoneSign);
	}
	
	public boolean isGenerating()
	{
		return generating;
	}
	
	public void generate()
	{
		if (generating)
			return;
		
		if (a == null || b == null)
			return;
		
		if (a.getWorld() != b.getWorld())
			return;
		
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			player.kickPlayer("La mine se r\u00e9g\u00e9n\u00e8re ! D'ici maximum 5 minutes, vous pourrez vous reconnecter.");
		}
		
		Thread thread = new Thread(this);
		thread.setDaemon(false);
		thread.setPriority(8);
		thread.start();
	}
	
	@Deprecated
	public void run()
	{
		bytes = new byte[256 * 1024 * 1024];
		cursor.set(0);
		generating = true;
		int minX = Math.min(a.getBlockX(), b.getBlockX());
		int minY = Math.min(a.getBlockY(), b.getBlockY());
		int minZ = Math.min(a.getBlockZ(), b.getBlockZ());
		int maxX = Math.max(a.getBlockX(), b.getBlockX());
		int maxY = Math.max(a.getBlockY(), b.getBlockY());
		int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());
		
		LOGGER.info("R\u00e9initialisation de la mine");
		for (int x = minX; x <= maxX; ++x)
		{
			for (int y = minY; y <= maxY; ++y)
			{
				for (int z = minZ; z <= maxZ; ++z)
				{
					setType(x, y, z, Material.STONE, (byte) 0x00);
				}
			}
		}
		
		Random random = new Random();
		
		// Generate stones
		final int maxStones = random.nextInt(16000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de roche", maxStones));
		for (int i = 0; i < maxStones; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			byte[] datas = {1, 3, 5};
			byte data = datas[random.nextInt(2)];
			int max = random.nextInt(32) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.STONE, data);
			}
		}

		// Generate dirts
		final int maxDirts = random.nextInt(7000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de terre", maxDirts));
		for (int i = 0; i < maxDirts; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(48) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.DIRT, (byte) 0x00);
			}
		}

		// Generate gravel
		final int maxGravels = random.nextInt(7000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de gravier", maxGravels));
		for (int i = 0; i < maxGravels; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(48) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.GRAVEL, (byte) 0x00);
			}
		}

		// Generate coal ores
		final int maxCoals = random.nextInt(11000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de charbon", maxCoals));
		for (int i = 0; i < maxCoals; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(24) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.COAL_ORE, (byte) 0x00);
			}
		}

		// Generate iron ores
		final int maxIrons = random.nextInt(9000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de fer", maxIrons));
		for (int i = 0; i < maxIrons; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(6) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.IRON_ORE, (byte) 0x00);
			}
		}

		// Generate gold ores
		final int maxGolds = random.nextInt(6000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons d'or", maxGolds));
		for (int i = 0; i < maxGolds; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(4) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.GOLD_ORE, (byte) 0x00);
			}
		}

		// Generate lapis ores
		final int maxLapis = random.nextInt(7000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de lapis", maxLapis));
		for (int i = 0; i < maxLapis; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(4) - 2 + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.LAPIS_ORE, (byte) 0x00);
			}
		}

		// Generate redstone ores
		final int maxRedstone = random.nextInt(7000) + 4000;
		LOGGER.info(String.format("G\u00e9n\u00e9ration des %d filons de redstone", maxRedstone));
		for (int i = 0; i < maxRedstone; ++i)
		{
			int x = random.nextInt(Math.abs(maxX - minX)) + minX;
			int y = random.nextInt(Math.abs(maxY - minY)) + minY;
			int z = random.nextInt(Math.abs(maxZ - minZ)) + minZ;
			int max = random.nextInt(8) + 1;
			for (int j = 0; j < max; ++j)
			{
				int oldX = x, oldY = y, oldZ = z;
				x += random.nextInt(4) - 2;
				y += random.nextInt(4) - 2;
				z += random.nextInt(4) - 2;
				if (x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ)
				{
					x = oldX;
					y = oldY;
					z = oldZ;
					continue;
				}
				setType(x, y, z, Material.REDSTONE_ORE, (byte) 0x00);
			}
		}
		
		Bukkit.getScheduler().runTask(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"), new Runnable()
		{
			@Override
			public void run()
			{
				if (coalSign != null)
				{
					if (coalSign.getBlock().getState() instanceof Sign)
					{
						Sign sign = (Sign) coalSign.getBlock().getState();
						sign.setLine(0, ChatColor.BLACK + "" + maxCoals);
						sign.setLine(1, ChatColor.DARK_GRAY + "filons de");
						sign.setLine(2, ChatColor.BLACK + "charbon");
						sign.setLine(3, "");
						sign.update(true, true);
					}
					else
					{
						LOGGER.warn("Location du panneau d'affichages des filons de charbon inexistant");
						coalSign = null;
					}
				}
				
				if (ironSign != null)
				{
					if (ironSign.getBlock().getState() instanceof Sign)
					{
						Sign sign = (Sign) ironSign.getBlock().getState();
						sign.setLine(0, ChatColor.GRAY + "" + maxIrons);
						sign.setLine(1, ChatColor.DARK_GRAY + "filons de");
						sign.setLine(2, ChatColor.GRAY + "fer");
						sign.setLine(3, "");
						sign.update(true, true);
					}
					else
					{
						LOGGER.warn("Location du panneau d'affichages des filons de fer inexistant");
						ironSign = null;
					}
				}
				
				if (goldSign != null)
				{
					if (goldSign.getBlock().getState() instanceof Sign)
					{
						Sign sign = (Sign) goldSign.getBlock().getState();
						sign.setLine(0, ChatColor.GOLD + "" + maxGolds);
						sign.setLine(1, ChatColor.DARK_GRAY + "filons");
						sign.setLine(2, "d'" + ChatColor.GOLD + "or");
						sign.setLine(3, "");
						sign.update(true, true);
					}
					else
					{
						LOGGER.warn("Location du panneau d'affichages des filons d'or inexistant");
						goldSign = null;
					}
				}
				
				if (lapisSign != null)
				{
					if (lapisSign.getBlock().getState() instanceof Sign)
					{
						Sign sign = (Sign) lapisSign.getBlock().getState();
						sign.setLine(0, ChatColor.DARK_BLUE + "" + maxLapis);
						sign.setLine(1, ChatColor.DARK_GRAY + "filons de");
						sign.setLine(2, ChatColor.DARK_BLUE + "lapis");
						sign.setLine(3, "");
						sign.update(true, true);
					}
					else
					{
						LOGGER.warn("Location du panneau d'affichages des filons de lapis inexistant");
						lapisSign = null;
					}
				}
				
				if (redstoneSign != null)
				{
					if (redstoneSign.getBlock().getState() instanceof Sign)
					{
						Sign sign = (Sign) redstoneSign.getBlock().getState();
						sign.setLine(0, ChatColor.DARK_RED + "" + maxRedstone);
						sign.setLine(1, ChatColor.DARK_GRAY + "filons de");
						sign.setLine(2, ChatColor.DARK_RED + "redstone");
						sign.setLine(3, "");
						sign.update(true, true);
					}
					else
					{
						LOGGER.warn("Location du panneau d'affichages des filons de redstone inexistant");
						redstoneSign = null;
					}
				}
			}
		});
		
		bytes = Arrays.copyOf(bytes, cursor.get());
		
		LOGGER.info("G\u00e9n\u00e9ration termin\u00e9, application en cours (" + bytes.length / 15 + " blocs)");
		
		System.gc();
		
		final IS is = new IS();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				short s = 0;
				while (s < 2048)
				{
					try
					{
						if (is.available() < 15)
						{
							cancel();
							is.close();
							generating = false;
							LOGGER.info("Mine reg\u00e9n\u00e9r\u00e9e !");
							return;
						}
						
						int x = is.readInt();
						int y = is.readInt();
						int z = is.readInt();
						int matId = is.readUnsignedShort();
						Material material = Material.getMaterial(matId);
						byte data = is.readByte();
						Block block = a.getWorld().getBlockAt(x, y, z);
						if (block.getType() != material)
						{
							--s;
							block.setType(material);
						}
						if (block.getData() != data)
							block.setData(data);
					}
					catch (Throwable t)
					{
						t.printStackTrace();
					}
					++s;
				}
			}
		}.runTaskTimer(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"), 0L, 0L);
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (!generating)
				{
					cancel();
					return;
				}
				
				LOGGER.info(String.format("Reste %s blocs ...", NumberFormat.getNumberInstance().format(is.available() / 15).replaceAll(" ", " ")));
			}
		}.runTaskTimer(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"), 20L, 20L);
	}
	
	private byte[] bytes = new byte[0];
	private AtomicInteger cursor = new AtomicInteger();
	
	@SuppressWarnings("deprecation")
	public void setType(int x, int y, int z, Material material, byte data)
	{
		byte[] bytes = new byte[15];
		bytes[0] = (byte) (x >> 24 & 0xFF);
		bytes[1] = (byte) (x >> 16 & 0xFF);
		bytes[2] = (byte) (x >> 8 & 0xFF);
		bytes[3] = (byte) (x & 0xFF);
		bytes[4] = (byte) (y >> 24 & 0xFF);
		bytes[5] = (byte) (y >> 16 & 0xFF);
		bytes[6] = (byte) (y >> 8 & 0xFF);
		bytes[7] = (byte) (y & 0xFF);
		bytes[8] = (byte) (z >> 24 & 0xFF);
		bytes[9] = (byte) (z >> 16 & 0xFF);
		bytes[10] = (byte) (z >> 8 & 0xFF);
		bytes[11] = (byte) (z & 0xFF);
		bytes[12] = (byte) (material.getId() >> 8 & 0xFF);
		bytes[13] = (byte) (material.getId() & 0xFF);
		bytes[14] = data;
		write(bytes);
	}
	
	public void write(byte[] data)
	{
		if (cursor.get() + data.length > bytes.length)
			bytes = Arrays.copyOf(bytes, bytes.length + data.length);
		for (int i = 0; i < data.length; ++i)
			bytes[cursor.getAndIncrement()] = data[i];
	}
	
	class IS extends InputStream
	{
		private AtomicInteger cursor = new AtomicInteger();
		
		@Override
		public int read()
		{
			if (available() <= 0)
				throw new IndexOutOfBoundsException(String.valueOf(available()));
			byte b = bytes[cursor.getAndIncrement()];
			
			return (int) b;
		}
		
		@Override
		public int read(byte[] b)
		{
			return read(b, 0, b.length);
		}
		
		@Override
		public int read(byte[] bs, int off, int len)
		{
			if (bs == null)
				throw new NullPointerException();
			if (off < 0)
				throw new IllegalArgumentException("Offset can't be less than 0");
			if (off > available())
				throw new IllegalArgumentException("Offset can't be more than readables bytes");
			if (bs.length < len)
				throw new IllegalArgumentException("Array size < expected length");
			if (len > available())
				throw new IllegalArgumentException("Can't read " + len + " bytes (available bytes : " + available() + ")");
			int length = 0;
			for (int i = 0; i < len; ++i)
			{
				length++;
				byte b = bytes[cursor.get() + i];
				bs[off + i] = b;
			}
			
			cursor.addAndGet(length);
			
			return length;
		}
		
		@Override
		public int available()
		{
			return bytes.length - cursor.get();
		}
		
		@Override
		public void close()
		{
			cursor.set(0);
			Mine.this.cursor.set(0);
			bytes = new byte[0];
		}
		
		@Override
		public void reset()
		{
			cursor.set(0);
		}
		
		public int readInt()
		{
			byte[] b = new byte[4];
			read(b, 0, 4);
			return Ints.fromByteArray(b);
		}
		
		public byte readByte()
		{
			byte[] b = new byte[1];
			read(b, 0, 1);
			return b[0];
		}
		
		public int readUnsignedShort()
		{
			byte[] b = new byte[4];
			read(b, 2, 2);
			return Ints.fromByteArray(b);
		}
	}
}
