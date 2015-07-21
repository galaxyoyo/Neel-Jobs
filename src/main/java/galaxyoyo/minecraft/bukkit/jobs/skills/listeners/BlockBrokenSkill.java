package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBrokenSkill
{
	public void onBlockBroken(BlockBreakEvent event);
}
