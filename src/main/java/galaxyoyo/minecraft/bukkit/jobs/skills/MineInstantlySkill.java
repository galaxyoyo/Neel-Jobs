package galaxyoyo.minecraft.bukkit.jobs.skills;

import galaxyoyo.minecraft.bukkit.jobs.skills.listeners.BlockBreakingSkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

public class MineInstantlySkill extends Skill implements BlockBreakingSkill
{
	private List<Material> blocks = new ArrayList<Material>();
	private int maxQuantity;
	private int quantity;
	
	public MineInstantlySkill(int cooldown, String id, int quantity, Material ... blocks)
	{
		super (cooldown, id);
		this.maxQuantity = quantity;
		this.quantity = 0;
		this.blocks = Arrays.asList(blocks);
	}
	
	protected MineInstantlySkill()
	{
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
		String blocks = getBlocks().toString().substring(1);
		blocks = blocks.substring(0, blocks.length() - 1);
		return "Mine instantan\u00e9ment " + maxQuantity + " bloc" + (maxQuantity > 1 ? "s" : "") + " suivant"
				+ (maxQuantity > 1 ? "s" : "") + " : " + blocks + "\n\rActivation : clic droit avec une "
				+ "hache (bûcheron)";
	}
	
	public List<Material> getBlocks()
	{
		return blocks;
	}
	
	@Override
	public Skill clone()
	{
		return new MineInstantlySkill(getBaseCooldown(), getId(), maxQuantity,
						getBlocks().toArray(new Material[blocks.size()]));
	}
	
	@Override
	public boolean equals(Skill skill)
	{
		return getClass() == skill.getClass() && getBaseCooldown() == skill.getBaseCooldown()
				&& maxQuantity == ((MineInstantlySkill) skill).maxQuantity
				&& Arrays.deepEquals(blocks.toArray(), ((MineInstantlySkill) skill).blocks.toArray());
	}

	@Override
	public void onBlockBreaking(BlockDamageEvent event)
	{
		if (!isActivated())
			return;
		
		if (getCooldown() > 0)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "Veuillez attendre " + getFormattedTime(getCooldown()) + " avant de pouvoir faire cela \u00e0 nouveau.");
			return;
		}
		
		if (getBlocks().contains(event.getBlock().getType()))
			event.setInstaBreak(true);
		
		quantity++;
		if (quantity >= maxQuantity)
		{
			desactivate();
			quantity = 0;
			startCooldown();
		}
	}
}
