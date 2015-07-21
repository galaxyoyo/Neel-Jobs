package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.CompetenceTree;
import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.JobsPlugin;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractSkill;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChangeItemByClickSkill extends Skill implements PlayerInteractSkill
{
	private Material from;
	private Material to;
	private int cb;
	
	public ChangeItemByClickSkill(Material from, Material to, int cb, String id)
	{
		this (from, to, cb, 0, id);
	}
	
	public ChangeItemByClickSkill(Material from, Material to, int cb, int cooldown, String id)
	{
		super (cooldown, id);
		this.from = from;
		this.to = to;
		this.cb = cb;
	}

	@Override
	public void execute(Player player)
	{
	}

	@Override
	public void reset(Player player)
	{
	}

	@Override
	public String getDescription()
	{
		return "Change en un clic droit " + cb + " " + from + " en " + to;
	}

	@Override
	public Skill clone()
	{
		Skill skill = new ChangeItemByClickSkill(from, to, cb, getBaseCooldown(), getId());
		skill.setActive(isActive());
		return skill;
	}

	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown()
				&& from == ((ChangeItemByClickSkill) skill).from
				&& cb == ((ChangeItemByClickSkill) skill).cb
				&& to == ((ChangeItemByClickSkill) skill).to;
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		
		if (event.getItem() == null || event.getItem().getType() != from)
			return;
		
		Player player = event.getPlayer();
		CompetenceTree tree = CompetenceTree.get(player);
		List<Job> jobs = Arrays.asList(((JobsPlugin) Bukkit.getServer().getPluginManager()
						.getPlugin("Jobs-RPG")).getJobs(player));
		
		if (jobs.contains(Job.STONECUTTER)
				&& tree.getActiveSkills(Job.STONECUTTER).size() >= 3
				&& tree.getActiveSkills(Job.STONECUTTER).get(2) != this)
			return;
		
		if (getCooldown() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Veuillez attendre "
					+ getFormattedTime(getCooldown()) + " avant de faire ceci.");
			return;
		}
		
		if (event.getPlayer().getInventory().contains(new ItemStack(from, cb)))
		{
			event.getPlayer().getInventory().remove(new ItemStack(from, cb));
			event.getPlayer().getInventory().addItem(new ItemStack(to, cb));
			
			startCooldown();
		}
	}
}
