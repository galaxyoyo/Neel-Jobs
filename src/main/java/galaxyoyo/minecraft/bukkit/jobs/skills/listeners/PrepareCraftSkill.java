package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.event.inventory.PrepareItemCraftEvent;

public interface PrepareCraftSkill
{
	public void onPrepareCraft(PrepareItemCraftEvent event);
}
