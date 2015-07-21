package galaxyoyo.minecraft.bukkit.jobs.skills.listeners;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface MobDamageSkill
{
	public void onMobDamage(EntityDamageByEntityEvent event);
}
