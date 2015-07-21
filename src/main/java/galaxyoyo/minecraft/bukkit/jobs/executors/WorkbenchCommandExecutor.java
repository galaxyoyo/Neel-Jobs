package galaxyoyo.minecraft.bukkit.jobs.executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class WorkbenchCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande.");
			return true;
		}
		
		CraftPlayer player = (CraftPlayer) sender;
		player.openWorkbench(player.getLocation(), true);
		
		return true;
	}
}
