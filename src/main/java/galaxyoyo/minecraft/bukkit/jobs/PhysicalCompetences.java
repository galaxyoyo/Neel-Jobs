package galaxyoyo.minecraft.bukkit.jobs;

import galaxyoyo.minecraft.bukkit.jobs.skills.BootsEnchantmentSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.EffectSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.PhysicalSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBrokenSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.EntityDamageSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.MobDamageSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerMoveSkill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PhysicalCompetences
{
	private static transient final List<PhysicalSkill> availableSkills = new ArrayList<PhysicalSkill>();
	private transient CompetenceTree tree;
	private final List<PhysicalSkill> skills = new ArrayList<PhysicalSkill>();
	
	public PhysicalCompetences(CompetenceTree tree)
	{
		this.tree = tree;
		for (PhysicalSkill skill : availableSkills)
		{
			skills.add(((PhysicalSkill) skill.clone()).setTree(this));
		}
	}
	
	protected PhysicalCompetences()
	{
	}
	
	public static List<PhysicalSkill> getAvailableSkills()
	{
		return availableSkills;
	}
	
	public List<PhysicalSkill> getSkills()
	{
		if (skills.size() != availableSkills.size())
		{
			for (int i = skills.size(); i < availableSkills.size(); ++i)
			{
				skills.add(((PhysicalSkill) availableSkills.get(i).clone()).setTree(this));
			}
		}
		
		return skills;
	}
	
	public CompetenceTree getCompetenceTree()
	{
		return tree;
	}
	
	public void setCompetenceTree(CompetenceTree tree)
	{
		this.tree = tree;
	}
	
	static
	{
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, false), "physical.0"),
						PlayerMoveSkill.class, 100000, 0));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false), "physical.1"),
						PlayerMoveSkill.class, 500000, 1));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true, false), "physical.2"),
						MobDamageSkill.class, 80000, 2));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, true, false), "physical.3"),
						MobDamageSkill.class, 500000, 3));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, true, false), "physical.4"),
						BlockBrokenSkill.class, 20000, 4));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1, true, false), "physical.5"),
						BlockBrokenSkill.class, 150000, 5));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true, false), "physical.6"),
						PlayerMoveSkill.class, 50000, 6));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, true, false), "physical.7"),
						PlayerMoveSkill.class, 400000, 7));
		availableSkills.add(new PhysicalSkill(new EffectSkill(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, false), "physical.8"),
						EntityDamageSkill.class, 400, 8));
		availableSkills.add(new PhysicalSkill(new BootsEnchantmentSkill(Enchantment.PROTECTION_FALL, 1, "physical.9"), EntityDamageSkill.class, 5000, 9));
		availableSkills.add(new PhysicalSkill(new BootsEnchantmentSkill(Enchantment.PROTECTION_FALL, 3, "physical.10"), EntityDamageSkill.class, 30000, 10));
		availableSkills.add(new PhysicalSkill(new BootsEnchantmentSkill(Enchantment.DEPTH_STRIDER, 1, "physical.11"), PlayerMoveSkill.class, 30000, 11));
		availableSkills.add(new PhysicalSkill(new BootsEnchantmentSkill(Enchantment.DEPTH_STRIDER, 3, "physical.12"), PlayerMoveSkill.class, 120420, 12));
	}
}
