package galaxyoyo.minecraft.bukkit.jobs.skills;

import org.bukkit.entity.Player;

public class BasicSkill extends Skill
{
	private String description;
	
	public BasicSkill(int cooldown, String desc, String id)
	{
		super (cooldown, id);
		this.description = desc;
	}
	
	protected BasicSkill()
	{
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
		return description;
	}
	
	@Override
	public Skill clone()
	{
		return new BasicSkill(getBaseCooldown(), getId(), getDescription());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown()
						&& getDescription().equalsIgnoreCase(skill.getDescription());
	}
}
