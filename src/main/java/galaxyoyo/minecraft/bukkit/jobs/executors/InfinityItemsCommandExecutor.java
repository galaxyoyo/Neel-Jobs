package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.InfinityItem;
import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfinityItemsCommandExecutor implements CommandExecutor
{
	private final JobsPlugin plugin;
	private final Map<String, InfinityItem> pending = new HashMap<String, InfinityItem>();
	
	public InfinityItemsCommandExecutor(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande");
			return true;
		}
		
		if (args.length <= 0 || args.length > 3)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		CraftPlayer player = (CraftPlayer) sender;
		
		if (args[0].equalsIgnoreCase("clear"))
		{
			if (args.length == 1)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Usage : /ii clear <radius>");
				return true;
			}
			
			if (!pending.containsKey(player.getName()))
			{
				player.sendMessage(ChatColor.RED + "Pas d'item infini en attente !");
				return true;
			}
			int radius;
			try
			{
				radius = Double.valueOf(args[1]).intValue();
			}
			catch (NumberFormatException ex)
			{
				player.sendMessage(ChatColor.RED + "'" + args[1] + "' n'est pas un nombre valide");
				return true;
			}
			InfinityItem ii = pending.remove(player.getName());
			Location loc = player.getLocation();
			loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			ii.setClearZone(loc);
			ii.setClearRadius(radius);
			plugin.infinityItems.put(ii.getLocation(), ii);
			player.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e !");
			return true;
		}
		
		if (pending.containsKey(player.getName()))
		{
			player.sendMessage(ChatColor.RED + "Un item infini est en cr\u00e9ation !");
			player.sendMessage("Faîtes /ii clear <radius> \u00e0 l'endroit où l'objet doît être enlev\u00e9.");
			return true;
		}
		
		Block targetBlock = player.getTargetBlock((Set<Material>) null, 5);
		if (targetBlock == null || targetBlock.getType() == Material.AIR)
		{
			player.sendMessage(ChatColor.RED + "Vous ne pointez aucun bloc !");
			return true;
		}
		Location loc = targetBlock.getLocation();
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		if (args[0].equalsIgnoreCase("remove"))
		{
			if (!plugin.infinityItems.containsKey(loc))
				player.sendMessage(ChatColor.RED + "Aucun item infini n'est contenu !");
			else
			{
				plugin.infinityItems.remove(loc);
				player.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e !");
			}
			return true;
		}
		
		ItemStack _stack = player.getItemInHand();
		ItemStack stack = new ItemStack(_stack.getType(), _stack.getAmount(), _stack.getDurability());
		stack.setItemMeta(_stack.getItemMeta().clone());
		stack.setData(_stack.getData().clone());
		if (stack == null || stack.getType() == Material.AIR)
		{
			player.sendMessage(ChatColor.RED + "Merci d'avoir quelque chose dans votre main.");
			return true;
		}
		
		InfinityItem ii = null;
		
		if (args[0].equalsIgnoreCase("job"))
		{
			if (args.length == 1)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Usage : /ii job <job>");
				return true;
			}
			
			Job job;
			try
			{
				job = Job.valueOf(args[1].toUpperCase());
			}
			catch (IllegalArgumentException ex)
			{
				player.sendMessage(ChatColor.RED + "Le job n'existe pas !");
				return true;
			}
			
			ii = new InfinityItem(stack, loc, job);
		}
		else if (args[0].equalsIgnoreCase("perm"))
		{
			if (args.length == 1)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Usage : /ii perm <permission>");
				return true;
			}
			
			String perm = args[1];
			if (Bukkit.getServer().getPluginManager().getPermission(perm) == null)
			{
				player.sendMessage(ChatColor.RED + "Cette permission n'existe pas !");
				return true;
			}
			ii = new InfinityItem(stack, loc, perm);
		}
		else if (args[0].equalsIgnoreCase("all"))
			ii = new InfinityItem(stack, loc);
		else
		{
			player.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		if (args.length == 3 && args[2].equalsIgnoreCase("clear"))
		{
			sender.sendMessage("Faîtes /ii clear <radius> \u00e0 l'endroit où l'objet doît être enlev\u00e9.");
			pending.put(player.getName(), ii);
			return true;
		}
		
		plugin.infinityItems.put(loc, ii);
		player.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e !");
		
		return true;
	}
}
