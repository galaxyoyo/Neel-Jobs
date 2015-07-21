package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBreakingSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockStopBreakingSkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.potion.PotionEffect;

public class BlockBreakingEffectSkill extends EffectSkill implements BlockBreakingSkill, BlockStopBreakingSkill
{
	private List<Material> blocks = new ArrayList<Material>();
	
	public BlockBreakingEffectSkill(String id, PotionEffect effect, Material ... blocks)
	{
		super (effect, 0, id);
		this.blocks = Arrays.asList(blocks);
		setActive(true);
	}
	
	protected BlockBreakingEffectSkill()
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
		List<String> levels = Arrays.asList("", " II", " III", " IV");
		PotionEffect effect = getPotionEffect();
		String blocks = this.getBlocks().toString().substring(1);
		blocks = blocks.substring(0, blocks.length() - 1);
		String str = "Effet de " + getPotionName(effect.getType()) + levels.get(effect.getAmplifier())
				+ " en minant " + blocks;
		return str;
	}
	
	public List<Material> getBlocks()
	{
		return blocks;
	}
	
	@Override
	public Skill clone()
	{
		Skill skill = new BlockBreakingEffectSkill(getId(), getPotionEffect(),
						getBlocks().toArray(new Material[blocks.size()]));
		skill.setActive(isActive());
		skill.setCooldown(getCooldown());
		return skill;
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && isActive() == skill.isActive()
				&& getBaseCooldown() == skill.getBaseCooldown()
				&& getPotionEffect().equals(((EffectSkill) skill).getPotionEffect())
				&& Arrays.deepEquals(blocks.toArray(),
						((BlockBreakingEffectSkill) skill).blocks.toArray());
	}
	
	@Override
	public void onBlockBreaking(BlockDamageEvent event)
	{
		if (getBlocks().contains(event.getBlock().getType()))
			event.getPlayer().addPotionEffect(getPotionEffect());
	}

	@Override
	public void onBlockStopBreaking(BlockDamageEvent event)
	{
		if (getBlocks().contains(event.getBlock().getType()))
			event.getPlayer().removePotionEffect(getPotionEffect().getType());
	}
}
