package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public interface FurnaceBurnSkill
{
	public void onFurnaceBurn(FurnaceBurnEvent event, Player player);
}
