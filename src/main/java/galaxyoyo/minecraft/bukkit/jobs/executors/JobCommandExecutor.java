package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.CompetenceTree;
import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommandExecutor implements CommandExecutor
{
	private final JobsPlugin plugin;
	
	public JobCommandExecutor(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		if (args[0].equalsIgnoreCase("change"))
		{
			if (args.length < 4)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
				return true;
			}
			
			OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
			if (!plugin.hasJob(op))
			{
				sender.sendMessage(ChatColor.DARK_RED + "Le joueur est inconnu, ne s'est jamais connect\u00e9 sur le serveur.");
				return true;
			}
			
			int jobNumber;
			
			try
			{
				jobNumber = Integer.parseInt(args[2]);
			}
			catch (NumberFormatException ex)
			{
				sender.sendMessage(ChatColor.DARK_RED + "'" + args[1] + "' n'est pas un nombre");
				return true;
			}
			

			if (jobNumber != 1 && jobNumber != 2)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Seuls deux m\u00e9tiers sont autoris\u00e9s");
				return true;
			}
			
			Job job = null;
			String jobName = "";
			for (int i = 3; i < args.length; ++i)
			{
				jobName += args[i] + " ";
			}
			jobName = jobName.substring(0, jobName.length() - 1);
			for (Job j : Job.values())
			{
				if (j.getDisplay().equalsIgnoreCase(args[3]) || j.name().equalsIgnoreCase(args[3]))
				{
					job = j;
					break;
				}
			}
			
			if (job == null)
			{
				sender.sendMessage(ChatColor.DARK_RED + "Le m\u00e9tier n'a pas \u00e9t\u00e9 trouv\u00e9.");
				return true;
			}
			
			Job[] arr = plugin.getJobs(op);
			CompetenceTree tree = CompetenceTree.get(Bukkit.getOfflinePlayer(args[1]));
			tree.remove(arr[jobNumber - 1]);
			arr[jobNumber - 1] = job;
			
			plugin.setJobs(op, arr);
			
			if (Bukkit.getServer().getPlayer(args[1]) != null)
			{
				Player player = Bukkit.getPlayer(args[1]);
				player.sendMessage(ChatColor.GOLD + "Vous avez chang\u00e9 de m\u00e9tier n°" + jobNumber  + " ! Vous \u00eates d\u00e9sormais " + job.getDisplay() + ".");
			}
			
			sender.sendMessage(ChatColor.GOLD + "Le joueur a bien chang\u00e9 de job !");
			return true;
		}
		
		return false;
	}

}
