package galaxyoyo.minecraft.bukkit.jobs.skills;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ForgeSkill extends Skill
{
	private ItemStack buy;
	private int enchantId;
	private int enchantLevel;
	
	@SuppressWarnings("deprecation")
	public ForgeSkill(ItemStack buy, Enchantment enchant, int level, String id)
	{
		super (id);
		this.buy = buy;
		this.enchantId = enchant.getId();
		enchantLevel = level;
	}
	
	protected ForgeSkill()
	{
		super ();
	}
	
	@Override
	public void execute(Player player)
	{
		player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"),
				"jobsrpg.command.forge", true);
	}
	
	@Override
	public void reset(Player player)
	{
		player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Jobs-RPG"),
				"jobsrpg.command.forge", false);
	}
	
	@Override
	public String getDescription()
	{
		List<String> levels = Arrays.asList("I", "II", "III", "IV", "V");
		return "Dans le /forge, possibilit\u00e9 d'acheter\n\run livre enchant\u00e9 " + getEnchant().getName()
				+ " " + levels.get(enchantLevel - 1) + " contre " + buy.getAmount() + "x" + buy.getType();
	}
	
	@Override
	public Skill clone()
	{
		return new ForgeSkill(buy, getEnchant(), enchantLevel, getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && buy.equals(((ForgeSkill) skill).getBuy())
				&& getSell().equals(((ForgeSkill) skill).getSell());
	}
	
	@SuppressWarnings("deprecation")
	public Enchantment getEnchant()
	{
		return Enchantment.getById(enchantId);
	}
	
	public ItemStack getBuy()
	{
		return buy;
	}
	
	public ItemStack getSell()
	{
		ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
		meta.addStoredEnchant(getEnchant(), enchantLevel, true);
		stack.setItemMeta(meta);
		return stack;
	}
}
