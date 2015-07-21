package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBrokenSkill;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ZonedMiningSkill extends Skill implements BlockBrokenSkill
{
	public ZonedMiningSkill(int cooldown, String id)
	{
		super (cooldown, id);
	}
	
	protected ZonedMiningSkill()
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
		return "Pour un bloc min\u00e9, tous ceux autour m\u00eame en diagonale\n\r"
				+ "sont \u00e9galement d\u00e9truit sans utilisation de durabilit\u00e9.\n\r"
				+ "Activation : clic droit avec pioche";
	}

	@Override
	public Skill clone()
	{
		return new ZonedMiningSkill(getBaseCooldown(), getId());
	}

	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown();
	}
	
	@Override
	public void onBlockBroken(BlockBreakEvent event)
	{
		if (!isActivated())
			return;
		
		if (getCooldown() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Veuillez attendre " + getFormattedTime(getCooldown()) + " avant de pouvoir faire cela \u00e0 nouveau.");
			return;
		}
		
		BlockFace[] faces = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
				BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.SELF};
		for (BlockFace face : faces)
		{
			for (BlockFace face2 : new BlockFace[] {BlockFace.UP, BlockFace.DOWN})
			{
				event.getBlock().getRelative(face).getRelative(face2).breakNaturally();
			}
			event.getBlock().getRelative(face).breakNaturally();
		}
		
		desactivate();
		startCooldown();
	}

}
