package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBreakingSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockStopBreakingSkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FortuneEnchantmentSkill extends Skill implements BlockBreakingSkill, BlockStopBreakingSkill
{
	private List<ItemStack> disableds = new ArrayList<ItemStack>();
	private int level;
	
	public FortuneEnchantmentSkill(int level, String id)
	{
		super (0, id);
		if (level <= 0)
			throw new IllegalArgumentException("Enchanting level <= 0");
		this.level = level;
	}
	
	protected FortuneEnchantmentSkill()
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
		List<String> levels = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");
		return "Fortune " + levels.get(level - 1) + " sur tous les items, sauf sur les items d\u00e9j\u00e0 "
				+ "Fortune ou Toucher de soie";
	}

	@Override
	public Skill clone()
	{
		return new FortuneEnchantmentSkill(level, getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass();
	}
	
	@Override
	public void onBlockBreaking(BlockDamageEvent event)
	{
		if (event.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)
				|| event.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
		{
			disableds.add(event.getItemInHand());
		}
		else if (CraftMagicNumbers.getBlock(event.getBlock().getType()).g(null, null) > 0.0F)
		{
			event.getItemInHand().addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, level);
		}
	}
	
	@Override
	public void onBlockStopBreaking(final BlockDamageEvent event)
	{
		if (!disableds.remove(event.getItemInHand()))
		{
			final ItemStack stack = event.getItemInHand();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getServer()
							.getPluginManager().getPlugin("Jobs-RPG"), new Runnable()
				{
					@Override
					public void run()
					{
						if (!disableds.remove(event.getPlayer().getName()))
						{
							stack.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
						}
					}
				}, 3L);
		}
	}
}
