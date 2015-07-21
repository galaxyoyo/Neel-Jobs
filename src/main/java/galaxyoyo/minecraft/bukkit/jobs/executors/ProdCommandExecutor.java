package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;
import galaxyoyo.minecraft.bukkit.jobs.ProductionRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class ProdCommandExecutor implements CommandExecutor
{
	private final JobsPlugin plugin;
	
	public ProdCommandExecutor(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande");
			return true;
		}
		
		if (args.length != 2)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		int pepites = 0;
		try
		{
			pepites = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.RED + "'" + args[0] + "' n'est pas un nombre valide");
			return true;
		}
		
		CraftPlayer player = (CraftPlayer) sender;
		CraftBlock target = (CraftBlock) player.getTargetBlock((Set<Material>) null, 5);
		
		if (target == null || target.getType() != Material.CHEST)
		{
			sender.sendMessage(ChatColor.RED + "Veuillez pointer un coffre");
			return true;
		}
		
		Location loc = new Location(target.getWorld(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ());
		if (pepites == 0)
		{
			plugin.pepitesPerDay.remove(loc);
			BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			for (BlockFace face : faces)
			{
				Block relative = loc.getBlock().getRelative(face);
				
				if (relative.getType() == Material.CHEST)
				{
					List<BlockFace> facesList = new ArrayList<BlockFace>(Arrays.asList(faces));
					facesList.remove(face.getOppositeFace());
					for (BlockFace f : facesList)
					{
						Block rel = relative.getRelative(f);
						if (rel.getType() != Material.WALL_SIGN)
							continue;
						CraftSign sign = (CraftSign) rel.getState();
						if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
							rel.setType(Material.AIR);
					}
				}
				else if (relative.getType() == Material.WALL_SIGN)
				{
					CraftSign sign = (CraftSign) relative.getState();
					if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
						relative.setType(Material.AIR);
				}
			}
		}
		else
		{
			plugin.pepitesPerDay.put(loc, Integer.valueOf(pepites));
			BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			for (BlockFace face : faces)
			{
				Block relative = loc.getBlock().getRelative(face);
				
				if (relative.getType() == Material.CHEST)
				{
					List<BlockFace> facesList = new ArrayList<BlockFace>(Arrays.asList(faces));
					facesList.remove(face.getOppositeFace());
					for (BlockFace f : facesList)
					{
						Block rel = relative.getRelative(f);
						if (rel.getType() != Material.AIR && rel.getType() != Material.WALL_SIGN)
							continue;
						rel.setType(Material.WALL_SIGN);
						CraftSign sign = (CraftSign) rel.getState();
						sign.setLine(0, ChatColor.GREEN + "Propri\u00e9taire");
						sign.setLine(1, ChatColor.GRAY + args[1]);
						sign.setLine(2, ChatColor.GOLD + "P\u00e9pites / jour");
						sign.setLine(3, ChatColor.WHITE + "" + pepites);
						MaterialData data = sign.getData();
						switch (f)
						{
							case NORTH :
								data.setData((byte) 2);
								break;
							case SOUTH :
								data.setData((byte) 3);
								break;
							case WEST :
								data.setData((byte) 4);
								break;
							case EAST :
								data.setData((byte) 5);
								break;
							default :
								continue;
						}
						sign.setData(data);
						sign.update(true, true);
					}
				}
				else if (relative.getType() == Material.AIR || relative.getType() == Material.WALL_SIGN)
				{
					relative.setType(Material.WALL_SIGN);
					CraftSign sign = (CraftSign) relative.getState();
					sign.setLine(0, ChatColor.GREEN + "Propri\u00e9taire");
					sign.setLine(1, ChatColor.GRAY + args[1]);
					sign.setLine(2, ChatColor.GOLD + "P\u00e9pites / jour");
					sign.setLine(3, ChatColor.WHITE + "" + pepites);
					MaterialData data = sign.getData();
					switch (face)
					{
						case NORTH :
							data.setData((byte) 2);
							break;
						case SOUTH :
							data.setData((byte) 3);
							break;
						case WEST :
							data.setData((byte) 4);
							break;
						case EAST :
							data.setData((byte) 5);
							break;
						default :
							continue;
					}
					sign.setData(data);
					sign.update(true, true);
				}
			}
			
			Date now = new Date();
			Calendar midnightCal = new GregorianCalendar();
			midnightCal.setTime(now);
			midnightCal.set(Calendar.HOUR_OF_DAY, 0);
			midnightCal.set(Calendar.MINUTE, 0);
			midnightCal.set(Calendar.SECOND, 0);
			midnightCal.set(Calendar.MILLISECOND, 0);
			midnightCal.add(Calendar.DAY_OF_MONTH, 1);
			Date tomorrow = midnightCal.getTime();
			long time = tomorrow.getTime() - now.getTime();
			time /= 50;
			ProductionRunnable run = new ProductionRunnable(plugin);
			run.runTaskLater(plugin, time);
		}
		
		sender.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e !");
		
		return true;
	}
}
