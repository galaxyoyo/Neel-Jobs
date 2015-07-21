package galaxyoyo.minecraft.bukkit.jobs.skills;

import org.bukkit.entity.Player;

public class HealthSkill extends Skill
{
	private double healthBonus;
	
	public HealthSkill(double healthBonus, String id)
	{
		super (0, id);
		this.healthBonus = healthBonus;
	}
	
	protected HealthSkill()
	{
		super ();
	}
	
	@Override
	public void execute(Player player)
	{
		player.setMaxHealth(player.getMaxHealth() + healthBonus);
		if (player.getHealth() == 20.0D)
			player.setHealth(player.getMaxHealth());
	}

	@Override
	public void reset(Player player)
	{
		player.setMaxHealth(20.0D);
	}
	
	@Override
	public String getDescription()
	{
		return "+ " + (int) getHealthBonus() + " points de vie";
	}
	
	public double getHealthBonus()
	{
		return healthBonus;
	}

	@Override
	public Skill clone()
	{
		Skill skill = new HealthSkill(getHealthBonus(), getId());
		skill.setActive(isActive());
		return skill;
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return skill.getClass() == getClass() && isActive() == skill.isActive()
				&& getBaseCooldown() == skill.getBaseCooldown()
				&& getHealthBonus() == ((HealthSkill) skill).getHealthBonus();
	}
}
