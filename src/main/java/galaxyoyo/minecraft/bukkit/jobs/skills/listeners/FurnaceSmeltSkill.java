package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

public interface FurnaceSmeltSkill
{
	public void onSmelt(FurnaceSmeltEvent event, Player player);
}
