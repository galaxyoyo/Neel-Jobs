package galaxyoyo.minecraft.bukkit.jobs.executors;

import static galaxyoyo.minecraft.bukkit.jobs.executors.ProtectionsCommandExecutor.Coord.X;
import static galaxyoyo.minecraft.bukkit.jobs.executors.ProtectionsCommandExecutor.Coord.Y;
import static galaxyoyo.minecraft.bukkit.jobs.executors.ProtectionsCommandExecutor.Coord.Z;
import static java.lang.Integer.MIN_VALUE;
import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;
import galaxyoyo.minecraft.bukkit.jobs.Zone;
import galaxyoyo.minecraft.bukkit.jobs.Zone.Protection;
import galaxyoyo.minecraft.bukkit.jobs.Zone.VisedProtection;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;

public class ProtectionsCommandExecutor implements CommandExecutor
{
	private final JobsPlugin plugin;
	
	public ProtectionsCommandExecutor(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if (args.length == 0)
		{
			help(sender);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("create"))
		{
			if (args.length < 8)
			{
				sender.sendMessage(ChatColor.DARK_RED + "/" + lbl + " help create");
				return true;
			}
			
			int x1 = parseInt(args[1], sender, X);
			int y1 = parseInt(args[2], sender, Y);
			int z1 = parseInt(args[3], sender, Z);
			int x2 = parseInt(args[4], sender, X);
			int y2 = parseInt(args[5], sender, Y);
			int z2 = parseInt(args[6], sender, Z);
			
			if (x1 == MIN_VALUE || y1 == MIN_VALUE || z1 == MIN_VALUE || x2 == MIN_VALUE || y2 == MIN_VALUE || z2 == MIN_VALUE)
				return true;
			
			World world;
			if (sender instanceof Player)
				world = ((Player) sender).getWorld();
			else
				world = Bukkit.getWorlds().get(0);
			
			VisedProtection visedProtec;
			try
			{
				visedProtec = VisedProtection.valueOf(args[7].toUpperCase());
			}
			catch (IllegalArgumentException ex)
			{
				sender.sendMessage(ChatColor.RED + "'" + args[7] + "' n'est pas un système de protection valide, accept\u00e9s : " + Arrays.asList(VisedProtection.values()));
				return true;
			}
			
			Zone zone = new Zone(world, x1, y1, z1, x2, y2, z2, visedProtec, Arrays.copyOfRange(args, 8, args.length));
			plugin.addZone(zone);
			sender.sendMessage(ChatColor.GOLD + "Zone cr\u00e9ee avec l'ID " + ChatColor.LIGHT_PURPLE + "#" + zone.getId() + ChatColor.GOLD + " !");
			return true;
		}
		else if (args[0].equalsIgnoreCase("showIds"))
		{
			if (!(sender instanceof Player))
			{
				help(sender);
				return true;
			}
			Player player = (Player) sender;
			List<Zone> zones = plugin.getZonesInPlace(player.getLocation());
			if (zones.isEmpty())
			{
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Vous ne vous situez dans aucune zone prot\u00e9g\u00e9e");
				return true;
			}
			player.sendMessage(ChatColor.LIGHT_PURPLE + "Vous vous situez dans " + zones.size() + " zone" + (zones.size() > 1 ? "s" : "") + " prot\u00e9g\u00e9e" + (zones.size() > 1 ? "s" : "") + " :");
			for (Zone zone : zones)
			{
				Location a = zone.getFirstPoint();
				Location b = zone.getLastPoint();
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Zone #" + zone.getId() + " : " + a.getBlockX() + ":" + a.getBlockY() + ":" + a.getBlockZ() + ", " + b.getBlockX() + ":" + b.getBlockY() + ":" + b.getBlockZ());
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Type de s\u00e9curit\u00e9 : " + zone.getVisedProtection());
				switch (zone.getVisedProtection())
				{
					case JOB :
						player.sendMessage(ChatColor.LIGHT_PURPLE + "Jobs autoris\u00e9s : " + zone.getJobs());
						break;
					case OP :
						player.sendMessage(ChatColor.LIGHT_PURPLE + "Joueurs OP autoris\u00e9s");
						break;
					case PERMISSION :
						player.sendMessage(ChatColor.LIGHT_PURPLE + "Joueurs poss\u00e9dant la permission '" + zone.getPermission() + "' autoris\u00e9s");
						break;
					case PLAYERS :
						player.sendMessage(ChatColor.LIGHT_PURPLE + "Joueurs " + zone.getAllowedPlayers() + " autoris\u00e9s");
						break;
				}
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Permission d'entrer : " + zone.getProtection(Protection.ENTRANCE));
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Permission de sortir : " + zone.getProtection(Protection.EXIT));
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Permission de placer un bloc : " + zone.getProtection(Protection.PLACE));
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Permission de casser : " + zone.getProtection(Protection.BREAK));
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Permission d'interagir : " + zone.getProtection(Protection.INTERACT));
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Possibilit\u00e9 de prendre des d\u00e9gâts : " + zone.getProtection(Protection.DAMAGE));
				if (zone.isAllowed(player))
					player.sendMessage(ChatColor.GREEN + "Vous êtes autoris\u00e9 \u00e0 faire les chose ALLOW et les choses DEFAULT");
				else
					player.sendMessage(ChatColor.RED + "Vous n'êtes autoris\u00e9 que de faire les choses ALLOW");
				player.sendMessage("");
			}
			return true;
		}
		else if (args[0].equalsIgnoreCase("change"))
		{
			if (args.length <= 3)
			{
				help(sender);
				return true;
			}
			
			int id = parseInt(args[1], sender);
			if (id < 0)
				return true;
			if (!plugin.containsZone(id))
			{
				sender.sendMessage(ChatColor.RED + "Cette zone n'existe pas");
				return true;
			}
			
			Zone zone = plugin.getZone(id);
			if (args[2].equalsIgnoreCase("secure"))
			{
				VisedProtection visedProtec;
				try
				{
					visedProtec = VisedProtection.valueOf(args[3].toUpperCase());
				}
				catch (IllegalArgumentException ex)
				{
					sender.sendMessage(ChatColor.RED + "'" + args[3] + "' n'est pas un système de protection valide, accept\u00e9s : " + Arrays.asList(VisedProtection.values()));
					return true;
				}
				zone = new Zone(zone.getFirstPoint(), zone.getLastPoint(), visedProtec, zone.getId(), Arrays.copyOfRange(args, 4, args.length));
				plugin.removeZone(zone);
				plugin.addZone(zone);
				sender.sendMessage(ChatColor.GOLD + "Zone #" + zone.getId() + " modifi\u00e9e avec succ\u00e8s !");
				return true;
			}
			else if (args[2].equalsIgnoreCase("allow"))
			{
				zone.appendArgs(Arrays.copyOfRange(args, 3, args.length));
				sender.sendMessage(ChatColor.GOLD + "Arguments ajout\u00e9s !");
				return true;
			}
			else if (args[2].equalsIgnoreCase("disallow"))
			{
				zone.appendArgs(Arrays.asList("remove", Arrays.copyOfRange(args, 3, args.length)).toArray(new String[args.length - 2]));
				sender.sendMessage(ChatColor.GOLD + "Arguments supprim\u00e9s !");
				return true;
			}
			else if (args[2].equalsIgnoreCase("zone"))
			{
				if (args.length <= 8)
				{
					help(sender);
					return true;
				}
				
				int x1 = parseInt(args[3], sender, X);
				int y1 = parseInt(args[4], sender, Y);
				int z1 = parseInt(args[5], sender, Z);
				int x2 = parseInt(args[6], sender, X);
				int y2 = parseInt(args[7], sender, Y);
				int z2 = parseInt(args[8], sender, Z);
				
				if (x1 == MIN_VALUE || y1 == MIN_VALUE || z1 == MIN_VALUE || x2 == MIN_VALUE || y2 == MIN_VALUE || z2 == MIN_VALUE)
					return true;
				
				Location a = new Location(zone.getFirstPoint().getWorld(), x1, y1, z1);
				Location b = new Location(a.getWorld(), x2, y2, z2);
				zone.setFirstPoint(a);
				zone.setLastPoint(b);
				sender.sendMessage(ChatColor.GOLD + "Zone #" + zone.getId() + " boug\u00e9e !");
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("protec"))
		{
			if (args.length != 4)
			{
				help(sender);
				return true;
			}
			
			int id = parseInt(args[1], sender);
			if (id < 0)
				return true;
			if (!plugin.containsZone(id))
			{
				sender.sendMessage(ChatColor.RED + "Cette zone n'existe pas");
				return true;
			}
			Zone zone = plugin.getZone(id);
			Protection protec;
			
			try
			{
				protec = Protection.valueOf(args[2].toUpperCase());
			}
			catch (IllegalArgumentException ex)
			{
				sender.sendMessage(ChatColor.RED + "La protection '" + args[2] + "' n'existe pas, disponibles : " + Arrays.toString(Protection.values()));
				return true;
			}
			Result result = null;
			try
			{
				result = Result.valueOf(args[3].toUpperCase());
			}
			catch (IllegalArgumentException ex)
			{
				sender.sendMessage("R\u00e9sultat '" + args[3] + "' inconnu, accept\u00e9s : " + Arrays.toString(Result.values()));
				return true;
			}
			zone.setProtection(protec, result);
			sender.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e : " + ChatColor.WHITE + protec + " = " + result);
			return true;
		}
		else if (args[0].equalsIgnoreCase("remove"))
		{
			if (args.length != 2)
			{
				help(sender);
				return true;
			}
			
			int id = parseInt(args[1], sender);
			if (id < 0)
				return true;
			if (!plugin.containsZone(id))
			{
				sender.sendMessage(ChatColor.RED + "Cette zone n'existe pas");
				return true;
			}
			plugin.removeZone(id);
			sender.sendMessage(ChatColor.GOLD + "Zone #" + id + "supprim\u00e9e avec succ\u00e8s !");
			return true;
		}
		else if (args[0].equalsIgnoreCase("help"))
		{
			if (args.length == 1)
			{
				cmdHelp(sender, lbl, "create <x1> <y1> <z1> <x2> <y2> <z2> [OP | PERMISSION | JOB | PLAYERS] args", "Cr\u00e9e une zone de protection, /" + lbl + " help create pour + d'infos");
				cmdHelp(sender, lbl, "change <id> secure [OP | PERMISSION | JOB | PLAYERS] <args>", "Modifie la s\u00e9curit\u00e9 de la zone, /" + lbl + " help change secure pour + d'infos");
				cmdHelp(sender, lbl, "change <id> allow <argsToAdd>", "Ajoute des joueurs ou un job \u00e0 autoriser");
				cmdHelp(sender, lbl, "change <id> disallow <argsToAdd>", "Enl\u00e9ve des joueurs ou un job \u00e0 autoriser");
				cmdHelp(sender, lbl, "change <id> zone <x1> <y1> <z1> <x2> <y2> <z2>", "Bouge une zone existante");
				cmdHelp(sender, lbl, "protec <id> [ENTRANCE | EXIT | PLACE | BREAK | INTERACT | DAMAGE] [ALLOW | DEFAULT | DENY]", "Modifie les permissions par d\u00e9faut pour tous, /" + lbl + " help protec pour + d'infos");
				cmdHelp(sender, lbl, "remove <id>", "Supprime une zone");
				if (sender instanceof Player)
					cmdHelp(sender, lbl, "showIds", "Montre toutes les zones dans lequel se situe le joueur");
				return true;
			}
			
			if (args[1].equalsIgnoreCase("create"))
			{
				cmdHelp(sender, lbl, "create [...] OP", "Seuls les joueurs OP pourront effectuer l'action");
				cmdHelp(sender, lbl, "create [...] PERMISSION <perm>", "Seuls les joueurs poss\u00e9dant la "
								+ "permisssion sp\u00e9cifi\u00e9e pourront effectuer l'action");
				cmdHelp(sender, lbl, "create [...] JOB <job1> [job2] ...", "Seuls les joueurs effectuant ce ou "
								+ "un de ces jobs pourront effectuer l'action");
				cmdHelp(sender, lbl, "create [...] PLAYERS <player1> [player2] ...", "Seuls les joueurs indiqu\u00e9s"
								+ " pourront effectuer l'action");
				return true;
			}
			
			if (args.length >= 3 && args[1].equalsIgnoreCase("change") && args[2].equalsIgnoreCase("secure"))
			{
				cmdHelp(sender, lbl, "change <id> secure OP", "Seuls les joueurs OP pourront effectuer l'action");
				cmdHelp(sender, lbl, "change <id> secure PERMISSION <perm>", "Seuls les joueurs poss\u00e9dant la "
								+ "permisssion sp\u00e9cifi\u00e9e pourront effectuer l'action");
				cmdHelp(sender, lbl, "change <id> secure JOB <job1> [job2] ...", "Seuls les joueurs effectuant ce ou "
								+ "un de ces jobs pourront effectuer l'action");
				cmdHelp(sender, lbl, "change <id> secure PLAYERS <player1> [player2] ...", "Seuls les joueurs indiqu\u00e9s"
								+ " pourront effectuer l'action");
				return true;
			}
			
			if (args[1].equalsIgnoreCase("protec"))
			{
				cmdHelp(sender, lbl, "protec <id> ENTRANCE", "Autorise ou non l'entr\u00e9e pour tout le monde dans la zone. D\u00e9faut : " + Protection.ENTRANCE.getDefaultValue());
				cmdHelp(sender, lbl, "protec <id> EXIT", "Autorise ou non la sortie pour tout le monde dans la zone. D\u00e9faut : " + Protection.EXIT.getDefaultValue());
				cmdHelp(sender, lbl, "protec <id> PLACE", "Autorise ou non le joueur \u00e0 placer un bloc dans la zone. D\u00e9faut : " + Protection.PLACE.getDefaultValue());
				cmdHelp(sender, lbl, "protec <id> BREAK", "Autorise ou non le joueur \u00e0 casser un bloc dans la zone. D\u00e9faut : " + Protection.BREAK.getDefaultValue());
				cmdHelp(sender, lbl, "protec <id> INTERACT", "Autorise ou non le joueur \u00e0 interagir dans la zone. D\u00e9faut : " + Protection.INTERACT.getDefaultValue());
				cmdHelp(sender, lbl, "protec <id> DAMAGE", "Permet \u00e0 toute entit\u00e9 de prendre des d\u00e9gâts ou non dans la zone. D\u00e9faut : " + Protection.DAMAGE.getDefaultValue());
				cmdHelp(sender, lbl, "protec <id> [...] ALLOW", "Autorise pour tout le monde l'action voulue");
				cmdHelp(sender, lbl, "protec <id> [...] DEFAULT", "Autorise pour les joueurs sp\u00e9cifi\u00e9s l'action");
				cmdHelp(sender, lbl, "protec <id> [...] DENY", "Refuse \u00e0 tout le monde l'action");
				return true;
			}
		}
		
		help(sender);
		return true;
	}
	
	public void cmdHelp(CommandSender sender, String lbl, String args, String desc)
	{
		sender.sendMessage(ChatColor.GOLD + (sender instanceof Player ? "/" : "") + lbl + " " + ChatColor.RED + args);
		sender.sendMessage(ChatColor.WHITE + desc);
	}
	
	public void help(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_RED + "/protec help");;
	}
	
	public int parseInt(String str, CommandSender sender, Coord ... coord)
	{
		try
		{
			return Integer.parseInt(str);
		}
		catch (NumberFormatException ex)
		{
			if (sender instanceof Player && str.startsWith("~") && coord.length >= 1)
			{
				Player player = (Player) sender;
				int add;
				try
				{
					add = Integer.parseInt(str.substring(1));
				}
				catch (NumberFormatException ex2)
				{
					sender.sendMessage(ChatColor.RED + "'" + str + "' n'est pas un nombre valide");
					return MIN_VALUE;
				}
				
				switch (coord[0])
				{
					case X:
						return player.getLocation().getBlockX() + add;
					case Y:
						return player.getLocation().getBlockY() + add;
					case Z:
						return player.getLocation().getBlockZ() + add;
				}
			}
			sender.sendMessage(ChatColor.RED + "'" + str + "' n'est pas un nombre valide");
			return MIN_VALUE;
		}
	}
	
	enum Coord
	{
		X, Y, Z;
	}
}
