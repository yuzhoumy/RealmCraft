package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class NewbieIntro implements SubCommand {

    @Override
    public String getName() {
        return "newbie-intro";
    }

    @Override
    public String getDescription() {
        return "體驗新手教學";
    }

    @Override
    public String getSyntax() {
        return "/rca newbie-intro";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令。");
            return;
        }

        plugin.getNewbieIntroduction().run(player);
    }
}
