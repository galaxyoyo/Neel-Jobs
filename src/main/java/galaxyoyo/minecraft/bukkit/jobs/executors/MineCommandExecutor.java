package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;
import galaxyoyo.minecraft.bukkit.jobs.Mine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

public class MineCommandExecutor implements CommandExecutor
{
	private final Mine mine;
	
	public MineCommandExecutor(JobsPlugin plugin)
	{
		this.mine = plugin.mine;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("seta") || args[0].equalsIgnoreCase("setb"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande.");
				return true;
			}
			
			CraftPlayer player = (CraftPlayer) sender;
			Block target = player.getTargetBlock(Sets.newHashSet(Material.BARRIER, Material.TORCH, Material.AIR), 8);
			if (target == null)
			{
				player.sendMessage(ChatColor.RED + "Veuillez pointer un bloc.");
				return true;
			}
			Location loc = target.getLocation();
			if (args[0].equalsIgnoreCase("setA"))
			{
				mine.setLocA(loc);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Location A d\u00e9finie en " + loc.getBlockX()
								+ ", " + loc.getBlockY() + ", " + loc.getBlockZ());
				return true;
			}
			else if (args[0].equalsIgnoreCase("setB"))
			{
				mine.setLocB(loc);
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Location B d\u00e9finie en " + loc.getBlockX()
								+ ", " + loc.getBlockY() + ", " + loc.getBlockZ());
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("regenerate"))
		{
			mine.generate();
			return true;
		}
		else if (args[0].equalsIgnoreCase("setsign"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande.");
				return true;
			}
			
			if (args.length != 2)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
				return true;
			}
			
			CraftPlayer player = (CraftPlayer) sender;
			Block target = player.getTargetBlock(Sets.newHashSet(Material.BARRIER, Material.TORCH, Material.AIR), 8);
			if (target == null || (target.getType() != Material.SIGN && target.getType() != Material.WALL_SIGN))
			{
				player.sendMessage(ChatColor.RED + "Veuillez pointer une pancarte.");
				return true;
			}
			Location loc = target.getLocation();
			try
			{
				switch (args[1].toLowerCase())
				{
					case "coal":
						mine.setCoalSignLocation(loc);
						break;
					case "iron":
						mine.setIronSignLocation(loc);
						break;
					case "gold":
						mine.setGoldSignLocation(loc);
						break;
					case "lapis":
						mine.setLapisSignLocation(loc);
						break;
					case "redstone":
						mine.setRedstoneSignLocation(loc);
						break;
					case "null":
						mine.removeSignLocation(loc);
						break;
					default:
						player.sendMessage(ChatColor.RED + "Ressource '" + args[1] + "' inconnue, possibles : coal (charbon), iron (fer), gold (or), lapis, redstone, null (pour supprimer)");
						return true;
				}
			}
			catch (IllegalArgumentException ex)
			{
				player.sendMessage(ChatColor.RED + ex.getMessage());
				return true;
			}
			player.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e !");
			return true;
		}
		
		sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
		return true;
	}
}
