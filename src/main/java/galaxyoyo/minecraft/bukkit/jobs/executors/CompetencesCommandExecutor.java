package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.CompetenceTree;
import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;
import galaxyoyo.minecraft.bukkit.jobs.PhysicalCompetences;
import galaxyoyo.minecraft.bukkit.jobs.skills.ForgeSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.PhysicalSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompetencesCommandExecutor implements CommandExecutor, Listener
{
	private final JobsPlugin plugin;
	
	public CompetencesCommandExecutor(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs peuvent acc\u00e9der \u00e0 cette commande.");
			return true;
		}
		
		if (args.length > 0)
		{
			if (!sender.hasPermission("jobsrpg.command.competences.change"))
			{
				sender.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas la permission d'utiliser cette commande.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("add"))
			{
				if (args.length != 4)
				{
					sender.sendMessage(ChatColor.DARK_RED + "Usage : /comp add <player> [1 | 2] <number>");
					return true;
				}
				
				String playerName = args[1];
				OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
				if (op == null)
				{
					sender.sendMessage(ChatColor.RED + "Ce joueur est inconnu, ne s'est jamais inscrit sur ce serveur");
					return true;
				}
				
				int jobNumber;
				try
				{
					jobNumber = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException ex)
				{
					sender.sendMessage(ChatColor.RED + "Nombre invalide : '" + args[2] + "'");
					return true;
				}
				
				if (jobNumber < 1 || jobNumber > 2)
				{
					sender.sendMessage(ChatColor.RED + "Seuls deux jobs sont pr\u00e9sents.");
					return true;
				}
				
				int pts;
				try
				{
					pts = Integer.parseInt(args[3]);
				}
				catch (NumberFormatException ex)
				{
					sender.sendMessage(ChatColor.RED + "Nombre invalide : '" + args[3] + "'");
					return true;
				}
				
				Job job = plugin.getJobs(op)[jobNumber - 1];
				CompetenceTree tree = CompetenceTree.get(Bukkit.getOfflinePlayer(playerName));
				tree.addCompetencePoint(job, pts);
				
				if (Bukkit.getServer().getPlayer(playerName) != null)
				{
					CraftPlayer dest = (CraftPlayer) Bukkit.getServer().getPlayer(playerName);
					dest.sendMessage(ChatColor.AQUA + "Vous avez " + (pts > 0 ? "reçu" : "perdu") + " "
					+ Math.abs(pts) + " point" + (Math.abs(pts) > 1 ? "s" : "") + " pour le job " + job.getDisplay());
				}
				
				sender.sendMessage(ChatColor.GOLD + "Point" + (Math.abs(pts) > 1 ? "s" : "") + " attribu\u00e9"
						 + (Math.abs(pts) > 1 ? "s" : "") + " !");
				return true;
			}
			else if (args[0].equalsIgnoreCase("physic"))
			{
				if (args.length != 5)
				{
					sender.sendMessage(ChatColor.RED + "Usage : /comp physic add <player> <compId> <number>");
					return true;
				}
				
				if (args[1].equalsIgnoreCase("add"))
				{
					String playerName = args[2];
					if (!CompetenceTree.contains(playerName))
					{
						sender.sendMessage(ChatColor.RED + "Le joueur '" + playerName + "' ne s'est jamais connect\u00e9");
						return true;
					}
					
					int id, number;
					try
					{
						id = Integer.parseInt(args[3]);
					}
					catch (NumberFormatException ex)
					{
						sender.sendMessage(ChatColor.RED + "'" + args[3] + "' n'est pas un nombre valide");
						return true;
					}
					
					if (id < 0 || id >= PhysicalCompetences.getAvailableSkills().size())
					{
						sender.sendMessage(ChatColor.RED + "ID invalide");
						return true;
					}
					
					try
					{
						number = Integer.parseInt(args[4]);
					}
					catch (NumberFormatException ex)
					{
						sender.sendMessage(ChatColor.RED + "'" + args[4] + "' n'est pas un nombre valide");
						return true;
					}
					
					CompetenceTree tree = CompetenceTree.get(Bukkit.getOfflinePlayer(playerName));
					PhysicalCompetences physical = tree.getPhysical();
					PhysicalSkill skill = physical.getSkills().get(id);
					skill.setPoints(skill.getPoints() + number);
					if (Bukkit.getServer().getPlayer(playerName) != null)
					{
						if (!skill.isActive())
							skill.reset(Bukkit.getServer().getPlayer(playerName));
					}
					sender.sendMessage(ChatColor.GOLD + "Op\u00e9ration effectu\u00e9e !");
					return true;
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Usage : /comp physic add <compId> <number>");
					return true;
				}
			}
		}
		
		CraftPlayer player = (CraftPlayer) sender;
		
		displayCompsInventory(player);
		
		return true;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (event.getInventory().getTitle().equalsIgnoreCase("Comp\u00e9tences"))
		{
			event.setCancelled(true);
			
			if (!(event.getWhoClicked() instanceof Player))
				return;
			
			CraftPlayer player = (CraftPlayer) event.getWhoClicked();
			if (event.getSlot() == 0)
			{
				player.closeInventory();
				displayPhysicalCompInventory(player);
			}
			else if (event.getSlot() == 3)
			{
				player.closeInventory();
				Job job = plugin.getJobs(player)[0];
				displayJobCompsInventory(player, job);
			}
			else if (event.getSlot() == 5)
			{
				player.closeInventory();
				Job job = plugin.getJobs(player)[1];
				displayJobCompsInventory(player, job);
			}
		}
		else if (event.getInventory().getTitle().startsWith(ChatColor.DARK_GREEN + "Comp\u00e9tences de"))
		{
			event.setCancelled(true);
			
			if (!(event.getWhoClicked() instanceof Player))
				return;
			
			CraftPlayer player = (CraftPlayer) event.getWhoClicked();
			if (event.getSlot() < 9 && event.getCurrentItem() != null
					&& event.getCurrentItem().getType() == Material.WOOL)
			{
				if (event.getCurrentItem().getDurability() == 4)
				{
					if (event.getSlot() == 4)
						return;
					
					CompetenceTree tree = CompetenceTree.get(player);
					String jobName = event.getInventory().getTitle().substring(19).toLowerCase();
					Job job = null;
					for (Job j : Job.values())
					{
						if (j.name().toLowerCase().contains(jobName) || j.getDisplay().toLowerCase().contains(jobName)
								|| j.getRawPrefix().toLowerCase().contains(jobName))
						{
							job = j;
							break;
						}
					}
					
					Skill skill;
					if (event.getSlot() > 4)
					{
						skill = job.getAvailableActiveSkills().get(event.getSlot() - 5);
						tree.addActiveSkill(job, skill);
						tree.removeSkillToDeliver(job);
						
						if (skill instanceof ForgeSkill)
							skill.execute(player);
					}
					else
					{
						skill = job.getAvailablePassiveSkills().get(event.getSlot());
						tree.addPassiveSkill(job, skill);
						tree.removeSkillToDeliver(job);
						skill.execute(player);
					}
					
					player.sendMessage(ChatColor.GREEN + "Comp\u00e9tence d\u00e9bloqu\u00e9e :");
					player.sendMessage(skill.getDescription().split("\n\r"));
					player.closeInventory();
					displayJobCompsInventory(player, job);
				}
				else if (event.getCurrentItem().getDurability() == 5)
				{
					player.sendMessage(ChatColor.GREEN + "Vous avez d\u00e9j\u00e0 d\u00e9bloqu\u00e9 cette comp\u00e9tence !");
				}
				else if (event.getCurrentItem().getDurability() == 14)
				{
					player.sendMessage(ChatColor.RED + "Vous n'avez pas encore d\u00e9bloqu\u00e9 cette comp\u00e9tence");
				}
			}
			else if (event.getSlot() == 17)
			{
				player.closeInventory();
				displayCompsInventory(player);
			}
		}
		else if (event.getInventory().getTitle().contains("physique"))
		{
			event.setCancelled(true);
			
			if (!(event.getWhoClicked() instanceof Player))
				return;
			
			CraftPlayer player = (CraftPlayer) event.getWhoClicked();
			PhysicalCompetences physic = CompetenceTree.get(player).getPhysical();
			if (event.getSlot() < physic.getSkills().size())
			{
				PhysicalSkill skill = physic.getSkills().get(event.getSlot());
				short damage = event.getCurrentItem().getDurability();
				if (damage == 14)
				{
					player.sendMessage(ChatColor.RED + "Vous ne pouvez pas activer cette comp\u00e9tence !");
				}
				else if (damage == 4)
				{
					skill.activate();
					skill.setActive(true);
					skill.execute(player);
					player.sendMessage(ChatColor.GREEN + "Comp\u00e9tence activ\u00e9e !");
				}
				else if (damage == 5)
				{
					skill.desactivate();
					skill.reset(player);
					player.sendMessage(ChatColor.GREEN + "Comp\u00e9tence d\u00e9sactiv\u00e9e !");
				}
				player.closeInventory();
				displayPhysicalCompInventory(player);
			}
			else if (event.getSlot() == 17)
			{
				player.closeInventory();
				displayCompsInventory(player);
			}
		}
	}
	
	public void displayCompsInventory(CraftPlayer player)
	{
		CompetenceTree tree = CompetenceTree.get(player);
		Job[] jobs = plugin.getJobs(player);
		
		Inventory inv = Bukkit.createInventory(player, 9, "Comp\u00e9tences");
		
		ItemStack physicStack = new ItemStack(Material.IRON_BOOTS);
		physicStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta meta = physicStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(ChatColor.DARK_GRAY + "Comp\u00e9tences physiques");
		physicStack.setItemMeta(meta);
		inv.setItem(0, physicStack);
		
		if (jobs[0] != Job.CHOMEUR)
		{
			ItemStack job1Stack = new ItemStack(jobs[0].getSymbol());
			job1Stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			meta = job1Stack.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			String str = "Comp\u00e9tences de " + jobs[0].getPrefix().replaceAll("\\[|\\]", "");
			str = str.substring(0, str.length() - 1);
			meta.setDisplayName(ChatColor.GREEN + str);
			job1Stack.setItemMeta(meta);
			inv.setItem(3, job1Stack);
		}
		
		if (jobs[1] != Job.CHOMEUR)
		{
			ItemStack job2Stack = new ItemStack(jobs[1].getSymbol());
			job2Stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			meta = job2Stack.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setDisplayName(ChatColor.GREEN + "Comp\u00e9tences de " + jobs[1].getRawPrefix());
			job2Stack.setItemMeta(meta);
			inv.setItem(5, job2Stack);
		}
		
		ItemStack illPts = new ItemStack(Material.GOLD_NUGGET);
		meta = illPts.getItemMeta();
		int pts = tree.getIllegalityPoints();
		meta.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + ChatColor.MAGIC + "   "
		+ ChatColor.RESET + " " + ChatColor.GOLD + pts + " point" + (pts > 1 ? "s" : "")
		+ " d'ill\u00e9galit\u00e9 " + ChatColor.AQUA + ChatColor.MAGIC + "   ");
		illPts.setItemMeta(meta);
		inv.setItem(8, illPts);
		
		player.openInventory(inv);
	}
	
	public void displayJobCompsInventory(CraftPlayer player, Job job)
	{
		CompetenceTree tree = CompetenceTree.get(player);
		
		Inventory inv = Bukkit.getServer().createInventory(player, 18, ChatColor.DARK_GREEN
				+ "Comp\u00e9tences de " + job.getRawPrefix());
		
		int i = 0;
		while (i < job.getAvailablePassiveSkills().size())
		{
			Skill skill = job.getAvailablePassiveSkills().get(i);
			ItemStack stack = new ItemStack(Material.WOOL);
			ItemMeta meta = stack.getItemMeta();
			if (tree.hasPassive(job, skill))
			{
				stack.setDurability((short) 5);
				meta.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + ChatColor.MAGIC + "   "
				+ ChatColor.RESET + ChatColor.GREEN + " Comp\u00e9tence d\u00e9bloqu\u00e9e "
						+ ChatColor.AQUA + ChatColor.MAGIC + "   ");
			}
			else
			{
				stack.setDurability((short) 14);
				meta.setDisplayName(ChatColor.RESET + "" + ChatColor.DARK_RED + ChatColor.MAGIC + "   "
				+ ChatColor.RESET + ChatColor.RED + " Comp\u00e9tence non d\u00e9bloqu\u00e9e "
						+ ChatColor.DARK_RED + ChatColor.MAGIC + "   ");
			}
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "Comp\u00e9tence passive");
			List<String> desc = Arrays.asList(skill.getDescription().split("\n\r"));
			desc.set(0, ChatColor.AQUA + "Bonus : " + ChatColor.LIGHT_PURPLE + desc.get(0));
			for (int j = 1; j < desc.size(); ++j)
				desc.set(j, ChatColor.LIGHT_PURPLE + desc.get(j));
			lore.addAll(desc);
			meta.setLore(lore);
			stack.setItemMeta(meta);
			inv.setItem(i, stack);
			
			++i;
		}
		
		if (tree.getSkillsToDeliver(job) >= 1
				&& tree.getPassiveSkills(job).size() < job.getAvailablePassiveSkills().size())
		{
			ItemStack stack = inv.getItem(tree.getPassiveSkills(job).size());
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + "D\u00e9bloquer comp\u00e9tence");
			stack.setItemMeta(meta);
			stack.setDurability((short) 4);
			inv.setItem(tree.getPassiveSkills(job).size(), stack);
		}
		
		i = 0;
		while (i < job.getAvailableActiveSkills().size())
		{
			Skill skill = job.getAvailableActiveSkills().get(i);
			ItemStack stack = new ItemStack(Material.WOOL);
			ItemMeta meta = stack.getItemMeta();
			if (tree.hasActive(job, skill))
			{
				stack.setDurability((short) 5);
				meta.setDisplayName(ChatColor.RESET + "" + ChatColor.AQUA + ChatColor.MAGIC + "   "
				+ ChatColor.RESET + ChatColor.GREEN + " Comp\u00e9tence d\u00e9bloqu\u00e9e "
						+ ChatColor.AQUA + ChatColor.MAGIC + "   ");
			}
			else
			{
				stack.setDurability((short) 14);
				meta.setDisplayName(ChatColor.RESET + "" + ChatColor.DARK_RED + ChatColor.MAGIC + "   "
				+ ChatColor.RESET + ChatColor.RED + " Comp\u00e9tence bloqu\u00e9e "
						+ ChatColor.DARK_RED + ChatColor.MAGIC + "   ");
			}
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.RED + "Comp\u00e9tence active");
			List<String> desc = Arrays.asList(skill.getDescription().split("\n\r"));
			desc.set(0, ChatColor.AQUA + "Bonus : " + ChatColor.LIGHT_PURPLE + desc.get(0));
			for (int j = 1; j < desc.size(); ++j)
				desc.set(j, ChatColor.LIGHT_PURPLE + desc.get(j));
			lore.addAll(desc);
			lore.add(ChatColor.DARK_RED + "Cooldown : " + ChatColor.BLUE
					+ skill.getFormattedTime(skill.getBaseCooldown()));
			meta.setLore(lore);
			stack.setItemMeta(meta);
			inv.setItem(5 + i, stack);
			
			++i;
		}
		
		if (tree.getSkillsToDeliver(job) >= 1
				&& tree.getActiveSkills(job).size() < job.getAvailableActiveSkills().size())
		{
			ItemStack stack = inv.getItem(5 + tree.getActiveSkills(job).size());
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.YELLOW + "D\u00e9bloquer comp\u00e9tence");
			stack.setItemMeta(meta);
			stack.setDurability((short) 4);
			inv.setItem(5 + tree.getActiveSkills(job).size(), stack);
		}
		
		ItemStack ptsStack = new ItemStack(job.getSymbol());
		ItemMeta ptsMeta = ptsStack.getItemMeta();
		int pts = tree.getCompetencePts(job);
		int max = tree.getCompetencePtsMax(job);
		ptsMeta.setDisplayName(ChatColor.GOLD + "" + pts + " point" + (pts > 1 ? "s" : "") + " / " + max);
		ptsMeta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "Niveau " + tree.getLevel(job),
				ChatColor.GREEN + "" + tree.getPassiveSkills(job).size() + " comp\u00e9tence"
				+ (tree.getPassiveSkills(job).size() > 1 ? "s" : "") + " passive"
				+ (tree.getPassiveSkills(job).size() > 1 ? "s" : "") + " d\u00e9bloqu\u00e9e"
				+ (tree.getPassiveSkills(job).size() > 1 ? "s" : ""), ChatColor.RED + ""
				+ tree.getActiveSkills(job).size() + " comp\u00e9tence"
				+ (tree.getActiveSkills(job).size() > 1 ? "s" : "") + " active"
				+ (tree.getActiveSkills(job).size() > 1 ? "s" : "") + " d\u00e9bloqu\u00e9e"
				+ (tree.getActiveSkills(job).size() > 1 ? "s" : ""), ChatColor.BLUE + ""
				+ tree.getSkillsToDeliver(job) + ChatColor.AQUA + " point"
				+ (tree.getSkillsToDeliver(job) > 1 ? "s" : "") + " de comp\u00e9tence \u00e0 attribuer",
				ChatColor.YELLOW + "Pour augmenter vos points de comp\u00e9tences :",
				ChatColor.YELLOW + job.getHowEarnPoints()));
		ptsStack.setItemMeta(ptsMeta);
		inv.setItem(9, ptsStack);
		
		ItemStack backStack = new ItemStack(Material.WOOD_DOOR);
		ItemMeta backMeta = backStack.getItemMeta();
		backMeta.setDisplayName(ChatColor.AQUA + "Retour");
		backStack.setItemMeta(backMeta);
		inv.setItem(17, backStack);
		
		player.openInventory(inv);
	}
	
	public void displayPhysicalCompInventory(Player player)
	{
		Inventory inv = Bukkit.createInventory(player, 18, ChatColor.GRAY + "Comp\u00e9tences physiques");
		
		CompetenceTree tree = CompetenceTree.get(player);
		PhysicalCompetences physic = tree.getPhysical();
		
		int i = 0;
		while (i < physic.getSkills().size())
		{
			PhysicalSkill skill = physic.getSkills().get(i);
			ItemStack stack = new ItemStack(Material.WOOL);
			ItemMeta meta = stack.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.setLore(Arrays.asList(skill.getDescription().split("\n\r")));
			meta.setDisplayName(ChatColor.AQUA + "Comp\u00e9tence physique");
			stack.setItemMeta(meta);
			if (skill.isActive())
			{
				if (skill.isActivated())
					stack.setDurability((short) 5);
				else
					stack.setDurability((short) 4);
			}
			else
				stack.setDurability((short) 14);
			inv.setItem(i, stack);
			++i;
		}
		
		ItemStack backStack = new ItemStack(Material.WOOD_DOOR);
		ItemMeta backMeta = backStack.getItemMeta();
		backMeta.setDisplayName(ChatColor.AQUA + "Retour");
		backStack.setItemMeta(backMeta);
		inv.setItem(17, backStack);
		
		player.openInventory(inv);
	}
}
