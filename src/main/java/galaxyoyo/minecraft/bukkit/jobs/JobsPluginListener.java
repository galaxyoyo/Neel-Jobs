package galaxyoyo.minecraft.bukkit.jobs;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.CHEST;
import static org.bukkit.Material.COAL;
import static org.bukkit.Material.COMPASS;
import static org.bukkit.Material.LAVA;
import static org.bukkit.Material.LOG;
import static org.bukkit.Material.LOG_2;
import static org.bukkit.Material.STATIONARY_LAVA;
import static org.bukkit.Material.STATIONARY_WATER;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.STRING;
import static org.bukkit.Material.WALL_SIGN;
import static org.bukkit.Material.WATER;
import static org.bukkit.Material.WOOD;
import static org.bukkit.block.BlockFace.DOWN;
import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SELF;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.UP;
import static org.bukkit.block.BlockFace.WEST;
import galaxyoyo.minecraft.bukkit.jobs.skills.PhysicalSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBreakingSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBrokenSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockStopBreakingSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.EntityDamageSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.FurnaceSmeltSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.MobDamageSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.MobSpawnSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractEntitySkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerMoveSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PrepareCraftSkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import net.minecraft.server.v1_8_R3.PacketPlayOutExperience;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSpider;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.google.common.collect.Lists;

public class JobsPluginListener implements Listener
{
	private final JobsPlugin plugin;
	
	public String lastSpeaker;
	
