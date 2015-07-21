package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.skills.Zone;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PatternCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent ex\u00e9cuter cette commande");
			return true;
		}
		
		if (args.length != 8 || !args[0].equalsIgnoreCase("create"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Usage : " + cmd.getUsage());
			return true;
		}
		
		String patternName = args[1];
		
		int x1, y1, z1, x2, y2, z2;
		
		try
		{
			x1 = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Nombre invalide : '" + args[2] + "'");
			return true;
		}
		
		try
		{
			y1 = Integer.parseInt(args[3]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Nombre invalide : '" + args[3] + "'");
			return true;
		}
		
		try
		{
			z1 = Integer.parseInt(args[4]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Nombre invalide : '" + args[4] + "'");
			return true;
		}
		
		try
		{
			x2 = Integer.parseInt(args[5]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Nombre invalide : '" + args[5] + "'");
			return true;
		}
		
		try
		{
			y2 = Integer.parseInt(args[6]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Nombre invalide : '" + args[6] + "'");
			return true;
		}
		
		try
		{
			z2 = Integer.parseInt(args[7]);
		}
		catch (NumberFormatException ex)
		{
			sender.sendMessage(ChatColor.DARK_RED + "Nombre invalide : '" + args[7] + "'");
			return true;
		}
		
		CraftPlayer player = (CraftPlayer) sender;
		
		if (!player.getInventory().contains(Material.PAPER))
		{
			player.sendMessage(ChatColor.RED + "Vous devez poss\u00e9der une feuille pour cr\u00e9er un pattern");
			return true;
		}
		player.getInventory().remove(new ItemStack(Material.PAPER, 1));
		
		Zone zone = new Zone(x2 - x1, y2 - y1, z2 - z1);
		
		for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); ++x)
		{
			for (int z = Math.min(y1, y2); z <= Math.max(z1, z2); ++z)
			{
				for (int y = Math.min(y1, y2); x <= Math.max(y1, y2); ++y)
				{
					int zoneX = x - Math.min(x1, x2);
					int zoneZ = z - Math.min(z1, z2);
					int zoneY = y - Math.min(y1, y2);
					CraftBlock block = (CraftBlock) player.getWorld().getBlockAt(x, y, z);
					zone.setMaterialAndData(zoneX, zoneY, zoneZ, block.getType(), block.getData());
					CraftBlockState state = (CraftBlockState) block.getState();
					if (state.getTileEntity() != null)
					{
						NBTTagCompound tag = new NBTTagCompound();
						state.getTileEntity().b(tag);
						zone.setNBTTag(zoneX, zoneY, zoneZ, tag);
					}
				}
			}
		}
		
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = new net.minecraft.server.v1_8_R3.ItemStack(Items.PAPER);
		NBTTagCompound nbt = new NBTTagCompound();
		nmsStack.c(zone.getTag(nbt));
		nbt.setString("Name", ChatColor.AQUA + patternName);
		ItemStack stack = CraftItemStack.asBukkitCopy(nmsStack);
		player.getInventory().addItem(stack);
		player.sendMessage(ChatColor.GOLD + "Pattern cr\u00e9\u00e9 !");
		
		return true;
	}
}
