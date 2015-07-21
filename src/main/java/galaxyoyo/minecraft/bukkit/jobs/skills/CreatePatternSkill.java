package galaxyoyo.minecraft.bukkit.jobs.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CreatePatternSkill extends Skill
{
	public CreatePatternSkill(int cooldown, String id)
	{
		super (cooldown, id);
	}
	
	protected CreatePatternSkill()
	{
		super ();
	}
	
	@Override
	public void execute(Player player)
	{
		player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"),
			"jobsrpg.pattern.create", true);
	}
	
	@Override
	public void reset(Player player)
	{
		player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"),
			"jobsrpg.pattern.create", false);
	}
	
	@Override
	public String getDescription()
	{
		return "Permet de cr\u00e9er un pattern en faisant un /pattern create";
	}
	
	@Override
	public Skill clone()
	{
		return new CreatePatternSkill(getBaseCooldown(), getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown();
	}
}
