package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.FurnaceBurnSkill;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceBurnEvent;

public class SmeltFasterSkill extends Skill implements FurnaceBurnSkill
{
	public SmeltFasterSkill(int cooldown, String id)
	{
		super (cooldown, id);
	}
	
	protected SmeltFasterSkill()
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
		return "Cuit 2 fois plus rapidement dans les fours dans un rayon de 5 blocs autour";
	}
	
	@Override
	public Skill clone()
	{
		return new SmeltFasterSkill(getBaseCooldown(), getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown();
	}
	
	@Override
	public void onFurnaceBurn(FurnaceBurnEvent event, Player player)
	{
		if (!isActivated())
			return;
		
		event.setBurnTime(event.getBurnTime() / 2);
	}
}
