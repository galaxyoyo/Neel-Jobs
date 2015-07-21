package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.FurnaceSmeltSkill;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

public class BurnTwoIngotsSkill extends Skill implements FurnaceSmeltSkill
{
	private static transient final Random random = new Random();
	private int proba;
	private Material mat;
	
	public BurnTwoIngotsSkill(int percent, Material material, String id)
	{
		super (id);
		proba = percent;
		mat = material;
	}
	
	protected BurnTwoIngotsSkill()
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
		return "Probabilit\u00e9 de " + proba + "% de faire fondre deux " + mat;
	}
	
	@Override
	public Skill clone()
	{
		return new BurnTwoIngotsSkill(proba, mat, getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && proba == ((BurnTwoIngotsSkill) skill).proba
				&& mat == ((BurnTwoIngotsSkill) skill).mat;
	}
	
	@Override
	public void onSmelt(FurnaceSmeltEvent event, Player player)
	{
		if (event.getResult().getType() == mat && random.nextInt(100) < proba)
		{
			ItemStack stack = event.getResult();
			stack.setAmount(2);
			event.setResult(stack);
		}
	}
}
