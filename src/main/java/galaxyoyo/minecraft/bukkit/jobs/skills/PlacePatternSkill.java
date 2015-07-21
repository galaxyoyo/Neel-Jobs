package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerInteractSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.PlayerMoveSkill;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlacePatternSkill extends Skill implements PlayerMoveSkill, PlayerInteractSkill
{
	public PlacePatternSkill(int cooldown, String id)
	{
		super (cooldown, id);
	}
	
	protected PlacePatternSkill()
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
		return "Permet de placer un pattern";
	}
	
	@Override
	public Skill clone()
	{
		return new PlacePatternSkill(getBaseCooldown(), getId());
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown();
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getMaterial() == Material.PAPER)
		{
			ItemStack is = CraftItemStack.asNMSCopy(event.getItem());
			if (!is.getTag().hasKey("zone"))
				return;
			NBTTagCompound tag = is.getTag().getCompound("zone");
			Zone zone = new Zone(tag);
			zone.place(event.getPlayer().getWorld(), (CraftPlayer) event.getPlayer(), false);
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (event.getPlayer().getItemInHand().getType() == Material.PAPER)
		{
			ItemStack is = CraftItemStack.asNMSCopy(event.getPlayer().getItemInHand());
			if (!is.getTag().hasKey("zone"))
				return;
			NBTTagCompound tag = is.getTag().getCompound("zone");
			Zone zone = new Zone(tag);
			zone.delete(event.getPlayer().getWorld(), event.getFrom());
			zone.place(event.getPlayer().getWorld(), (CraftPlayer) event.getPlayer(), true);
		}
	}
}
