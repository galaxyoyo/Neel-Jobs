package galaxyoyo.minecraft.bukkit.jobs;

import galaxyoyo.minecraft.bukkit.jobs.skills.BabySpawnTwiceSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.BasicSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.BlockBreakingEffectSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.BurnTwoIngotsSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.BurnTwoSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.ChangeItemByClickSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.ChangeLogRecipeSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.ChangeSticksRecipeSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.CompassMobSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.CreatePatternSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.CropsDropSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.EffectSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.ForgeSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.FortuneEnchantmentSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.GrowUpAnimalsSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.HealthSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.LootEnchantmentSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.MineInstantlySkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.NewSpawnSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.PermissionSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.PlacePatternSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.PlantsDropSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;
import galaxyoyo.minecraft.bukkit.jobs.skills.SmeltFasterSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.ZonedHarvestSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.ZonedMiningSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Job
{
	CHOMEUR("Chômeur", ChatColor.WHITE, "", Material.AIR, 0, ""),
	WOODCUTTER("Bûcheron", ChatColor.GREEN, "[Bûcheron]", Material.LOG, 300, "Miner une bûche = 1 point"),
	MINER("Mineur", ChatColor.BLUE, "[Mineur]", Material.COBBLESTONE, 2000, "Miner roche, pierre ou minerai = 1 point"),
	BLACKSMITH("Forgeron", ChatColor.GRAY, "[Forgeron]", Material.IRON_INGOT, 50, "Un outil non en bois = 1 point"),
	CARPENTER("Menuisier", ChatColor.RED, "[Menuisier]", Material.WOOD, 1500, "Planches craft\u00e9es = 1 point"),
	MASON("Maçon", ChatColor.BLACK, "[Maçon]", Material.BRICK, 1, "Une maison cr\u00e9\u00e9e = 1 point (validation par admin)"),
	STONECUTTER("Tailleur de pierre", ChatColor.DARK_GRAY, "[Tailleur]", Material.STONE, 1000, "1 cobble fondue = 1 point"),
	FARMER("Agriculteur / Éleveur", ChatColor.YELLOW, "[Agriculteur]", Material.BREAD, 2000, "1 plant de bl\u00e9 / pomme de terre / carotte mature r\u00e9cup\u00e9r\u00e9 = 1 point"),
	HUNTER("Chasseur", ChatColor.DARK_GREEN, "[Chasseur]", Material.COOKED_BEEF, 500, "1 mob tu\u00e9 = 1 point");
	
	private final String display;
	private final ChatColor color;
	private final String prefix;
	private final Material symbol;
	private final int maxPtsLvl1;
	private final String howEarnPoints;
	private final List<Skill> availableActiveSkillsList = new ArrayList<Skill>();
	private final Map<String, Skill> availableActiveSkillsMap = new HashMap<String, Skill>();
	private final List<Skill> availablePassiveSkillsList = new ArrayList<Skill>();
	private final Map<String, Skill> availablePassiveSkillsMap = new HashMap<String, Skill>();
	
	Job(String display, ChatColor color, String prefix, Material symbol, int maxPtsLvl1, String howEarnPoints)
	{
		this.display = display;
		this.color = color;
		if (!prefix.endsWith(" ") && !prefix.isEmpty())
			prefix += " ";
		prefix = color + prefix;
		this.prefix = prefix;
		this.symbol = symbol;
		this.maxPtsLvl1 = maxPtsLvl1;
		this.howEarnPoints = howEarnPoints;
	}
	
	public String getDisplay()
	{
		return display;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public String getRawPrefix()
	{
		String str = prefix.replaceAll("\\[|\\]", "");
		str = str.substring(0, str.length() - 1);
		return str;
	}
	
	public Material getSymbol()
	{
		return symbol;
	}
	
	public int getMaxPtsLvl1()
	{
		return maxPtsLvl1;
	}
	
	public String getHowEarnPoints()
	{
		return howEarnPoints;
	}
	
	public Skill getActiveSkill(String id)
	{
		return availableActiveSkillsMap.get(id);
	}
	
	public List<Skill> getAvailableActiveSkills()
	{
		return availableActiveSkillsList;
	}
	
	public Skill getPassiveSkill(String id)
	{
		return availablePassiveSkillsMap.get(id);
	}
	
	public List<Skill> getAvailablePassiveSkills()
	{
		return availablePassiveSkillsList;
	}
	
	public void addActiveSkill(Skill skill)
	{
		skill.setActive(true);
		availableActiveSkillsList.add(skill);
		availableActiveSkillsMap.put(skill.getId(), skill);
	}
	
	public void addPassiveSkill(Skill skill)
	{
		skill.setActive(false);
		availablePassiveSkillsList.add(skill);
		availablePassiveSkillsMap.put(skill.getId(), skill);
	}
	
	static
	{
		WOODCUTTER.addPassiveSkill(new BlockBreakingEffectSkill("woodcutter.passive.haste1", new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0), Material.LOG, Material.LOG_2));
		WOODCUTTER.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0), "woodcutter.passive.strength"));
		WOODCUTTER.addPassiveSkill(new HealthSkill(6.0D, "woodcutter.passive.health.6"));
		WOODCUTTER.addPassiveSkill(new BlockBreakingEffectSkill("woodcutter.passive.haste2", new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), Material.LOG, Material.LOG_2));
		
		WOODCUTTER.addActiveSkill(new MineInstantlySkill(6000, "woodcutter.active.mineLogInstant1", 1, Material.LOG, Material.LOG_2));
		WOODCUTTER.addActiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, 400, 2), 2400, true, "woodcutter.active.haste1"));
		WOODCUTTER.addActiveSkill(new MineInstantlySkill(6000, "woodcutter.active.mineLogInstant2", 2, Material.LOG, Material.LOG_2));
		WOODCUTTER.addActiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 2), 2400, true, "woodcutter.active.haste1+"));
		
		MINER.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0), "miner.passive.haste1"));
		MINER.addPassiveSkill(new FortuneEnchantmentSkill(1, "miner.passive.fortune1"));
		MINER.addPassiveSkill(new HealthSkill(4.0D, "miner.passive.health.4"));
		MINER.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), "miner.passive.haste2"));
		
		MINER.addActiveSkill(new ZonedMiningSkill(1200, "miner.active.zonedMining"));
		MINER.addActiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, 300, 2), 6000, "miner.active.haste3"));
		MINER.addActiveSkill(new ZonedMiningSkill(900, "miner.active.zonedMining+"));
		MINER.addActiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, 400, 2), 4800, "miner.active.haste3+"));
		
		BLACKSMITH.addPassiveSkill(new HealthSkill(6.0D, "blacksmith.passive.health.6"));
		BLACKSMITH.addPassiveSkill(new BurnTwoIngotsSkill(15, Material.IRON_INGOT, "blacksmith.passive.15%2IronIngots"));
		BLACKSMITH.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0), "blacksmith.passive.strength"));
		BLACKSMITH.addPassiveSkill(new BurnTwoIngotsSkill(25, Material.IRON_INGOT,"blacksmith.passive.25%2IronIngots"));
		
		BLACKSMITH.addActiveSkill(new ForgeSkill(new ItemStack(Material.IRON_INGOT, 5), Enchantment.DIG_SPEED, 1, "blacksmoth.passive.5Iron=EfficiencyI"));
		BLACKSMITH.addActiveSkill(new ForgeSkill(new ItemStack(Material.IRON_INGOT, 8), Enchantment.DURABILITY, 1, "blacksmoth.passive.5Iron=UnbreakingI"));
		BLACKSMITH.addActiveSkill(new ForgeSkill(new ItemStack(Material.IRON_INGOT, 15), Enchantment.DIG_SPEED, 2, "blacksmoth.passive.5Iron=EfficiencyII"));
		BLACKSMITH.addActiveSkill(new ForgeSkill(new ItemStack(Material.IRON_INGOT, 32), Enchantment.SILK_TOUCH, 1, "blacksmoth.passive.5Iron=SilkTouchI"));
		
		CARPENTER.addPassiveSkill(new ChangeSticksRecipeSkill("carpenter.passive.2Wood=6Sticks"));
		CARPENTER.addPassiveSkill(new HealthSkill(4.0D, "carpenter.passive.health.4"));
		CARPENTER.addPassiveSkill(new ChangeLogRecipeSkill("carpenter.passive.1Log=6Woods"));
		CARPENTER.addPassiveSkill(new PermissionSkill("jobsrpg.command.workbench", "carpenter.passive.portableWorkbench"));
		
		CARPENTER.addActiveSkill(new BasicSkill(6000, "Craft 1 bûche en 12 planches\n\rActivation : clic droit avec une bûche", "carpenter.active.1Log=12Woods"));
		CARPENTER.addActiveSkill(new BasicSkill(108000, "Autorise \u00e0 miner pendant 10 minutes du bois", "carpenter.active.woodCuttingPermission"));
		CARPENTER.addActiveSkill(new BasicSkill(3600, "Craft 1 bûche en 18 planches\n\rActivation : clic droit avec une bûche", "carpenter.active.1Log=18Woods"));
		
		MASON.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0), "mason.passive.haste1"));
		MASON.addPassiveSkill(new HealthSkill(4.0D, "mason.passive.health.4"));

		MASON.addActiveSkill(new PlacePatternSkill(6000, "mason.active.placePatternPermission"));
		MASON.addActiveSkill(new CreatePatternSkill(192000, "mason.active.createPatternPermission"));

		STONECUTTER.addPassiveSkill(new BurnTwoSkill(Material.COBBLESTONE, 25, "stonecutter.passive.25%2Cobble="));
		STONECUTTER.addPassiveSkill(new HealthSkill(4.0D, "stonecutter.passive.health.4"));
		STONECUTTER.addPassiveSkill(new BurnTwoSkill(Material.COBBLESTONE, 50, "stonecutter.passive.50%2Cobble="));
		STONECUTTER.addPassiveSkill(new PermissionSkill("jobsrpg.command.workbench", "stonecutter.passive.portableWorkbench"));
		
		STONECUTTER.addActiveSkill(new ChangeItemByClickSkill(Material.COBBLESTONE, Material.STONE, 16, 3600, "stonecutter.active.16Cobble=16Stone"));
		STONECUTTER.addActiveSkill(new BasicSkill(108000, "Autorise \u00e0 miner pendant 10 minutes de la roche", "stonecutter.active.stoneCuttingPermission"));
		STONECUTTER.addActiveSkill(new ChangeItemByClickSkill(Material.COBBLESTONE, Material.STONE, 32, 3600, "stonecutter.active.32Cobble=32Stone"));
		STONECUTTER.addActiveSkill(new SmeltFasterSkill(12000, "stonecutter.active.smelt2xFaster"));
		
		FARMER.addPassiveSkill(new HealthSkill(4.0D, "farmer.passive.health.4"));
		FARMER.addPassiveSkill(new CropsDropSkill("farmer.passive.cropsDropMore"));
		FARMER.addPassiveSkill(new BabySpawnTwiceSkill("farmer.passive.moreBabies"));
		FARMER.addPassiveSkill(new PlantsDropSkill("farmer.passive.morePlants"));
		
		FARMER.addActiveSkill(new ZonedHarvestSkill(false, 1200, "farmer.active.biggerZone"));
		FARMER.addActiveSkill(new NewSpawnSkill(2, 12000, "farmer.active.moreSpawn"));
		FARMER.addActiveSkill(new ZonedHarvestSkill(true, 2400, "farmer.active.biggerZone++"));
		FARMER.addActiveSkill(new GrowUpAnimalsSkill(10.0D, 10.0D, 3, 24000, "farmer.active.babies=adults"));
		
		HUNTER.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), "hunter.passive.speed"));
		HUNTER.addPassiveSkill(new LootEnchantmentSkill(1, "hunter.passive.looting"));
		HUNTER.addPassiveSkill(new HealthSkill(4.0D, "hunter.passive.health.4"));
		HUNTER.addPassiveSkill(new EffectSkill(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1), "hunter.passive.strength2"));
		
		HUNTER.addActiveSkill(new LootEnchantmentSkill(2, 6000, "hunter.active.looting++"));
		HUNTER.addActiveSkill(new CompassMobSkill(6000, "hunter.active.mobFinding"));
		HUNTER.addActiveSkill(new LootEnchantmentSkill(2, 3600, "hunter.active.looting++-"));
		HUNTER.addActiveSkill(new ChangeItemByClickSkill(Material.STRING, Material.LEASH, 1, 6000, "hunter.active.1String=1Leash"));
	}
}
