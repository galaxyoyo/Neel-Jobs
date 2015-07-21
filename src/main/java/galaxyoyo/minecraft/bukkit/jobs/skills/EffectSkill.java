package galaxyoyo.minecraft.bukkit.jobs.skills;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectSkill extends Skill
{
	private PotionEffect effect;
	
	public EffectSkill(PotionEffect effect, String id)
	{
		this (effect, 0, false, id);
	}
	
	public EffectSkill(PotionEffect effect, int cooldown, String id)
	{
		this (effect, cooldown, false, id);
	}
	
	public EffectSkill(PotionEffect effect, boolean active, String id)
	{
		this (effect, 0, active, id);
	}
	
	public EffectSkill(PotionEffect effect, int cooldown, boolean active, String id)
	{
		super (cooldown, id);
		this.effect = effect;
		setActive(active);
	}
	
	protected EffectSkill()
	{
	}
	
	@Override
	public void execute(Player player)
	{
		if (getCooldown() > 0)
		{
			player.sendMessage(ChatColor.RED + "Veuillez attendre " + getFormattedTime(getCooldown()) + " avant de pouvoir faire cela \u00e0 nouveau.");
			return;
		}
		
		player.addPotionEffect(getPotionEffect());
		
		if (isActive())
			startCooldown();
	}
	
	@Override
	public void reset(Player player)
	{
		player.removePotionEffect(effect.getType());
	}
	
	@Override
	public String getDescription()
	{
		List<String> levels = Arrays.asList("", " II", " III", " IV");
		PotionEffect effect = getPotionEffect();
		String str = "Effet de " + getPotionName(effect.getType()) + levels.get(effect.getAmplifier())
				+ (isActive() ? " pendant " + effect.getDuration() / 20 + " secondes\n\rActivation : "
						+ "double clic droit avec hache (bûcheron) / pioche (mineur)" : "");
		return str;
	}
	
	public PotionEffect getPotionEffect()
	{
		return new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier());
	}
	
	@Override
	public Skill clone()
	{
		EffectSkill skill = new EffectSkill(effect, getBaseCooldown(), isActive(), getId());
		skill.setCooldown(skill.getCooldown());
		return skill;
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown()
				&& effect.equals(((EffectSkill) skill).getPotionEffect());
	}
	
	@SuppressWarnings("deprecation")
	public static String getPotionName(PotionEffectType type)
	{
		return getPotionName(type.getId());
	}
	
	public static String getPotionName(int type)
	{
		switch (type)
		{
		case 1: return "vitesse";
		case 2: return "lenteur";
		case 3: return "c\u00e9l\u00e9rit\u00e9";
		case 4: return "fatigue de minage";
		case 5: return "force";
		case 6: return "soins instantan\u00e9s";
		case 7: return "d\u00e9gâts instantan\u00e9s";
		case 8: return "sauts am\u00e9lior\u00e9s";
		case 9: return "confusion";
		case 10: return "r\u00e9g\u00e9n\u00e9ration";
		case 11: return "r\u00e9sistance";
		case 12: return "r\u00e9sistance au feu";
		case 13: return "respiration aquatique";
		case 14: return "invisibilit\u00e9";
		case 15: return "c\u00e9cit\u00e9";
		case 16: return "vision nocturne";
		case 17: return "faim";
		case 18: return "faiblesse";
		case 19: return "poison";
		case 20: return "wither";
		case 21: return "bonus de vie";
		case 22: return "absorption";
		case 23: return "saturation";
		default: throw new IllegalArgumentException("type does not exist");
		}
	}
}
