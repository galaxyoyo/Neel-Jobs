package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

public class ChatCommandExecutor implements CommandExecutor
{
	private final JobsPlugin plugin;
	
	public ChatCommandExecutor(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "Merci d'entrer un message.");
			return true;
		}

		Job[] jobs = plugin.getJobs(p);
		String lastSpeaker = plugin.listener.lastSpeaker;
		String header = ChatColor.BOLD + "<" + jobs[0].getPrefix() + ChatColor.RESET + ChatColor.BOLD + jobs[1].getPrefix() + ChatColor.RESET + ChatColor.BOLD + p.getDisplayName() + ChatColor.BOLD + ">";
		String msg = Strings.join(args, " ");
		String name = String.valueOf(p.getName().charAt(0));
		double distance = -1.0D;
		
		if (cmd.getLabel().equalsIgnoreCase("chuchotte"))
		{
			msg = ChatColor.AQUA + msg;
			distance = 5.0D;
		}
		else if (cmd.getLabel().equalsIgnoreCase("hurle"))
		{
			msg = ChatColor.RED + msg;
			distance = 75.0D;
		}
		else if (cmd.getLabel().equalsIgnoreCase("horsrp"))
		{
			name += ChatColor.RESET + " -> " + ChatColor.GREEN + "all";
			distance = -1.0D;
		}
		else
		{
			sender.sendMessage(ChatColor.DARK_RED + "Erreur");
			return true;
		}
		
		msg = msg.replaceAll("&&", "%µ£¨%%µ£¨M.;");
		for (int i = 0; i <= 9; ++i)
			msg = msg.replaceAll("&" + i, ChatColor.getByChar(String.valueOf(i).charAt(0)).toString());
		for (char c = 'a'; c <= 'f'; ++c)
			msg = msg.replaceAll("&" + c, ChatColor.getByChar(c).toString());
		for (char c = 'A'; c <= 'F'; ++c)
			msg = msg.replaceAll("&" + c, ChatColor.getByChar(Character.toLowerCase(c)).toString());
		msg = msg.replaceAll("&k", ChatColor.MAGIC.toString()).replaceAll("&l", ChatColor.BOLD.toString())
						.replaceAll("&m", ChatColor.STRIKETHROUGH.toString()).replaceAll("&n", ChatColor.UNDERLINE.toString())
						.replaceAll("&o", ChatColor.ITALIC.toString()).replaceAll("&r", ChatColor.RESET.toString());
		msg = msg.replaceAll("&K", ChatColor.MAGIC.toString()).replaceAll("&L", ChatColor.BOLD.toString())
						.replaceAll("&M", ChatColor.STRIKETHROUGH.toString()).replaceAll("&N", ChatColor.UNDERLINE.toString())
						.replaceAll("&O", ChatColor.ITALIC.toString()).replaceAll("&R", ChatColor.RESET.toString());
		msg = msg.replaceAll("%µ£¨%%µ£¨M.;", "&&");
		
		Bukkit.getServer().getConsoleSender().sendMessage(header);
		Bukkit.getServer().getConsoleSender().sendMessage("[" + ChatColor.GRAY + name + ChatColor.RESET + "] " + msg);
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if (distance > 0.0D && player.getLocation().distance(p.getLocation()) > distance)
				continue;
			
			if (lastSpeaker == null || !p.getName().equals(lastSpeaker))
			{
				
				player.sendMessage(header);
			}
			player.sendMessage("[" + ChatColor.GRAY + name + ChatColor.RESET + "] " + msg);
		}
		lastSpeaker = p.getName();
		plugin.listener.lastSpeaker = p.getName();
		
		return true;
	}

}
