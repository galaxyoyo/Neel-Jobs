package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.CompetenceTree;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IllegalityCommandExecutor implements CommandExecutor
{
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{		
		if (args.length < (sender instanceof Player ? 2 : 3) || args.length > 3)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		String targetPlayer = args.length < 3 ? ((Player) sender).getName() : args[2];
		int amount = 0;
		try
		{
			amount = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre valides");
			return true;
		}
		
		if (!CompetenceTree.contains(targetPlayer))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Le joueur '" + targetPlayer + "' ne s'est jamais inscrit sur le serveur");
			return true;
		}
		
		CompetenceTree tree = CompetenceTree.get(Bukkit.getOfflinePlayer(targetPlayer));
		
		if (args[0].equalsIgnoreCase("add"))
		{
			tree.addIllegalityPoint(amount);
		}
		else if (args[0].equalsIgnoreCase("remove"))
		{
			tree.removeIllegalityPoint(amount);
		}
		else if (args[0].equalsIgnoreCase("set"))
		{
			tree.setIllegalityPoint(amount);
		}
		
		sender.sendMessage(ChatColor.GOLD + "Points attribu\u00e9s !");
		
		if (args.length == 3)
		{
			if (Bukkit.getServer().getPlayer(targetPlayer) != null)
			{
				Player target = Bukkit.getServer().getPlayer(targetPlayer);
				if (args[0].equalsIgnoreCase("add"))
				{
					target.sendMessage(ChatColor.RED + "" + amount + " point" + (Math.abs(amount) > 1 ? "s" : "")
									+ " vous ont \u00e9t\u00e9 ajout\u00e9" + (Math.abs(amount) > 1 ? "s" : ""));
				}
				else if (args[0].equalsIgnoreCase("remove"))
				{
					target.sendMessage(ChatColor.GOLD + "" + amount + " point" + (Math.abs(amount) > 1 ? "s" : "")
									+ " vous ont \u00e9t\u00e9 enlev\u00e9" + (Math.abs(amount) > 1 ? "s" : ""));
				}
				else if (args[0].equalsIgnoreCase("set"))
				{
					target.sendMessage("Vous poss\u00e9dez d\u00e9sormais " + amount + " point"
									+ (Math.abs(amount) > 1 ? "s" : "") + " d'ill\u00e9galit\u00e9");
				}
			}
		}
		
		return true;
	}
}
