package galaxyoyo.minecraft.bukkit.jobs.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CompassMobSkill extends Skill
{
	public CompassMobSkill(int cooldown, String id)
	{
		super (cooldown, id);
	}
	
	protected CompassMobSkill()
	{
		super ();
	}
	
	@Override
	public void execute(Player player)
	{
		if (!isActivated())
			return;
		
		if (getCooldown() > 0)
		{
			player.sendMessage(ChatColor.RED + "Veuillez patienter " + getFormattedTime(getCooldown()) + " avant de faire ceci.");
			return;
		}
		
		List<Entity> entities = new ArrayList<Entity>();
		LivingEntity nearest = null;
		double r = 1.0D;
		while (nearest == null)
		{
			entities = player.getNearbyEntities(r, r, r);
			int i = 0;
			Entity e = null;
			while (i < entities.size())
			{
				e = entities.get(i);
				if (e instanceof LivingEntity && !(e instanceof Player))
					nearest = (LivingEntity) e;
				++i;
			}
			++r;
		}
		player.setCompassTarget(nearest.getLocation());
		player.sendMessage("Un mob a \u00e9t\u00e9 trouv\u00e9 \u00e0 "
				+ (int) nearest.getLocation().distance(player.getLocation()) + " blocs de votre position, "
				+ "utilisez votre boussole (si vous en avez une)");
		
		desactivate();
		startCooldown();
	}
	
	@Override
	public void reset(Player player)
	{
	}
	
	@Override
	public String getDescription()
	{
		return "Pointe votre boussole vers le mob non-joueur le plus proche\n\rActivation : deux clic droits avec une fl\u00e8che";
	}
	
	@Override
	public Skill clone()
	{
		Skill skill = new CompassMobSkill(getBaseCooldown(), getId());
		skill.setActive(isActive());
		return skill;
	}

	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown();
	}
}
