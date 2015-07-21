package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.event.block.BlockDamageEvent;

public interface BlockBreakingSkill
{
	public void onBlockBreaking(BlockDamageEvent event);
}
