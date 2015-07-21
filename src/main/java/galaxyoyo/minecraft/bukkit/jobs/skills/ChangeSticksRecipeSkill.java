package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PrepareCraftSkill;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class ChangeSticksRecipeSkill extends Skill implements PrepareCraftSkill
{
	public ChangeSticksRecipeSkill(String id)
	{
		super (id);
	}
	
	protected ChangeSticksRecipeSkill()
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
		return "Deux planches = 6 bâtons";
	}
	
	@Override
	public Skill clone()
	{
		return new ChangeSticksRecipeSkill(getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass();
	}
	
	@Override
	public void onPrepareCraft(PrepareItemCraftEvent event)
	{
		if (event.getRecipe().getResult().getType() == Material.STICK)
		{
			event.getInventory().getResult().setAmount(6);
		}
	}
}
