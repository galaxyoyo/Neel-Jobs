package galaxyoyo.minecraft.bukkit.jobs.executors;

import galaxyoyo.minecraft.bukkit.jobs.CompetenceTree;
import galaxyoyo.minecraft.bukkit.jobs.Job;
import galaxyoyo.minecraft.bukkit.jobs.skills.ForgeSkill;
import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;
import me.cybermaxke.merchants.api.Merchant;
import me.cybermaxke.merchants.api.MerchantAPI;
import me.cybermaxke.merchants.api.Merchants;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ForgeCommandExecutor implements CommandExecutor
{	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.DARK_RED + "Seuls les joueurs ont acc\u00e8s \u00e0 cette commande");
			return true;
		}
		
		CraftPlayer player = (CraftPlayer) sender;
		CompetenceTree tree = CompetenceTree.get(player);
		MerchantAPI api = Merchants.get();
		Merchant merchant = api.newMerchant(ChatColor.DARK_GRAY + "Forge");
		for (Skill s : tree.getActiveSkills(Job.BLACKSMITH))
		{
			ForgeSkill skill = (ForgeSkill) s;
			merchant.addOffer(api.newOffer(skill.getSell(), skill.getBuy()));
		}
		merchant.addCustomer(player);
		
		return true;
	}
}
