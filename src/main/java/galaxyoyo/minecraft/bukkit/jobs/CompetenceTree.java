package galaxyoyo.minecraft.bukkit.jobs;

import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

public class CompetenceTree
{
	private static final Map<String, CompetenceTree> trees = new HashMap<String, CompetenceTree>();
	
	private String player;
	private PhysicalCompetences physical;
	private final Map<Job, List<Skill>> activeSkills = new HashMap<Job, List<Skill>>();
	private final Map<Job, List<Skill>> passiveSkills = new HashMap<Job, List<Skill>>();
	private final Map<Job, Integer> competencesPts = new HashMap<Job, Integer>();
	private final Map<Job, Integer> competencesPtsMax = new HashMap<Job, Integer>();
	private final Map<Job, Integer> levels = new HashMap<Job, Integer>();
	private final Map<Job, Integer> skillsToDeliver = new HashMap<Job, Integer>();
	private int illegalityPts = 0;
	
	private static transient final Type treesMapType = new TypeToken<HashMap<String, CompetenceTree>>() {}.getType();
	
	public CompetenceTree(String uuid)
	{
		this.player = uuid;
	}
	
	public static CompetenceTree get(Player player)
	{
		if (JobsPlugin.isCrackedVersion(player))
		{
			CompetenceTree tree;
			if ((tree = trees.remove(player.getUniqueId().toString())) != null)
				return trees.put(player.getName(), tree);
			
			if (!trees.containsKey(player.getName()))
				return trees.put(player.getName(), new CompetenceTree(player.getName()));
			return trees.get(player.getName());
		}
		
		if (trees.containsKey(player.getName()))
		{
			CompetenceTree tree = trees.remove(player.getName());
			tree.player = player.getUniqueId().toString();
			trees.put(player.getUniqueId().toString(), tree);
		}
		
		if (!trees.containsKey(player.getUniqueId().toString()))
			return trees.put(player.getUniqueId().toString(), new CompetenceTree(player.getUniqueId().toString()));
		return trees.get(player.getUniqueId().toString());
	}
	
	public static CompetenceTree get(OfflinePlayer player)
	{
		if (JobsPlugin.isCrackedVersion(player))
		{
			CompetenceTree tree;
			if ((tree = trees.remove(player.getUniqueId().toString())) != null)
				return trees.put(player.getName(), tree);
			
			if (!trees.containsKey(player.getName()))
				return trees.put(player.getName(), new CompetenceTree(player.getName()));
			return trees.get(player.getName());
		}
		
		if (trees.containsKey(player.getName()))
		{
			CompetenceTree tree = trees.remove(player.getName());
			tree.player = player.getUniqueId().toString();
			trees.put(player.getUniqueId().toString(), tree);
		}
		
		if (!trees.containsKey(player.getUniqueId().toString()))
			return trees.put(player.getUniqueId().toString(), new CompetenceTree(player.getUniqueId().toString()));
		return trees.get(player.getUniqueId().toString());
	}
	
	public static boolean contains(Player player)
	{
		Validate.notNull(player);
		if (JobsPlugin.isCrackedVersion(player))
			return trees.containsKey(player.getName());
		else
			return trees.containsKey(player.getUniqueId().toString());
	}
	
