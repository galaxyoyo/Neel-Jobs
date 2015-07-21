package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.PhysicalCompetences;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBrokenSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.EntityDamageSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.MobDamageSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerMoveSkill;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class PhysicalSkill extends Skill implements PlayerMoveSkill, EntityDamageSkill, MobDamageSkill, BlockBrokenSkill
{
	private transient PhysicalCompetences tree;
	private Skill parent;
	private String listenerClass;
	private double points = 0.0D;
	private int maxPoints;
	private int id;
	
	public PhysicalSkill(Skill parent, Class<?> listener, int maxPoints, int id)
	{
		this (parent, listener.getName(), maxPoints, id);
	}
	
	public PhysicalSkill(Skill parent, String listenerClass, int maxPoints, int id)
	{
		this.parent = parent;
		this.listenerClass = listenerClass;
		this.maxPoints = maxPoints;
		this.id = id;
		setActive(false);
		desactivate();
	}
	
	protected PhysicalSkill()
	{
		super ();
	}
	
	public PhysicalCompetences getTree()
	{
		return tree;
	}
	
	public PhysicalSkill setTree(PhysicalCompetences tree)
	{
		this.tree = tree;
		return this;
	}
	
	@Override
	public void execute(Player player)
	{
		if (isActivated() && isActive())
			parent.execute(player);
		else
			reset(player);
	}
	
	@Override
	public void reset(Player player)
	{
		parent.reset(player);
	}
	
	@Override
	public String getDescription()
	{
		String ptsStr = NumberFormat.getNumberInstance(Locale.FRANCE).format((int) points).replaceAll(" ", " ");
		String maxPtsStr = NumberFormat.getNumberInstance(Locale.FRANCE).format(maxPoints).replaceAll(" ", " ");
		String desc = ChatColor.LIGHT_PURPLE + parent.getDescription() + "\n\r";
		desc += ChatColor.GOLD + "Pour l'obtenir : ";
		if (parent instanceof BootsEnchantmentSkill)
		{
			if (listenerClass.equalsIgnoreCase(PlayerMoveSkill.class.getName()))
				desc += "Nager " + maxPtsStr + " blocs";
			else if (listenerClass.equalsIgnoreCase(EntityDamageSkill.class.getName()))
				desc += "Prendre " + maxPtsStr + " d\u00e9gâts de chute";
		}
		else if (listenerClass.equalsIgnoreCase(PlayerMoveSkill.class.getName()))
		{
			if (((EffectSkill) parent).getPotionEffect().getType() == PotionEffectType.SPEED)
				desc += "Marcher " + maxPtsStr + " blocs";
			else
				desc += "Sauter " + maxPtsStr + " fois";
		}
		else if (listenerClass.equalsIgnoreCase(EntityDamageSkill.class.getName()))
		{
			desc += "Prendre " + maxPtsStr + " coeurs de d\u00e9gâts de noyade";
		}
		else if (listenerClass.equalsIgnoreCase(MobDamageSkill.class.getName()))
		{
			desc += "Infliger " + maxPtsStr + " points de\n\r" + ChatColor.GOLD + "d\u00e9gâts \u00e0 n'importe quel mob";
		}
		else if (listenerClass.equalsIgnoreCase(BlockBrokenSkill.class.getName()))
		{
			desc += "Miner " + maxPtsStr + " blocs";
		}
		
		desc += "\n\r";
		if (points < maxPoints)
			desc += ChatColor.YELLOW + "Progr\u00e8s : " + ptsStr + " / " + maxPtsStr;
		else
			desc += ChatColor.GREEN + "Comp\u00e9tence obtenue !";
		
		return desc;
	}
	
	public int getPhysicalId()
	{
		return id;
	}
	
	public String getId()
	{
		return String.valueOf(id);
	}
	
	public int getPoints()
	{
		return (int) points;
	}
	
	public void setPoints(double points)
	{
		this.points = points;
		if (points >= maxPoints)
			setActive(true);
		else
		{
			setActive(false);
			desactivate();
		}
	}
	
	public int getMaxPoints()
	{
		return maxPoints;
	}
	
	public String getListenerClassName()
	{
		return listenerClass;
	}
	
	@Override
	public Skill clone()
	{
		PhysicalSkill skill = new PhysicalSkill(parent.clone(), listenerClass, maxPoints, id);
		return skill;
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && parent.equals(skill);
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (points < maxPoints)
		{
			if (event.getPlayer().getLocation().getBlock().getType() == Material.AIR && parent instanceof EffectSkill)
			{
				Location from = event.getFrom();
				Location to = event.getTo();
				if (parent instanceof EffectSkill && ((EffectSkill) parent).getPotionEffect().getType() == PotionEffectType.JUMP)
				{
					if (from.getY() < to.getY() && to.getBlockY() == from.getBlockY() + 1 && to.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
						points++;
				}
				else if (parent instanceof EffectSkill && ((EffectSkill) parent).getPotionEffect().getType() == PotionEffectType.SPEED)
				{
					points += from.distance(new Location(to.getWorld(), to.getX(), from.getY(), to.getZ()));
				}
			}
			else if (event.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER && parent instanceof BootsEnchantmentSkill)
			{
				points += event.getFrom().distance(event.getTo());
			}
		}
		else
		{
			setActive(true);
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			if (points < maxPoints)
			{
				if (event.getCause() == DamageCause.DROWNING && parent instanceof EffectSkill)
				{
					points++;
				}
				else if (event.getCause() == DamageCause.FALL && parent instanceof BootsEnchantmentSkill)
				{
					points += event.getDamage();
				}
			}
			else
			{
				setActive(true);
			}
		}
	}

	@Override
	public void onMobDamage(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			if (points < maxPoints)
			{
				points += event.getDamage();
			}
			else
			{
				setActive(true);
			}
		}
	}

	@Override
	public void onBlockBroken(BlockBreakEvent event)
	{
		Block block = CraftMagicNumbers.getBlock(event.getBlock());
		BlockPosition pos = new BlockPosition(event.getBlock().getLocation().getX(), event.getBlock().getLocation().getY(), event.getBlock().getLocation().getZ());
		World world = ((CraftWorld) event.getBlock().getWorld()).getHandle();
		if (block.g(world, pos) <= 0.0F)
			return;
		
		if (points < maxPoints)
		{
			points++;
		}
		else
		{
			setActive(true);
		}
	}
}
