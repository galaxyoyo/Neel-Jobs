package galaxyoyo.minecraft.bukkit.jobs.skills;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class BootsEnchantmentSkill extends Skill
{
	private Enchantment enchant;
	private int level;
	
	public BootsEnchantmentSkill(Enchantment enchant, int level, String id)
	{
		super (id);
		this.enchant = enchant;
		this.level = level;
	}
	
	protected BootsEnchantmentSkill()
	{
		super ();
	}
	
	public Enchantment getEnchantment()
	{
		return enchant;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	@Override
	public void execute(Player player)
	{
		reset(player);
		
		if (isActive() && isActivated())
		{
			if (player.getInventory().getBoots() != null
							&& player.getInventory().getBoots().getType() != Material.AIR)
				player.getInventory().getBoots().addUnsafeEnchantment(enchant, level);
		}
	}
	
	@Override
	public void reset(Player player)
	{
		if (player.getInventory().getBoots() != null)
			player.getInventory().getBoots().removeEnchantment(enchant);
	}
	
	@Override
	public String getDescription()
	{
		List<String> levels = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");
		return "Bottes enchant\u00e9es " + enchant.getName() + " " + levels.get(level - 1) + "\n\r"
						+ ChatColor.DARK_RED + "/!\\ Remplace le pr\u00e9c\u00e9dent enchantment /!\\";
	}
	
	@Override
	public Skill clone()
	{
		return new BootsEnchantmentSkill(enchant, level, getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && enchant == ((BootsEnchantmentSkill) skill).getEnchantment()
						&& level == ((BootsEnchantmentSkill) skill).getLevel();
	}
}
