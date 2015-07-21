package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.FurnaceSmeltSkill;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class BurnTwoSkill extends Skill implements FurnaceSmeltSkill
{
	private static transient final Random random = new Random();
	private Material mat;
	private int proba;
	
	public BurnTwoSkill(Material material, int proba, String id)
	{
		super (id);
		mat = material;
		this.proba = proba;
	}
	
	protected BurnTwoSkill()
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
		return proba + " % de chance de cuire deux " + mat + " \u00e0 la fois";
	}
	
	@Override
	public Skill clone()
	{
		return new BurnTwoSkill(mat, proba, getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && mat == ((BurnTwoSkill) skill).mat;
	}
	
	@Override
	public void onSmelt(FurnaceSmeltEvent event, Player player)
	{
		if (event.getSource().getType() == mat && event.getResult().getAmount()
				< event.getResult().getMaxStackSize() && event.getSource().getAmount() > 1
				&& random.nextInt() < proba)
		{
			event.getSource().setAmount(event.getSource().getAmount() - 1);
			ItemStack result = event.getResult();
			result.setAmount(result.getAmount() + 1);
			event.setResult(result);
		}
	}
}
