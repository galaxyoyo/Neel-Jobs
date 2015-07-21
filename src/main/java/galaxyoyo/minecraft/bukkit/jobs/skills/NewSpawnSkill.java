package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractEntitySkill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NewSpawnSkill extends Skill implements PlayerInteractEntitySkill
{
	private int count;
	
	public NewSpawnSkill(int count, int cooldown, String id)
	{
		super (cooldown, id);
		this.count = count;
		setActive(true);
	}
	
	protected NewSpawnSkill()
	{
		super ();
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
		return "Fait spawner " + count + " animaux avec un clic droit de houe";
	}
	
	@Override
	public Skill clone()
	{
		return new NewSpawnSkill(count, getBaseCooldown(), getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown()
				&& count == ((NewSpawnSkill) skill).count;
	}
	
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		if (!isActivated())
			return;
		
		if (getCooldown() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Veuillez attendre " + getFormattedTime(getCooldown()) + " avant de pouvoir faire cela \u00e0 nouveau.");
			return;
		}
		
		if (event.getPlayer().getItemInHand() == null)
			return;
		
		switch (event.getPlayer().getItemInHand().getType())
		{
		case WOOD_HOE:
		case STONE_HOE:
		case IRON_HOE:
		case GOLD_HOE:
		case DIAMOND_HOE:
			break;
		default:
			return;
		}
		
		if (event.getRightClicked() instanceof Animals)
		{
			Animals a1 = (Animals) event.getRightClicked().getWorld().spawnEntity(
					event.getRightClicked().getLocation(), event.getRightClicked().getType());
			Animals a2 = (Animals) event.getRightClicked().getWorld().spawnEntity(
					event.getRightClicked().getLocation(), event.getRightClicked().getType());
			a1.setBaby();
			a2.setBaby();
			startCooldown();
		}
		
		startCooldown();
	}
}
