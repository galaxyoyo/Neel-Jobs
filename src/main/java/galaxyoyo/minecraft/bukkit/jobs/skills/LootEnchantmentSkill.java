package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.MobDamageSkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LootEnchantmentSkill extends Skill implements MobDamageSkill
{
	private List<String> disableds = new ArrayList<String>();
	private int level;
	
	public LootEnchantmentSkill(int level, String id)
	{
		this (level, 0, id);
	}
	
	public LootEnchantmentSkill(int level, int cooldown, String id)
	{
		super (cooldown, id);
		if (level <= 0)
			throw new IllegalArgumentException("Enchanting level <= 0");
		this.level = level;
	}
	
	protected LootEnchantmentSkill()
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
		return "Butin " + levels.get(level - 1) + " sur tous les items, sauf sur les items d\u00e9j\u00e0 Butin"
				+ (isActive() ? "\n\rActivation : clic droit avec une fl\u00e8che" : "");
	}

	@Override
	public Skill clone()
	{
		Skill skill = new LootEnchantmentSkill(level, getBaseCooldown(), getId());
		skill.setActive(isActive());
		return skill;
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown();
	}
	
	@Override
	public void onMobDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity)
				|| ((LivingEntity) event.getEntity()).getHealth() - event.getDamage() > 0.0D)
			return;
		
		if (isActive() && !isActivated())
			return;
		
		if (isActive())
			desactivate();
		
		CraftPlayer player = (CraftPlayer) event.getDamager();
		
		if (player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS))
		{
			disableds.add(player.getName());
		}
		else
		{
			player.getItemInHand().addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level);
		}
		
		final Player finalPlayer = player;
		final ItemStack stack = player.getItemInHand();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getServer()
				.getPluginManager().getPlugin("Jobs-RPG"), new Runnable()
		{
			@Override
			public void run()
			{
				if (!disableds.remove(finalPlayer.getName()))
				{
					stack.removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
				}
			}
		}, 3L);
		
		startCooldown();
	}
}