	@SuppressWarnings("deprecation")
	public static boolean contains(String playerName)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		if (player == null)
			return false;
		if (JobsPlugin.isCrackedVersion(player))
			return trees.containsKey(playerName);
		else
			return trees.containsKey(player.getUniqueId().toString());
	}
	
	public List<Skill> getActiveSkills(Job job)
	{
		if (!activeSkills.containsKey(job))
			activeSkills.put(job, new ArrayList<Skill>());
		return activeSkills.get(job);
	}
	
	public String getPlayerName()
	{
		return player;
	}
	
	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer()
	{
		try
		{
			UUID uuid = UUID.fromString(player);
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if (JobsPlugin.isCrackedVersion(player))
				this.player = player.getName();
			return player;
		}
		catch (Throwable t)
		{
			return Bukkit.getOfflinePlayer(player);
		}
	}
	
	public Player getPlayer()
	{
		Validate.notNull(getOfflinePlayer());
		Validate.notNull(getOfflinePlayer().getPlayer());
		return getOfflinePlayer().getPlayer();
	}
	
	public PhysicalCompetences getPhysical()
	{
		if (physical == null)
			physical = new PhysicalCompetences(this);
		if (physical.getCompetenceTree() == null)
			physical.setCompetenceTree(this);
		return physical;
	}
	
	public List<Skill> getAllActiveSkills()
	{
		List<Skill> skills = new ArrayList<Skill>();
		for (Entry<Job, List<Skill>> entry : activeSkills.entrySet())
			skills.addAll(entry.getValue());
		return skills;
	}
	
	public List<Skill> getPassiveSkills(Job job)
	{
		if (!passiveSkills.containsKey(job))
			passiveSkills.put(job, new ArrayList<Skill>());
		return passiveSkills.get(job);
	}
	
	public List<Skill> getAllPassiveSkills()
	{
		List<Skill> skills = new ArrayList<Skill>();
		for (Entry<Job, List<Skill>> entry : passiveSkills.entrySet())
			skills.addAll(entry.getValue());
		return skills;
	}
	
	@Deprecated
	public boolean hasActive(Job job, Class<? extends Skill> skillClass)
	{
		for (Skill skill : getActiveSkills(job))
		{
			if (skill.getClass() == skillClass)
				return true;
		}
		return false;
	}
	
	@Deprecated
	public boolean hasPassive(Job job, Class<? extends Skill> skillClass)
	{
		for (Skill skill : getPassiveSkills(job))
		{
			if (skill.getClass() == skillClass)
				return true;
		}
		return false;
	}
	
	public boolean hasActive(Job job, Skill skill)
	{
		return getActiveCloned(job, skill) != null;
	}
	
	public boolean hasPassive(Job job, Skill skill)
	{
		return getPassiveCloned(job, skill) != null;
	}
	
	public Skill getActiveCloned(Job job, Skill skill)
	{
		for (Skill s : getActiveSkills(job))
		{
			if (skill.equals(s))
				return s;
		}
		
		return null;
	}
	
	public Skill getPassiveCloned(Job job, Skill skill)
	{
		for (Skill s : getPassiveSkills(job))
		{
			if (skill.equals(s))
				return s;
		}
		
		return null;
	}
	
	public void addActiveSkill(Job job, Skill skill)
	{
		getActiveSkills(job).add(skill.clone());
	}
	
	public void addPassiveSkill(Job job, Skill skill)
	{
		getPassiveSkills(job).add(skill.clone());
	}
	
	public int getCompetencePts(Job job)
	{
		if (!competencesPts.containsKey(job))
			this.competencesPts.put(job, 0);
		return this.competencesPts.get(job);
	}
	
	public void addCompetencePoint(Job job, int number)
	{
		this.competencesPts.put(job, getCompetencePts(job) + number);
		
		while (this.competencesPts.get(job) >= this.getCompetencePtsMax(job))
		{
			this.competencesPts.put(job, this.competencesPts.get(job) - this.competencesPtsMax.get(job));
			if (!levels.containsKey(job))
				this.levels.put(job, Integer.valueOf(1));
			this.levels.put(job, levels.get(job) + 1);
			addSkillToDeliver(job);
			this.competencesPtsMax.put(job, this.competencesPtsMax.get(job) * 130 / 100);
			if (Bukkit.getServer().getPlayer(UUID.fromString(player)) != null)
			{
				Player player = Bukkit.getServer().getPlayer(UUID.fromString(this.player));
				player.sendMessage(ChatColor.GOLD + "Vous \u00eates mont\u00e9 de niveau pour le m\u00e9tier " + job.getDisplay() + " !");
				player.sendMessage(ChatColor.GOLD + "Veuillez choisir une comp\u00e9tence dans le " + ChatColor.GREEN + "/comp");
			}
		}
	}
	
	public int getCompetencePtsMax(Job job)
	{
		if (!competencesPtsMax.containsKey(job))
			this.competencesPtsMax.put(job, Integer.valueOf(job.getMaxPtsLvl1()));
		return this.competencesPtsMax.get(job);
	}
	
	public int getLevel(Job job)
	{
		if (!levels.containsKey(job))
			this.levels.put(job, Integer.valueOf(1));
		return this.levels.get(job);
	}
	
	public int getIllegalityPoints()
	{
		return illegalityPts;
	}
	
	public void addIllegalityPoint()
	{
		addIllegalityPoint(1);
	}
	
	public void addIllegalityPoint(int number)
	{
		illegalityPts += number;
	}
	
	public void removeIllegalityPoint()
	{
		addIllegalityPoint(-1);
	}
	
	public void removeIllegalityPoint(int number)
	{
		addIllegalityPoint(-number);
	}
	
	public void setIllegalityPoint(int pts)
	{
		illegalityPts = pts;
	}
	
	public int getSkillsToDeliver(Job job)
	{
		if (!this.skillsToDeliver.containsKey(job))
			this.skillsToDeliver.put(job, Integer.valueOf(0));
		return this.skillsToDeliver.get(job);
	}
	
	public void addSkillToDeliver(Job job)
	{
		if (!this.skillsToDeliver.containsKey(job))
			this.skillsToDeliver.put(job, Integer.valueOf(0));
		this.skillsToDeliver.put(job, this.skillsToDeliver.get(job) + 1);
	}
	
	public void removeSkillToDeliver(Job job)
	{
		if (!this.skillsToDeliver.containsKey(job))
			this.skillsToDeliver.put(job, Integer.valueOf(0));
		if (this.skillsToDeliver.get(job) <= 0)
			throw new IndexOutOfBoundsException("-1");
		this.skillsToDeliver.put(job, this.skillsToDeliver.get(job) - 1);
	}
	
	public void remove(Job job)
	{
		this.activeSkills.remove(job);
		this.passiveSkills.remove(job);
		this.competencesPts.remove(job);
		this.competencesPtsMax.remove(job);
		this.levels.remove(job);
		this.skillsToDeliver.remove(job);
	}
	
	public static void read() throws IOException
	{
		File file = new File("plugins" + File.separator + "Jobs-RPG", "skill-trees.gz");
		
		if (!file.isFile())
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
			return;
		}
		
		if (file.length() == 0)
			return;
		
		InputStreamReader isr = new InputStreamReader(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))));
		trees.clear();
		Map<String, CompetenceTree> map = ((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getGson().fromJson(isr, treesMapType);
		trees.putAll(map);
		for (CompetenceTree tree : trees.values())
		{
			for (Entry<Job, List<Skill>> entry : tree.activeSkills.entrySet())
			{
				List<Skill> skills = tree.getActiveSkills(entry.getKey());
				while (skills.contains(null))
				{
					tree.addSkillToDeliver(entry.getKey());
					skills.remove(null);
				}
				
				int i = 0;
				while (i < skills.size())
				{
					Skill local = skills.get(i);
					Skill remote = entry.getKey().getAvailableActiveSkills().get(i);
					if (!local.getId().equalsIgnoreCase(remote.getId()))
					{
						for (int j = 0; j < skills.size(); ++j)
							tree.addSkillToDeliver(entry.getKey());
						skills.clear();
						break;
					}
					++i;
				}
			}
			for (Entry<Job, List<Skill>> entry : tree.passiveSkills.entrySet())
			{
				List<Skill> skills = tree.getPassiveSkills(entry.getKey());
				while (skills.contains(null))
				{
					tree.addSkillToDeliver(entry.getKey());
					skills.remove(null);
				}
				
				int i = 0;
				while (i < skills.size())
				{
					Skill local = skills.get(i);
					Skill remote = entry.getKey().getAvailablePassiveSkills().get(i);
					if (!local.getId().equalsIgnoreCase(remote.getId()))
					{
						for (int j = 0; j < skills.size(); ++j)
							tree.addSkillToDeliver(entry.getKey());
						skills.clear();
						break;
					}
					++i;
				}
			}
		}
	}
	
	public static void write() throws IOException
	{
		File file = new File("plugins" + File.separator + "Jobs-RPG", "skill-trees.gz");
		file.getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file))));
		Gson gson = ((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getGson();
		gson.toJson(trees, treesMapType, new JsonWriter(pw));
		pw.close();
	}

	public static List<Field> getFields(Class<?> clazz)
	{
		List<Field> fields = new ArrayList<Field>();
		
		for (Field f : clazz.getDeclaredFields())
		{
			f.setAccessible(true);
			fields.add(f);
		}
		
		if (clazz.getSuperclass() != null)
			fields.addAll(getFields(clazz.getSuperclass()));
		
		return fields;
	}
	
	@Override
	public String toString()
	{
		return "CompetenceTree{player=" + getOfflinePlayer().getName() + ", activeSkills=" + activeSkills + ", passiveSkills=" + passiveSkills + ", competencesPts=" + competencesPts + ", competencesPtsMax=" + competencesPtsMax + ", levels=" + levels + "}";
	}
}
