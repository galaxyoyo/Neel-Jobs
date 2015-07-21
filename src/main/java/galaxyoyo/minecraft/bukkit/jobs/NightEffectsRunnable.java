package galaxyoyo.minecraft.bukkit.jobs;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class NightEffectsRunnable extends BukkitRunnable
{
	@Override
	public void run()
	{
		long time = Bukkit.getServer().getWorlds().get(0).getTime();
		
		if (time < 12541 || time > 23458)
		{
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				player.removePotionEffect(PotionEffectType.BLINDNESS);
				for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
				{
					if (effect.getType().equals(PotionEffectType.SLOW_DIGGING))
					{
						Block target = player.getTargetBlock((Set<Material>) null, 5);
						if (target == null || (target.getType() != Material.LOG
										&& target.getType() != Material.LOG_2))
							player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
					}
				}
				player.removePotionEffect(PotionEffectType.WEAKNESS);
				player.removePotionEffect(PotionEffectType.SLOW);
			}
		}
		else
		{
			for (Player player : Bukkit.getServer().getOnlinePlayers())
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, true, false));
				
				if (time < 14000)
				{
					for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
					{
						if (effect.getType().equals(PotionEffectType.SLOW_DIGGING))
						{
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						}
						else if (effect.getType().equals(PotionEffectType.SLOW))
						{
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.SLOW);
						}
						else if (effect.getType().equals(PotionEffectType.BLINDNESS))
							player.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false));
				}
				else if (time < 16000)
				{
					for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
					{
						if (effect.getType().equals(PotionEffectType.SLOW_DIGGING))
						{
							if (effect.getAmplifier() != 1)
								player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						}
						else if (effect.getType().equals(PotionEffectType.SLOW))
						{
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.SLOW);
						}
						else if (effect.getType().equals(PotionEffectType.BLINDNESS))
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false));
				}
				else if (time < 22000)
				{
					for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
					{
						if (effect.getType().equals(PotionEffectType.SLOW_DIGGING))
						{
							if (effect.getAmplifier() != 2)
								player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						}
						else if (effect.getType().equals(PotionEffectType.SLOW))
						{
							if (effect.getAmplifier() != 1)
								player.removePotionEffect(PotionEffectType.SLOW);
						}
						else if (effect.getType().equals(PotionEffectType.BLINDNESS))
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false));
				}
				else if (time < 23000)
				{
					for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
					{
						if (effect.getType().equals(PotionEffectType.SLOW_DIGGING))
						{
							if (effect.getAmplifier() != 1)
								player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						}
						else if (effect.getType().equals(PotionEffectType.SLOW))
						{
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.SLOW);
						}
						else if (effect.getType().equals(PotionEffectType.BLINDNESS))
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false));
				}
				else if (time < 22000)
				{
					for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
					{
						if (effect.getType().equals(PotionEffectType.SLOW_DIGGING))
						{
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						}
						else if (effect.getType().equals(PotionEffectType.SLOW))
						{
							if (effect.getAmplifier() != 0)
								player.removePotionEffect(PotionEffectType.SLOW);
						}
						else if (effect.getType().equals(PotionEffectType.BLINDNESS))
							player.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0, true, false));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, true, false));
				}
			}
		}
		
		if (time == 12000)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Le soleil se couche");
		else if (time == 12541)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "La nuit tombe et vous "
							+ "commencez à être fatigu\u00e9");
		else if (time == 14000)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Il fait de plus en plus "
							+ "sombre et la visibilit\u00e9 d\u00e9croît fortement");
		else if (time == 16000)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Il fait beaucoup trop sombre "
							+ "et vous vous sentez \u00e9puis\u00e9, pensez \u00e0 vous coucher !");
		else if (time == 22000)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Le jour arrive et vous "
							+ "commencez \u00e0 y voir plus clair.");
		else if (time == 23000)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Votre fatigue disparaît au "
							+ "fur et \u00e0 mesure que le jour se l\u00e8ve.");
		else if (time == 23458)
			Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Le soleil se l\u00e8ve.");
	}
}
