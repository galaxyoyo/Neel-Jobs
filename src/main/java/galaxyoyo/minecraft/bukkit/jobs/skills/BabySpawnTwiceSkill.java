package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.MobSpawnSkill;

import java.util.Random;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class BabySpawnTwiceSkill extends Skill implements MobSpawnSkill
{
	private static transient final Random random = new Random();
	
	public BabySpawnTwiceSkill(String id)
	{
		super (id);
	}
	
	protected BabySpawnTwiceSkill()
	{
		super ();
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
		return "Une reproduction a 50 % de chance de produire des jumeaux";
	}
	
	@Override
	public Skill clone()
	{
		return new BabySpawnTwiceSkill(getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass();
	}

	@Override
	public void onMobSpawned(CreatureSpawnEvent event, Player player)
	{
		if (event.getSpawnReason() == SpawnReason.BREEDING)
		{
			if (random.nextInt(2) == 0)
			{
				if (!Animals.class.isAssignableFrom(event.getEntityType().getEntityClass()))
					return;
				
				Entity e = event.getEntity().getWorld().spawnEntity(event.getLocation(), event.getEntityType());
				if (e instanceof Animals)
					((Animals) e).setBaby();
			}
		}
	}
}
