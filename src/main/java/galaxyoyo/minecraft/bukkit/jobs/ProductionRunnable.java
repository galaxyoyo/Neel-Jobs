package galaxyoyo.minecraft.bukkit.jobs;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ProductionRunnable extends BukkitRunnable
{
	private final JobsPlugin plugin;
	
	public ProductionRunnable(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run()
	{
		for (Entry<Location, Integer> entry : plugin.pepitesPerDay.entrySet())
		{
			Block block = entry.getKey().getBlock();
			if (block.getType() != Material.CHEST || entry.getValue().intValue() == 0)
			{
				plugin.pepitesPerDay.remove(entry.getKey());
				continue;
			}
			CraftChest state = (CraftChest) block.getState();
			int all = entry.getValue().intValue();
			while (all > 0)
			{
				int stack = Math.min(all, 64);
				state.getBlockInventory().addItem(new ItemStack(Material.GOLD_NUGGET, stack));
				all -= stack;
			}
		}
		ProductionRunnable run = new ProductionRunnable(plugin);
		run.runTaskLater(plugin, 1728000);
	}
}
