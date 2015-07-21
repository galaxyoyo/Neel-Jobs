package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public interface MobSpawnSkill
{
	public void onMobSpawned(CreatureSpawnEvent event, Player player);
}
