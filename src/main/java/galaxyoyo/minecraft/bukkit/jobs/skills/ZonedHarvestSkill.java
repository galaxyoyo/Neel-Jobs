package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.CompetenceTree;
import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractSkill;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ZonedHarvestSkill extends Skill implements PlayerInteractSkill
{
	private boolean five;
	
	public ZonedHarvestSkill(boolean five, int cooldown, String id)
	{
		super (cooldown, id);
		this.five = five;
		setActive(true);
	}
	
	protected ZonedHarvestSkill()
	{
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
		return "Avec un clic gauche de houe, une surface de " + (five ? "5x5" : "3x3") + " est fertilis\u00e9";
	}

	@Override
	public Skill clone()
	{
		return new ZonedHarvestSkill(five, getBaseCooldown(), getId());
	}

	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown()
				&& five == ((ZonedHarvestSkill) skill).five;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.LEFT_CLICK_BLOCK || event.getItem() == null)
			return;
		
		
		if (CompetenceTree.get(event.getPlayer()).getActiveSkills(Job.FARMER).size() > 2 && !five)
			return;
		
		switch (event.getItem().getType())
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
		
		Block target = event.getPlayer().getTargetBlock((Set<Material>) null, 5);
		if (target == null || (target.getType() != Material.DIRT && target.getType() != Material.GRASS
				&& target.getType() != Material.SOIL))
			return;
		
		if (getCooldown() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Veuillez attendre " + getFormattedTime(getCooldown()) + " avant de pouvoir faire cela \u00e0 nouveau.");
			return;
		}
		
		BlockFace[] faces = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
				BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.SELF};
		for (BlockFace face : Arrays.asList(faces))
		{
			Block b = target.getRelative(face);
			if (b.getType() == Material.DIRT || b.getType() == Material.GRASS || b.getType() == Material.SOIL)
			{
				b.setType(Material.SOIL);
				b.setData((byte) 7);
			}
			
			if (five)
			{
				for (BlockFace f : Arrays.asList(faces))
				{
					Block b_ = b.getRelative(f);
					if (b_.getType() == Material.DIRT || b_.getType() == Material.GRASS)
					{
						b_.setType(Material.SOIL);
						b_.setData((byte) 7);
					}
				}
			}
		}
		
		desactivate();
		startCooldown();
	}

}
