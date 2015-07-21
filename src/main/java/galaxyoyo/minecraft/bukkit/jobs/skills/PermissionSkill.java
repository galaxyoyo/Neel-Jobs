package galaxyoyo.minecraft.bukkit.jobs.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionSkill extends Skill
{
	private String permission;
	
	public PermissionSkill(String perm, String id)
	{
		super (id);
		this.permission = perm;
	}
	
	protected PermissionSkill()
	{
		super ();
	}
	
	@Override
	public void execute(Player player)
	{
		player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"), permission, true);
	}
	
	@Override
	public void reset(Player player)
	{
		player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"), permission, false);
	}
	
	@Override
	public String getDescription()
	{
		return "Donne la permission " + permission;
	}
	
	@Override
	public Skill clone()
	{
		return new PermissionSkill(permission, getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && permission.equalsIgnoreCase(((PermissionSkill) skill).permission);
	}
	
}
