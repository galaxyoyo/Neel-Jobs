package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PrepareCraftSkill;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class ChangeLogRecipeSkill extends Skill implements PrepareCraftSkill
{
	public ChangeLogRecipeSkill(String id)
	{
		super (id);
	}
	
	protected ChangeLogRecipeSkill()
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
		return "Une bûche = 6 planches";
	}
	
	@Override
	public Skill clone()
	{
		return new ChangeLogRecipeSkill(getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass();
	}
	
	@Override
	public void onPrepareCraft(PrepareItemCraftEvent event)
	{
		if (event.getRecipe().getResult().getType() == Material.WOOD)
		{
			event.getInventory().getResult().setAmount(6);
		}
	}
}
