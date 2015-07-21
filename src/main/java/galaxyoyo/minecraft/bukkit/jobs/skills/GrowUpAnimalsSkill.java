package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractEntitySkill;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class GrowUpAnimalsSkill extends Skill implements PlayerInteractEntitySkill
{
	private double x, z;
	private int count;
	
	public GrowUpAnimalsSkill(double x, double z, int count, int cooldown, String id)
	{
		super (cooldown, id);
		this.x = x;
		this.z = z;
		this.count = count;
		setActive(true);
	}
	
	protected GrowUpAnimalsSkill()
	{
		super ();
	}
	
	@Override
	public void execute(Player player)
	{
	}
	
	@Override
	public void reset(Player player)
	{
	}
	
	@Override
	public String getDescription()
	{
		return "Fait grandir instantan\u00e9mnt " + count + " b\u00e9b\u00e9s dans une zone de " + (int) x + "x" + (int) z;
	}
	
	@Override
	public Skill clone()
	{
		return new GrowUpAnimalsSkill(x, z, count, getBaseCooldown(), getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		if (getClass() != skill.getClass() || getBaseCooldown() != skill.getBaseCooldown())
			return false;
		
		GrowUpAnimalsSkill s = (GrowUpAnimalsSkill) skill;
		return x == s.x && z == s.z && count == s.count;
	}
	
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		if (player.getItemInHand() != null)
		{
			switch (player.getItemInHand().getType())
			{
			case WOOD_HOE:
			case STONE_HOE:
			case IRON_HOE:
			case GOLD_HOE:
			case DIAMOND_HOE:
				break;
			default:
				return;
			}
			
			Entity ent = event.getRightClicked();
			if (!(ent instanceof Animals))
				return;
			Animals a1 = (Animals) ent;
			if (a1.isAdult())
				return;
			
			if (getCooldown() > 0)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "Veuillez attendre " + getFormattedTime(getCooldown()) + " avant de pouvoir faire cela \u00e0 nouveau.");
				return;
			}
			
			List<Entity> entities = a1.getNearbyEntities(x, x, z);
			int i = 0;
			int j = count - 1;
			while (i < entities.size() && j > 0)
			{
				Entity e = entities.get(i);
				if (e instanceof Animals)
				{
					if (!((Animals) e).isAdult())
					{
						((Animals) e).setAdult();
						--j;
					}
				}
				++i;
			}
			
			startCooldown();
		}
	}
}
