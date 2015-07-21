package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBrokenSkill;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CropsDropSkill extends Skill implements BlockBrokenSkill
{
	private static transient Random random = new Random();
	
	public CropsDropSkill(String id)
	{
		super (id);
	}
	
	protected CropsDropSkill()
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
		return "G\u00e9n\u00e8re plus de graines au minage";
	}
	
	@Override
	public Skill clone()
	{
		return new CropsDropSkill();
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBlockBroken(BlockBreakEvent event)
	{
		if (event.getBlock().getType() == Material.CROPS && event.getBlock().getData() == 7)
		{
			if (random.nextInt(3) == 1)
			{
				CraftPlayer player = (CraftPlayer) event.getPlayer();
				Location loc = event.getBlock().getLocation();
				player.getWorld().dropItem(loc, new ItemStack(Material.SEEDS, random.nextInt(2) + 1));
			}
		}
	}
}
