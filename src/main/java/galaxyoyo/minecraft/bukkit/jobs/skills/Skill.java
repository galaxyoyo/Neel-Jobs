package galaxyoyo.minecraft.bukkit.jobs.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Skill implements Cloneable
{
	private String className;
	private int baseCooldown;
	private String id;
	private int cooldown = 0;
	private boolean active;
	private boolean activated = false;
	
	public Skill(int baseCooldown, String id)
	{
		this.baseCooldown = baseCooldown;
		this.id = id;
		this.className = getClass().getName();
	}
	
	public Skill(String id)
	{
		this (0, id);
	}
	
	public Skill()
	{
		this (0, "");
	}
	
	public abstract void execute(Player player);
	
	public abstract void reset(Player player);
	
	public abstract String getDescription();
	
	public boolean isActive()
	{
		return active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public int getCooldown()
	{
		return cooldown;
	}
	
	public float getCooldownInSecs()
	{
		return (float) cooldown / 20.0F;
	}
	
	public String getFormattedTime(int ticks)
	{
		int seconds = ticks / 20;
		int minutes = seconds / 60;
		seconds -= minutes * 60;
		int hours = minutes / 60;
		minutes -= hours * 60;
		int days = hours / 24;
		hours -= days * 24;
		List<String> list = new ArrayList<String>();
		
		if (days > 0)
		{
			list.add(days + " jour" + (days > 1 ? "s" : ""));
		}
		if (hours > 0)
		{
			list.add(hours + " heure" + (hours > 1 ? "s" : ""));
		}
		if (minutes > 0)
		{
			list.add(minutes + " minute" + (minutes > 1 ? "s" : ""));
		}
		if (seconds > 0)
		{
			list.add(seconds + " seconde" + (seconds > 1 ? "s" : ""));
		}
		
		String str = Strings.join(list, ", ");
		return str.isEmpty() ? ticks + " ticks" : str;
	}
	
	public int getBaseCooldown()
	{
		return baseCooldown;
	}
	
	public void setBaseCooldown(int cooldown)
	{
		this.baseCooldown = cooldown;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void setCooldown(int cooldown)
	{
		this.cooldown = cooldown;
	}
	
	public void startCooldown()
	{
		BukkitRunnable run = new CooldownRunnable();
		setCooldown(baseCooldown);
		run.runTaskTimer(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"), 0L, 1L);
	}
	
	public boolean isActivated()
	{
		return activated;
	}
	
	public void activate()
	{
		activated = true;
	}
	
	public void desactivate()
	{
		activated = false;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public abstract Skill clone();
	
	public abstract boolean equals(Skill skill);
	
	public class CooldownRunnable extends BukkitRunnable
	{
		@Override
		public void run()
		{
			--cooldown;
			if (cooldown == -1)
			{
				cancel();
			}
		}
	}
}
