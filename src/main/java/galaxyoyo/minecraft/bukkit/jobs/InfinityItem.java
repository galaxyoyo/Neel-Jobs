package galaxyoyo.minecraft.bukkit.jobs;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfinityItem
{
	private transient final JobsPlugin plugin = (JobsPlugin) Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG");
	private Location location;
	private ItemStack stack;
	private SecureType secure;
	private Job job;
	private String perm;
	private Location clearZone;
	private int clearRadius;
	
	public InfinityItem(ItemStack item, Location loc, Job job)
	{
		ItemStack stack = item.clone();
		stack.setAmount(1);
		this.stack = stack;
		this.location = loc;
		this.secure = SecureType.JOB;
		this.job = job;
		this.perm = null;
	}

	public InfinityItem(ItemStack item, Location loc, String perm)
	{
		ItemStack stack = item.clone();
		stack.setAmount(1);
		this.stack = stack;
		this.location = loc;
		this.secure = SecureType.PERMISSION;
		this.job = null;
		this.perm = perm;
	}
	public InfinityItem(ItemStack item, Location loc)
	{
		ItemStack stack = item.clone();
		stack.setAmount(1);
		this.stack = stack;
		this.location = loc;
		this.secure = SecureType.ALL;
		this.job = null;
		this.perm = null;
	}
	
	protected InfinityItem()
	{
	}
	
	public ItemStack getItem()
	{
		return stack;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public SecureType getSecureType()
	{
		return secure;
	}
	
	public Job getAcceptedJob()
	{
		if (secure != SecureType.JOB)
			throw new IllegalArgumentException("Secure isn't for jobs");
		return job;
	}
	
	public String getPermission()
	{
		if (secure != SecureType.PERMISSION)
			throw new IllegalArgumentException("Secure isn't a permission");
		return perm;
	}
	
	public Location getClearZone()
	{
		return clearZone;
	}

	public void setClearZone(Location loc)
	{
		this.clearZone = loc;
	}
	
	public int getClearRadius()
	{
		return clearRadius;
	}
	
	public void setClearRadius(int radius)
	{
		this.clearRadius = radius;
	}
	
	public boolean isAllowed(Player player)
	{
		switch (secure)
		{
			case ALL :
				return true;
			case JOB :
				return Arrays.asList(plugin.getJobs(player)).contains(job);
			case PERMISSION :
				return player.hasPermission(perm);
		}
		
		return false;
	}
	
	private static enum SecureType
	{
		JOB, PERMISSION, ALL;
	}
}
