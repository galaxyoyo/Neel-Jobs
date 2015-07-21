package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBrokenSkill;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class PlantsDropSkill extends Skill implements BlockBrokenSkill
{
	private static transient Random random = new Random();
	
	public PlantsDropSkill(String id)
	{
		super (id);
	}
	
	protected PlantsDropSkill()
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
		return "G\u00e9n\u00e8re plus de bl\u00e9 / carottes / pommes de terre au minage";
	}
	
	@Override
	public Skill clone()
	{
		return new PlantsDropSkill(getId());
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
		if ((event.getBlock().getType() == Material.CROPS
				|| event.getBlock().getType() == Material.CARROT
				|| event.getBlock().getType() == Material.POTATO) && event.getBlock().getData() == 7)
		{
			if (random.nextInt(3) == 1)
			{
				CraftPlayer player = (CraftPlayer) event.getPlayer();
				Location loc = event.getBlock().getLocation();
				Material mat = null;
				switch (event.getBlock().getType())
				{
				case CROPS:
					mat = Material.WHEAT;
					break;
				case CARROT:
					mat = Material.CARROT_ITEM;
					break;
				case POTATO:
					mat = Material.POTATO_ITEM;
					break;
				default:
					break;
				}
				player.getWorld().dropItem(loc, new ItemStack(mat, random.nextInt(2) + 1));
			}
		}
	}
}
