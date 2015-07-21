package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.event.block.BlockDamageEvent;

public interface BlockStopBreakingSkill
{
	public void onBlockStopBreaking(BlockDamageEvent event);
}