	public JobsPluginListener(JobsPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		if (plugin.mine.isGenerating())
		{
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "La mine se r\u00e9g\u00e9n\u00e8re ! Veuillez patienter");
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		List<Result> results = Lists.newArrayList();
		for (Zone zone : plugin.getZones())
		{
			Result result = zone.onPlayerMove(event);
			if (result != null)
				results.add(result);
		}
		if (!results.isEmpty())
		{
			if (!results.contains(Result.ALLOW))
			{
				event.setTo(event.getFrom());
				return;
			}
		}
		
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		
		for (InfinityItem ii : plugin.infinityItems.values())
		{
			if (ii.getClearZone() != null && ii.getClearZone().distance(player.getLocation()) <= ii.getClearRadius())
			{
				ItemStack item = ii.getItem();
				int cb = 0;
				for (ItemStack stack : player.getInventory().getContents())
				{
					if (stack != null && stack.getType() == item.getType() && stack.getDurability() == item.getDurability())
					{
						cb += stack.getAmount();
						player.getInventory().remove(stack);
					}
				}
				
				if (cb > 0)
					player.sendMessage(ChatColor.GOLD + "" + cb + " item" + (cb > 1 ? "s" : "")
									+ " supprim\u00e9" + (cb > 1 ? "s" : "") + " !"); 
			}
		}
		
		CompetenceTree tree = CompetenceTree.get(player);
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof PlayerMoveSkill)
				((PlayerMoveSkill) skill).onPlayerMove(event);
		}
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof PlayerMoveSkill)
				((PlayerMoveSkill) skill).onPlayerMove(event);
		}
		for (PhysicalSkill skill : tree.getPhysical().getSkills())
		{
			if (skill.getListenerClassName().equalsIgnoreCase(PlayerMoveSkill.class.getName()))
				skill.onPlayerMove(event);
		}
	}
	
	@EventHandler
	public void onBlockDamaged(BlockDamageEvent event)
	{
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		CompetenceTree tree = CompetenceTree.get(player);
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		boolean isLog;
		switch (event.getBlock().getType())
		{
		case LOG:
		case LOG_2:
			isLog = true;
			if (!jobs.contains(Job.WOODCUTTER) && !jobs.contains(Job.MASON))
				player.sendMessage(ChatColor.RED + "Vous n'\u00eates pas Bûcheron ! Il est ill\u00e9gal de couper du bois sans permission pr\u00e9alable !");
			break;
		default:
			isLog = false;
			break;
		}
		
		if (isLog)
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));
		
		final boolean finalIsLog = isLog;
		final Player finalPlayer = player;
		final CompetenceTree finalTree = tree;
		final BlockDamageEvent finalEvent = event;
		
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof BlockBreakingSkill)
			{
				((BlockBreakingSkill) skill).onBlockBreaking(event);
			}
		}
		
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof BlockBreakingSkill)
			{
				((BlockBreakingSkill) skill).onBlockBreaking(event);
			}
		}
		
		if (isLog && !jobs.contains(Job.MASON))
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1));

		PacketAdapter pa = new PacketAdapter(plugin, PacketType.Play.Client.BLOCK_DIG)
		{
		    @Override
		    public void onPacketReceiving(PacketEvent ev)
		    {
		    	PlayerDigType type = ev.getPacket().getPlayerDigTypes().read(0);
		    	if (type == PlayerDigType.ABORT_DESTROY_BLOCK || type == PlayerDigType.STOP_DESTROY_BLOCK)
		    	{
		    		if (finalIsLog)
		    			finalPlayer.removePotionEffect(PotionEffectType.SLOW_DIGGING);
		    		ProtocolLibrary.getProtocolManager().removePacketListener(this);
		    		
		    		for (Skill skill : finalTree.getAllPassiveSkills())
		    		{
		    			if (skill instanceof BlockStopBreakingSkill)
		    			{
		    				((BlockStopBreakingSkill) skill).onBlockStopBreaking(finalEvent);
		    			}
		    		}
		    		
		    		for (Skill skill : finalTree.getAllActiveSkills())
		    		{
		    			if (skill instanceof BlockStopBreakingSkill)
		    			{
		    				((BlockStopBreakingSkill) skill).onBlockStopBreaking(finalEvent);
		    			}
		    		}
		    	}
		    }
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(pa);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		List<Result> results = Lists.newArrayList();
		for (Zone zone : plugin.getZones())
		{
			Result result = zone.onBlockPlace(event);
			if (result != null)
				results.add(result);
		}
		if (!results.isEmpty())
		{
			if (!results.contains(Result.ALLOW))
			{
				event.setCancelled(true);
				return;
			}
		}
		
		if (event.getBlockPlaced().getType() == CHEST)
		{
			BlockFace[] faces = {NORTH, WEST, SOUTH, EAST};
			for (BlockFace face : faces)
			{
				Block relative = event.getBlock().getRelative(face);
				if (relative.getType() == CHEST)
				{
					if (plugin.pepitesPerDay.containsKey(relative.getLocation()))
					{
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez placer un coffre ici :"
										+ " un coffre de revenus est adjacent, ce qui fait donc que vous ne"
										+ " pourriez plus miner votre coffre. Si vous voulez agrandir, demandez un admin");
						return;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		
		List<Result> results = Lists.newArrayList();
		for (Zone zone : plugin.getZones())
		{
			Result result = zone.onBlockBreak(event);
			if (result != null)
				results.add(result);
		}
		if (!results.isEmpty())
		{
			if (!results.contains(Result.ALLOW))
			{
				event.setCancelled(true);
				return;
			}
		}
		
		Location loc = event.getBlock().getLocation();
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		InfinityItem ii = plugin.infinityItems.get(loc);
		if (ii != null)
		{
			player.sendMessage(ChatColor.RED + "Pour obtenir vos items, faîtes un clic droit");
			event.setCancelled(true);
		}
		
		if ((event.getBlock().getType() == Material.SIGN || event.getBlock().getType() == Material.WALL_SIGN)
						&& (loc.equals(plugin.mine.getCoalSignLocation())
						|| loc.equals(plugin.mine.getIronSignLocation())
						|| loc.equals(plugin.mine.getGoldSignLocation())
						|| loc.equals(plugin.mine.getLapisSignLocation())
						|| loc.equals(plugin.mine.getRedstoneSignLocation())))
		{
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "Vous ne pouvez d\u00e9truire cette pancarte");
		}
		
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		
		CompetenceTree tree = CompetenceTree.get(player);
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		boolean isLog;
		switch (event.getBlock().getType())
		{
		case LOG:
		case LOG_2:
			isLog = true;
			if (jobs.contains(Job.CARPENTER) && tree.getActiveSkills(Job.CARPENTER).size() >= 2
							&& tree.getActiveSkills(Job.CARPENTER).get(1).isActivated())
				break;
			if (jobs.contains(Job.WOODCUTTER))
				CompetenceTree.get(player).addCompetencePoint(Job.WOODCUTTER, 1);
			else if (!jobs.contains(Job.MASON))
				CompetenceTree.get(player).addIllegalityPoint();
			break;
		case COBBLESTONE:
		case STONE:
			isLog = false;
			if (jobs.contains(Job.STONECUTTER) && tree.getActiveSkills(Job.STONECUTTER).size() >= 2
							&& tree.getActiveSkills(Job.STONECUTTER).get(1).isActivated())
				break;
		case COAL_ORE:
		case IRON_ORE:
		case GOLD_ORE:
		case DIAMOND_ORE:
		case EMERALD_ORE:
		case REDSTONE_ORE:
		case LAPIS_ORE:
			if (jobs.contains(Job.MINER))
				CompetenceTree.get(player).addCompetencePoint(Job.MINER, 1);
			else
				CompetenceTree.get(player).addIllegalityPoint();
			isLog = false;
			break;
		case CROPS:
		case CARROT:
		case POTATO:
			if (jobs.contains(Job.FARMER) && event.getBlock().getData() == 7)
				CompetenceTree.get(player).addCompetencePoint(Job.FARMER, 1);
		default:
			isLog = false;
			break;
		}
		
		if (event.getBlock().getType() == CHEST)
		{
			if (plugin.pepitesPerDay.containsKey(event.getBlock().getLocation()))
			{
				if (player.hasPermission("jobsrpg.blocks.breakProdMoney"))
				{
					plugin.pepitesPerDay.remove(event.getBlock().getLocation());
					BlockFace[] faces = {NORTH, WEST, SOUTH, EAST};
					for (BlockFace face : faces)
					{
						Block relative = event.getBlock().getRelative(face);
						if (relative.getType() == WALL_SIGN)
						{
							Sign sign = (Sign) relative.getState();
							if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
								relative.setType(AIR);
						}
						else if (relative.getType() == CHEST)
						{
							if (plugin.pepitesPerDay.containsKey(relative.getLocation()))
								plugin.pepitesPerDay.remove(relative.getLocation());
							List<BlockFace> facesList = new ArrayList<BlockFace>(Arrays.asList(faces));
							facesList.remove(face.getOppositeFace());
							for (BlockFace f : facesList)
							{
								Block rel = relative.getRelative(f);
								if (rel.getType() == WALL_SIGN)
								{
									Sign sign = (Sign) rel.getState();
									if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
										rel.setType(AIR);
								}
							}
							relative.breakNaturally();
						}
					}
				}
				else
				{
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "Vous n'avez pas le droit de casser un coffre de "
									+ "production, veuillez demander un admin s'il vous appartient");
				}
			}
			else
			{
				BlockFace[] faces = {NORTH, WEST, SOUTH, EAST};
				for (BlockFace face : faces)
				{
					Block relative = event.getBlock().getRelative(face);
					if (relative.getType() == WALL_SIGN)
					{
						Sign sign = (Sign) relative.getState();
						if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
							relative.setType(AIR);
					}
					else if (relative.getType() == CHEST)
					{
						if (plugin.pepitesPerDay.containsKey(relative.getLocation()))
						{
							if (player.hasPermission("jobsrpg.blocks.breakProdMoney"))
							{
								plugin.pepitesPerDay.remove(relative.getLocation());
								for (BlockFace _face : faces)
								{
									Block _relative = relative.getRelative(_face);
									if (_relative.getType() == WALL_SIGN)
									{
										Sign sign = (Sign) _relative.getState();
										if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
											_relative.setType(AIR);
									}
									else if (_relative.getType() == CHEST)
									{
										if (plugin.pepitesPerDay.containsKey(_relative.getLocation()))
											plugin.pepitesPerDay.remove(_relative.getLocation());
										List<BlockFace> facesList = new ArrayList<BlockFace>(Arrays.asList(faces));
										facesList.remove(_face.getOppositeFace());
										for (BlockFace f : facesList)
										{
											Block rel = _relative.getRelative(f);
											if (rel.getType() == WALL_SIGN)
											{
												Sign sign = (Sign) rel.getState();
												if (sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "Propri\u00e9taire"))
													rel.setType(AIR);
											}
										}
									}
								}
								relative.breakNaturally();
							}
							else
							{
								event.setCancelled(true);
								player.sendMessage(ChatColor.RED + "Vous n'avez pas le droit de casser un coffre de "
												+ "production, veuillez demander un admin s'il vous appartient");
							}
						}
					}
				}
			}
		}
		else if (event.getBlock().getType() == WALL_SIGN)
		{
			BlockFace signFace;
			switch (event.getBlock().getData())
			{
				case 3 :
					signFace = NORTH;
					break;
				case 4 :
					signFace = EAST;
					break;
				case 5 :
					signFace = WEST;
					break;
				default :
					signFace = SOUTH;
					break;
			}
			
			if (event.getBlock().getRelative(signFace).getType() == CHEST)
			{
				List<BlockFace> faces = new ArrayList<BlockFace>(Arrays.asList(SELF, NORTH, WEST, SOUTH, EAST));
				
				for (BlockFace face : faces)
				{
					Block relative = event.getBlock().getRelative(signFace).getRelative(face);
					if (relative.getType() == CHEST)
					{
						loc = relative.getLocation();
						loc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
						if (plugin.pepitesPerDay.containsKey(loc))
						{
							event.getPlayer().sendMessage(ChatColor.RED + "Le coffre coll\u00e9 r\u00e9cup\u00e8re de l'argent quotidiennement. "
											+ "Veuillez demander \u00e0 un admin pour le supprimer.");
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof BlockBrokenSkill)
			{
				((BlockBrokenSkill) skill).onBlockBroken(event);
			}
		}
		
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof BlockBrokenSkill)
			{
				((BlockBrokenSkill) skill).onBlockBroken(event);
			}
		}
		

		for (PhysicalSkill skill : tree.getPhysical().getSkills())
		{
			if (skill.getListenerClassName().equalsIgnoreCase(BlockBrokenSkill.class.getName()))
				skill.onBlockBroken(event);
		}
		
		if (isLog && !jobs.contains(Job.MASON))
		{
			player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
			breakLog(event.getBlock(), player);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
						&& event.getClickedBlock().getType() != CHEST
						&& event.getClickedBlock().getType() != Material.WOODEN_DOOR
						&& event.getClickedBlock().getType() != Material.SPRUCE_DOOR
						&& event.getClickedBlock().getType() != Material.BIRCH_DOOR
						&& event.getClickedBlock().getType() != Material.JUNGLE_DOOR
						&& event.getClickedBlock().getType() != Material.DARK_OAK_DOOR
						&& event.getClickedBlock().getType() != Material.ACACIA_DOOR
						&& event.getClickedBlock().getType() != Material.ENDER_CHEST)
		{
			List<Result> results = Lists.newArrayList();
			for (Zone zone : plugin.getZones())
			{
				Result result = zone.onPlayerInteract(event);
				if (result != null)
					results.add(result);
			}
			if (!results.isEmpty())
			{
				if (!results.contains(Result.ALLOW))
				{
					event.setCancelled(true);
					return;
				}
			}
		}
		
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Location loc = event.getClickedBlock().getLocation();
			loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			InfinityItem ii = plugin.infinityItems.get(loc);
			if (ii != null && ii.isAllowed(player))
			{
				Inventory inv = Bukkit.getServer().createInventory(player, 27,
								LocaleI18n.get(CraftMagicNumbers.getItem(ii.getItem().getType()).getName() + ".name"));
				ItemStack stack = ii.getItem().clone();
				stack.setAmount(stack.getMaxStackSize());
				int i = 0;
				while (i < inv.getSize())
				{
					inv.setItem(i, stack.clone());
					++i;
				}
				player.openInventory(inv);
			}
		}
		
		CompetenceTree tree = CompetenceTree.get(player);
		
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof PlayerInteractSkill)
				((PlayerInteractSkill) skill).onPlayerInteract(event);
		}
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof PlayerInteractSkill)
				((PlayerInteractSkill) skill).onPlayerInteract(event);
		}
		
		if (event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		if (event.getItem() != null)
		{
			if (jobs.contains(Job.WOODCUTTER))
			{
				boolean ok;
				switch (event.getMaterial())
				{
				case WOOD_AXE:
				case STONE_AXE:
				case IRON_AXE:
				case GOLD_AXE:
				case DIAMOND_AXE:
					ok = true;
					break;
				default:
					ok = false;
					break;
				}
				
				if (ok)
				{
					int size = tree.getActiveSkills(Job.WOODCUTTER).size();
					Skill skill1 = Job.WOODCUTTER.getAvailableActiveSkills().get(0);
					Skill skill2 = Job.WOODCUTTER.getAvailableActiveSkills().get(1);
					Skill skill3 = Job.WOODCUTTER.getAvailableActiveSkills().get(2);
					Skill skill4 = Job.WOODCUTTER.getAvailableActiveSkills().get(3);
					
					if (size == 4 && tree.hasActive(Job.WOODCUTTER, skill4))
					{
						Skill cloned4 = tree.getActiveCloned(Job.WOODCUTTER, skill4);
						if (cloned4.getCooldown() > 0)
						{
							player.sendMessage("Merci de patienter " + cloned4.getFormattedTime(cloned4.getCooldown()) + " avant de faire ceci.");
							return;
						}
						
						Skill cloned3 = tree.getActiveCloned(Job.WOODCUTTER, skill3);
						if (cloned3.getCooldown() > 0)
						{
							if (!cloned4.isActivated())
							{
								cloned4.activate();
								cloned4.execute(player);
								player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 4 activ\u00e9e !");
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
							}
							return;
						}
					}
					
					if (size >= 3 && tree.hasActive(Job.WOODCUTTER, skill3))
					{
						Skill cloned3 = tree.getActiveCloned(Job.WOODCUTTER, skill3);
						if (cloned3.getCooldown() > 0)
						{
							player.sendMessage("Merci de patienter " + cloned3.getFormattedTime(cloned3.getCooldown()) + " avant de faire ceci.");
							return;
						}
						else
						{
							if (!cloned3.isActivated())
							{
								cloned3.activate();
								cloned3.execute(player);
								player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 3 activ\u00e9e !");
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
							}
							return;
						}
					}
					else
					{
						if (size >= 2 && tree.hasActive(Job.WOODCUTTER, skill2))
						{
							Skill cloned2 = tree.getActiveCloned(Job.WOODCUTTER, skill2);
							if (cloned2.getCooldown() > 0)
							{
								player.sendMessage("Merci de patienter " + cloned2.getFormattedTime(cloned2.getCooldown()) + " avant de faire ceci.");
								return;
							}
							
							Skill cloned1 = tree.getActiveCloned(Job.WOODCUTTER, skill1);
							if (cloned1.getCooldown() > 0)
							{
								if (!cloned2.isActivated())
								{
									cloned2.activate();
									cloned2.execute(player);
									player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 2 activ\u00e9e !");
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
								}
								return;
							}
						}
						
						if (size >= 1 && tree.hasActive(Job.WOODCUTTER, skill1))
						{
							Skill cloned1 = tree.getActiveCloned(Job.WOODCUTTER, skill1);
							if (cloned1.getCooldown() > 0)
							{
								player.sendMessage("Merci de patienter " + cloned1.getFormattedTime(cloned1.getCooldown()) + " avant de faire ceci.");
								return;
							}
							else
							{
								if (!cloned1.isActivated())
								{
									cloned1.activate();
									cloned1.execute(player);
									player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 1 activ\u00e9e !");
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
								}
								return;
							}
						}
					}
				}
			}
			
			if (jobs.contains(Job.MINER))
			{
				boolean ok;
				switch (event.getMaterial())
				{
				case WOOD_PICKAXE:
				case STONE_PICKAXE:
				case IRON_PICKAXE:
				case GOLD_PICKAXE:
				case DIAMOND_PICKAXE:
					ok = true;
					break;
				default:
					ok = false;
					break;
				}
				
				if (ok)
				{
					int size = tree.getActiveSkills(Job.MINER).size();
					Skill skill1 = Job.MINER.getAvailableActiveSkills().get(0);
					Skill skill2 = Job.MINER.getAvailableActiveSkills().get(1);
					Skill skill3 = Job.MINER.getAvailableActiveSkills().get(2);
					Skill skill4 = Job.MINER.getAvailableActiveSkills().get(3);
					
					if (size == 4 && tree.hasActive(Job.MINER, skill4))
					{
						Skill cloned4 = tree.getActiveCloned(Job.MINER, skill4);
						if (cloned4.getCooldown() > 0)
						{
							player.sendMessage("Merci de patienter " + cloned4.getFormattedTime(cloned4.getCooldown()) + " avant de faire ceci.");
							return;
						}
						
						Skill cloned3 = tree.getActiveCloned(Job.MINER, skill3);
						if (cloned3.getCooldown() > 0)
						{
							if (!cloned4.isActivated())
							{
								cloned4.activate();
								cloned4.execute(player);
								player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 4 activ\u00e9e !");
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
							}
							return;
						}
					}
					
					if (size >= 3 && tree.hasActive(Job.MINER, skill3))
					{
						Skill cloned3 = tree.getActiveCloned(Job.MINER, skill3);
						if (cloned3.getCooldown() > 0)
						{
							player.sendMessage("Merci de patienter " + cloned3.getFormattedTime(cloned3.getCooldown()) + " avant de faire ceci.");
							return;
						}
						else
						{
							if (!cloned3.isActivated())
							{
								cloned3.activate();
								cloned3.execute(player);
								player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 3 activ\u00e9e !");
							}
							else
							{
								player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
							}
							return;
						}
					}
					else
					{
						if (size >= 2 && tree.hasActive(Job.MINER, skill2))
						{
							Skill cloned2 = tree.getActiveCloned(Job.MINER, skill2);
							if (cloned2.getCooldown() > 0)
							{
								player.sendMessage("Merci de patienter " + cloned2.getFormattedTime(cloned2.getCooldown()) + " avant de faire ceci.");
								return;
							}
							
							Skill cloned1 = tree.getActiveCloned(Job.MINER, skill1);
							if (cloned1.getCooldown() > 0)
							{
								if (!cloned2.isActivated())
								{
									cloned2.activate();
									cloned2.execute(player);
									player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 2 activ\u00e9e !");
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
								}
								return;
							}
						}
						
						if (size >= 1 && tree.hasActive(Job.MINER, skill1))
						{
							Skill cloned1 = tree.getActiveCloned(Job.MINER, skill1);
							if (cloned1.getCooldown() > 0)
							{
								player.sendMessage("Merci de patienter " + cloned1.getFormattedTime(cloned1.getCooldown()) + " avant de faire ceci.");
								return;
							}
							else
							{
								if (!cloned1.isActivated())
								{
									cloned1.activate();
									cloned1.execute(player);
									player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 1 activ\u00e9e !");
								}
								else
								{
									player.sendMessage(ChatColor.RED + "Cette comp\u00e9tence est d\u00e9j\u00e0 active !");
								}
								return;
							}
						}
					}
				}
			}
			
			if (jobs.contains(Job.HUNTER))
			{
				if (event.getItem().getType() == ARROW)
				{
					int size = tree.getActiveSkills(Job.HUNTER).size();
					Skill skill1 = Job.HUNTER.getAvailableActiveSkills().get(0);
					Skill skill3 = Job.HUNTER.getAvailableActiveSkills().get(2);
					Skill cloned = null;
					
					if (size >= 3)
						cloned = tree.getActiveCloned(Job.HUNTER, skill3);
					else if (size >= 1)
						cloned = tree.getActiveCloned(Job.HUNTER, skill1);
					
					if (cloned != null)
					{
						if (tree.getActiveSkills(Job.HUNTER).size() >= 2)
						{
							if (cloned.getCooldown() > 0)
							{
								player.sendMessage("Merci de patienter " + cloned.getFormattedTime(cloned.getCooldown()) + " avant de faire ceci.");
								return;
							}
							
							if (cloned.isActivated())
							{
								player.sendMessage("Cette comp\u00e9tence est d\u00e9ja activ\u00e9e !");
								return;
							}
							else
							{
								player.sendMessage("Comp\u00e9tence " + (size >= 3 ? "3" : "1") + " activ\u00e9e !");
								cloned.activate();
								cloned.execute(player);
							}
						}
					}
				}
				else if (event.getItem().getType() == COMPASS)
				{
					if (tree.getActiveSkills(Job.HUNTER).size() >= 2)
					{
						Skill skill = Job.HUNTER.getAvailableActiveSkills().get(1);
						Skill cloned = tree.getActiveCloned(Job.HUNTER, skill);
						if (cloned.getCooldown() > 0)
						{
							player.sendMessage("Merci de patienter " + cloned.getFormattedTime(cloned.getCooldown()) + " avant de faire ceci.");
							return;
						}
						
						if (!cloned.isActivated())
						{
							player.sendMessage("Comp\u00e9tence 2 activ\u00e9e !");
							cloned.activate();
							cloned.execute(player);
						}
					}
				}
				else if (event.getItem().getType() == STRING)
				{
					Skill skill = Job.HUNTER.getAvailableActiveSkills().get(3);
					
					if (tree.hasActive(Job.HUNTER, skill))
					{
						Skill cloned = tree.getActiveCloned(Job.HUNTER, skill);
						cloned.activate();
						cloned.execute(player);
					}
				}
			}
			
			if (jobs.contains(Job.STONECUTTER))
			{
				if (player.getItemInHand().getType() == COAL)
				{
					if (tree.getActiveSkills(Job.STONECUTTER).size() >= 4)
					{
						final Skill skill = tree.getActiveCloned(Job.STONECUTTER, Job.STONECUTTER.getAvailableActiveSkills().get(3));
						if (skill.getCooldown() > 0)
						{
							player.sendMessage(ChatColor.RED + "Veuillez patienter " + skill.getFormattedTime(skill.getCooldown())
											+ " avant de pouvoir faire ceci.");
							return;
						}
						
						skill.activate();
						skill.execute(player);
						player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 4 activ\u00e9e !");
						
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								skill.desactivate();
							}
						}.runTaskLater(plugin, 600L);
					}
				}
				
				boolean ok = false;
				switch (player.getItemInHand().getType())
				{
					case WOOD_PICKAXE:
					case STONE_PICKAXE:
					case IRON_PICKAXE:
					case GOLD_PICKAXE:
					case DIAMOND_PICKAXE:
						ok = true;
						break;
					default:
						ok = false;
						break;
				}
				
				if (ok)
				{
					if (tree.getActiveSkills(Job.STONECUTTER).size() >= 2)
					{
						final Skill skill = tree.getActiveSkills(Job.STONECUTTER).get(1);
						if (skill.getCooldown() > 0 || skill.isActivated())
						{
							player.sendMessage(ChatColor.RED + "Veuillez attendre " + skill.getFormattedTime(skill.getCooldown()) + " avant de pouvoir faire ceci.");
							return;
						}
						
						skill.activate();
						skill.execute(player);
						player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 2 activ\u00e9 !");
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								skill.desactivate();
							}
						}.runTaskLater(plugin, 12000);
					}
				}
			}
			
			if (jobs.contains(Job.CARPENTER))
			{
				if (player.getItemInHand().getType() == Material.LOG || player.getItemInHand().getType() == Material.LOG_2)
				{
					int size = tree.getActiveSkills(Job.CARPENTER).size();
					
					if (size >= 3)
					{
						Skill skill = tree.getActiveSkills(Job.CARPENTER).get(2);
						if (skill.getCooldown() > 0 || skill.isActivated())
						{
							player.sendMessage(ChatColor.RED + "Veuillez attendre " + skill.getFormattedTime(skill.getCooldown()) + " avant de pouvoir faire ceci.");
							return;
						}
						
						ItemStack stack = player.getItemInHand().clone();
						stack.setAmount(1);
						ItemStack result = stack.clone();
						stack.setAmount(18);
						stack.setType(Material.WOOD);
						player.getInventory().remove(stack);
						player.getInventory().addItem(result);
					}
					else if (size >= 1)
					{
						Skill skill = tree.getActiveSkills(Job.CARPENTER).get(0);
						if (skill.getCooldown() > 0 || skill.isActivated())
						{
							player.sendMessage(ChatColor.RED + "Veuillez attendre " + skill.getFormattedTime(skill.getCooldown()) + " avant de pouvoir faire ceci.");
							return;
						}
						
						ItemStack stack = player.getItemInHand().clone();
						stack.setAmount(1);
						ItemStack result = stack.clone();
						stack.setAmount(12);
						stack.setType(Material.WOOD);
						player.getInventory().remove(stack);
						player.getInventory().addItem(result);
					}
				}
				
				boolean ok = false;
				switch (player.getItemInHand().getType())
				{
					case WOOD_AXE:
					case STONE_AXE:
					case IRON_AXE:
					case GOLD_AXE:
					case DIAMOND_AXE:
						ok = true;
						break;
					default:
						ok = false;
						break;
				}
				
				if (ok)
				{
					if (tree.getActiveSkills(Job.CARPENTER).size() >= 2)
					{
						final Skill skill = tree.getActiveSkills(Job.CARPENTER).get(1);
						if (skill.getCooldown() > 0 || skill.isActivated())
						{
							player.sendMessage(ChatColor.RED + "Veuillez attendre " + skill.getFormattedTime(skill.getCooldown()) + " avant de pouvoir faire ceci.");
							return;
						}
						
						skill.activate();
						skill.execute(player);
						player.sendMessage(ChatColor.GOLD + "Comp\u00e9tence 2 activ\u00e9 !");
						new BukkitRunnable()
						{
							@Override
							public void run()
							{
								skill.desactivate();
							}
						}.runTaskLater(plugin, 12000);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		CompetenceTree tree = CompetenceTree.get(event.getPlayer());
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof PlayerInteractEntitySkill)
				((PlayerInteractEntitySkill) skill).onPlayerInteractEntity(event);
		}
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof PlayerInteractEntitySkill)
				((PlayerInteractEntitySkill) skill).onPlayerInteractEntity(event);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if (!plugin.hasJob(event.getPlayer()))
			plugin.setJobs(event.getPlayer(), new Job[] {Job.CHOMEUR, Job.CHOMEUR});
		CompetenceTree tree = CompetenceTree.get(event.getPlayer());
		CraftPlayer player = (CraftPlayer) event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		for (PhysicalSkill skill : tree.getPhysical().getSkills())
		{
			if (skill.isActive() && skill.isActivated())
				skill.execute(player);
			else
				skill.reset(player);
		}
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		for (Job  job : Job.values())
		{
			for (Skill skill : job.getAvailableActiveSkills())
			{
				skill.reset(player);
			}
			for (Skill skill : job.getAvailablePassiveSkills())
			{
				skill.reset(player);
			}
		}
		
		for (Skill skill : tree.getAllPassiveSkills())
		{
			skill.execute(player);
		}
		
		if (jobs.contains(Job.BLACKSMITH))
		{
			for (Skill skill : tree.getActiveSkills(Job.BLACKSMITH))
			{
				skill.execute(player);
			}
		}
		
		if (jobs.contains(Job.MASON))
		{
			for (Skill skill : tree.getActiveSkills(Job.MASON))
			{
				skill.execute(player);
			}
		}
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event)
	{
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			onPlayerJoin(new PlayerJoinEvent(player, ""));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if (event.isCancelled())
			return;
		
		event.setCancelled(true);
		Job[] jobs = plugin.getJobs(event.getPlayer());
		String header = ChatColor.BOLD + "<" + jobs[0].getPrefix() + ChatColor.RESET + ChatColor.BOLD + jobs[1].getPrefix() + ChatColor.RESET + ChatColor.BOLD + event.getPlayer().getDisplayName() + ChatColor.BOLD + ">";
		String msg = "[" + ChatColor.GRAY + event.getPlayer().getName().charAt(0) + ChatColor.RESET + "] " + event.getMessage();
		
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
		msg = msg.replaceAll("%µ£¨%%µ£¨M.;", "&");
		
		Bukkit.getServer().getConsoleSender().sendMessage(header);
		Bukkit.getServer().getConsoleSender().sendMessage(msg);
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getLocation().distance(event.getPlayer().getLocation()) > 35.0D)
				continue;
			
			if (lastSpeaker == null || !event.getPlayer().getName().equals(lastSpeaker))
			{				
				player.sendMessage(header);
			}
			player.sendMessage(msg);
		}
		lastSpeaker = event.getPlayer().getName();
	}
	
	@EventHandler
	public void onSignEdit(SignChangeEvent event)
	{
		for (int i = 0; i < 4; ++i)
		{
			String msg = event.getLine(i).replaceAll("&&", "%µ£¨%%µ£¨M.;");
			for (int j = 0; j <= 9; ++j)
				msg = msg.replaceAll("&" + j, ChatColor.getByChar(String.valueOf(j).charAt(0)).toString());
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
			msg = msg.replaceAll("%µ£¨%%µ£¨M.;", "&");
			event.setLine(i, msg);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		List<Result> results = Lists.newArrayList();
		for (Zone zone : plugin.getZones())
		{
			Result result = zone.onEntityDamage(event);
			if (result != null)
				results.add(result);
		}
		if (!results.isEmpty())
		{
			if (!results.contains(Result.ALLOW))
			{
				event.setCancelled(true);
				return;
			}
		}
		
		if (!(event.getEntity() instanceof Player))
			return;
		
		CraftPlayer player = (CraftPlayer) event.getEntity();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		CompetenceTree tree = CompetenceTree.get(player);
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof EntityDamageSkill)
				((EntityDamageSkill) skill).onEntityDamage(event);
		}
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof EntityDamageSkill)
				((EntityDamageSkill) skill).onEntityDamage(event);
		}
		for (PhysicalSkill skill : tree.getPhysical().getSkills())
		{
			if (skill.getListenerClassName().equalsIgnoreCase(EntityDamageSkill.class.getName()))
				skill.onEntityDamage(event);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player))
			return;
		
		CraftPlayer player = (CraftPlayer) event.getDamager();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		CompetenceTree tree = CompetenceTree.get(player);
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof MobDamageSkill)
				((MobDamageSkill) skill).onMobDamage(event);
		}
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof MobDamageSkill)
				((MobDamageSkill) skill).onMobDamage(event);
		}
		for (PhysicalSkill skill : tree.getPhysical().getSkills())
		{
			if (skill.getListenerClassName().equalsIgnoreCase(MobDamageSkill.class.getName()))
				skill.onMobDamage(event);
		}
		
		if (event.getEntity() instanceof LivingEntity)
		{
			LivingEntity e = (LivingEntity) event.getEntity();
			if (!(e instanceof Player) && e.getHealth() - event.getDamage() <= 0.0D)
			{
				if (jobs.contains(Job.HUNTER))
					tree.addCompetencePoint(Job.HUNTER, 1);
				else if (!(e instanceof Monster))
					tree.addIllegalityPoint();
			}
		}
		
		if (event.getEntity() instanceof Player)
			tree.addIllegalityPoint();
	}
	
	@EventHandler
	public void onCreatureSpawned(CreatureSpawnEvent event)
	{
		if (event.getEntityType() == EntityType.PLAYER)
			return;
		
		if (DedicatedServer.getServer().getPlayerList().getPlayerCount() > 0
						&& event.getSpawnReason() == SpawnReason.BREEDING)
		{
			List<Entity> entities = new ArrayList<Entity>();
			Player nearest = null;
			double r = 0.0D;
			while (nearest == null && r < 50.0D)
			{
				entities = event.getEntity().getNearbyEntities(r, r, r);
				int i = 0;
				Entity e = null;
				while (i < entities.size() && nearest == null)
				{
					e = entities.get(i);
					if (e instanceof Player)
						nearest = (Player) e;
					++i;
				}
				++r;
			}
			
			CompetenceTree tree = CompetenceTree.get(nearest);
			for (Skill skill : tree.getAllPassiveSkills())
			{
				if (skill instanceof MobSpawnSkill)
					((MobSpawnSkill) skill).onMobSpawned(event, nearest);
			}
			for (Skill skill : tree.getAllActiveSkills())
			{
				if (skill instanceof MobSpawnSkill)
					((MobSpawnSkill) skill).onMobSpawned(event, nearest);
			}
		}
		
		if (event.getEntityType() == EntityType.SPIDER)
		{
			if (event.getSpawnReason() == SpawnReason.NATURAL)
			{
				CraftSpider spider = (CraftSpider) event.getEntity();
				EntitySpider es = spider.getHandle();
				es.getAttributeInstance(GenericAttributes.maxHealth).setValue(60.0D);
				es.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
				es.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
				.setValue(es.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() + 0.21D);
				es.setHealth(60.0F);
				
			}
		}
		else if (event.getEntity() instanceof Monster)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event)
	{
		CraftPlayer player = (CraftPlayer) event.getViewers().get(0);
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		CompetenceTree tree = CompetenceTree.get(player);
		for (Skill skill : tree.getAllPassiveSkills())
		{
			if (skill instanceof PrepareCraftSkill)
				((PrepareCraftSkill) skill).onPrepareCraft(event);
		}
		for (Skill skill : tree.getAllActiveSkills())
		{
			if (skill instanceof PrepareCraftSkill)
				((PrepareCraftSkill) skill).onPrepareCraft(event);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event)
	{		
		CraftPlayer player = (CraftPlayer) event.getViewers().get(0);
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		CompetenceTree tree = CompetenceTree.get(player);
		
		if (event.getCurrentItem().getType() == WOOD)
		{
			if (jobs.contains(Job.CARPENTER))
				tree.addCompetencePoint(Job.CARPENTER, 1);
			else
				tree.addIllegalityPoint();
		}
		
		switch (event.getCurrentItem().getType())
		{
		case STONE_SWORD:
		case STONE_SPADE:
		case STONE_AXE:
		case STONE_PICKAXE:
		case STONE_HOE:
		case IRON_SWORD:
		case IRON_SPADE:
		case IRON_AXE:
		case IRON_PICKAXE:
		case IRON_HOE:
		case GOLD_SWORD:
		case GOLD_SPADE:
		case GOLD_AXE:
		case GOLD_PICKAXE:
		case GOLD_HOE:
		case DIAMOND_SWORD:
		case DIAMOND_SPADE:
		case DIAMOND_AXE:
		case DIAMOND_PICKAXE:
		case DIAMOND_HOE:
			break;
		default:
			return;
		}
		
		if (jobs.contains(Job.BLACKSMITH))
			tree.addCompetencePoint(Job.BLACKSMITH, 1);
		else
			tree.addIllegalityPoint();
	}
	
	@EventHandler
	public void onSmelt(FurnaceSmeltEvent event)
	{
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if (player.getGameMode() != GameMode.SURVIVAL)
				continue;
			
			if (player.getLocation().distance(event.getBlock().getLocation()) <= 5.0D)
			{
				if (Arrays.asList(plugin.getJobs(player)).contains(Job.STONECUTTER))
				{
					CompetenceTree tree = CompetenceTree.get(player);
					for (Skill skill : tree.getAllPassiveSkills())
					{
						if (skill instanceof FurnaceSmeltSkill)
							((FurnaceSmeltSkill) skill).onSmelt(event, player);
					}
					for (Skill skill : tree.getAllActiveSkills())
					{
						if (skill instanceof FurnaceSmeltSkill)
							((FurnaceSmeltSkill) skill).onSmelt(event, player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onFurnaceExtract(FurnaceExtractEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		List<Job> jobs = Arrays.asList(plugin.getJobs(event.getPlayer()));
		CompetenceTree tree = CompetenceTree.get(event.getPlayer());
		
		if (jobs.contains(Job.STONECUTTER) && event.getItemType() == STONE)
		{
			tree.addCompetencePoint(Job.STONECUTTER, event.getItemAmount());
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (event.isCancelled() || !(event.getWhoClicked() instanceof CraftPlayer))
			return;
		
		final CraftPlayer player = (CraftPlayer) event.getWhoClicked();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;
		List<Job> jobs = Arrays.asList(plugin.getJobs(player));
		
		if (event.getInventory() instanceof AnvilInventory)
		{
			CraftInventoryAnvil inv = (CraftInventoryAnvil) event.getInventory();
			
			if (inv.getItem(2) != null && event.getSlot() == 2)
			{
				event.setCancelled(true);
				if (jobs.contains(Job.BLACKSMITH))
				{
					ItemStack stack = inv.getItem(2).clone();
					Repairable r = (Repairable) stack.getItemMeta();
					int nb = r.getRepairCost();
					if (stack.getItemMeta().hasDisplayName() && !inv.getItem(0).getItemMeta().hasDisplayName())
						nb--;
					r.setRepairCost(0);
					stack.setItemMeta((ItemMeta) r);
					
					if (player.getInventory().firstEmpty() >= 0)
						player.getInventory().addItem(stack);
					else
						player.getWorld().dropItem(player.getLocation(), stack);
					ItemStack stack0 = inv.getItem(0);
					ItemStack stack1 = inv.getItem(1);
					if (stack1 != null)
						stack1 = stack1.clone();
					if (stack0 != null && stack1 != null)
					{
						if (stack0.getType() == stack1.getType())
							nb = 1;
					}
					inv.setContents(new ItemStack[2]);
					inv.getResultInventory().setItem(0, null);
					if (stack1 != null)
					{
						stack1.setAmount(stack1.getAmount() - nb);
						if (stack1.getAmount() <= 0)
							stack1 = null;
						inv.setItem(1, stack1);
					}
					player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
				}
				player.getHandle().playerConnection.a().a(new PacketPlayOutExperience(player.getExp(), player.getExpToLevel(), player.getLevel()), null);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event)
	{
		Material from = event.getBlock().getType();
		int id = from.getId();
		if (id >= 8 && id <= 11)
		{
			Block to = event.getToBlock();
			if (to.getType() == AIR)
			{
				int mirrorID1 = id == 8 || id == 9 ? 10 : 8;
				int mirrorID2 = id == 8 || id == 9 ? 10 : 9;
				BlockFace[] faces = {NORTH, EAST, SOUTH, WEST,
								UP, DOWN};
				for (BlockFace face : faces)
				{
					Block relative = to.getRelative(face);
					int relativeId = relative.getType().getId();
					if (relativeId == mirrorID1 || relativeId == mirrorID2)
					{
						event.setCancelled(true);
						return;
					}
				}
			}
		}

		Material to = event.getToBlock().getType();
		if (event.getFace() == DOWN)
		{
			if ((from == LAVA || from == STATIONARY_LAVA) && to == STATIONARY_WATER)
			{
				event.setCancelled(true);
				return;
			}
		}
		
		if ((from == WATER || from == STATIONARY_WATER) && (to == STATIONARY_LAVA))
		{
			event.setCancelled(true);
			return;
		}
		
		if (from == to)
			event.setCancelled(true);
		
		if (to != AIR && to != WATER && to != STATIONARY_WATER && to != LAVA)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockApplyPhysics(BlockPhysicsEvent event)
	{
		if (event.getChangedType().ordinal() > 12)
			return;
		if (event.getBlock().getType() == STATIONARY_LAVA && event.getChangedType() != STATIONARY_LAVA
						&& event.getChangedType() != LAVA)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;
		
		if (event.getBucket() == Material.LAVA_BUCKET)
		{
			Block future = event.getBlockClicked().getRelative(event.getBlockFace());
			BlockFace[] faces = {SELF, DOWN, NORTH, SOUTH, WEST, EAST};
			for (BlockFace face : faces)
			{
				Block relative = future.getRelative(face);
				if (relative.getType() == STATIONARY_WATER || relative.getType() == WATER)
				{
					event.setCancelled(true);
					return;
				}
			}
		}
		else if (event.getBucket() == Material.WATER_BUCKET)
		{
			Block future = event.getBlockClicked().getRelative(event.getBlockFace());
			BlockFace[] faces = {SELF, DOWN, NORTH, SOUTH, WEST, EAST};
			for (BlockFace face : faces)
			{
				Block relative = future.getRelative(face);
				if (relative.getType() == STATIONARY_LAVA || relative.getType() == LAVA)
				{
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void breakLog(Block block, Player player)
	{
		BlockFace[] faces = {UP, NORTH, SOUTH, EAST, WEST};
		for (BlockFace face : faces)
		{
			Block b = block.getRelative(face);
			if (b != null && (b.getType() == LOG || b.getType() == LOG_2))
			{
				ItemStack stack = player.getItemInHand();
				if (stack != null)
				{
					net.minecraft.server.v1_8_R3.Item item = net.minecraft.server.v1_8_R3.Item.getById(stack.getTypeId());
					if (item != null && item.usesDurability())
					{
						int i = 1;
						if (stack.containsEnchantment(Enchantment.DURABILITY))
						{
							i = new Random().nextInt(stack.getEnchantmentLevel(Enchantment.DURABILITY)) == 0 ? 0 : 1;
						}
						
						stack.setDurability((short) (stack.getDurability() + i));
						if (stack.getDurability() > item.getMaxDurability())
						{
							player.setItemInHand(null);
							player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
							return;
						}
					}
				}
				b.breakNaturally(player.getItemInHand());
				breakLog(b, player);
			}
		}
	}
}
