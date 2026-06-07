package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class CancelRestart implements SubCommand {

    @Override
    public String getName() {
        return "cancel-restart";
    }

    @Override
    public String getDescription() {
        return "如果伺服器進入了準備重啓狀態，取消重啓排程。";
    }

    @Override
    public String getSyntax() {
        return "/rca cancel-restart";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!plugin.getRestarter().isPreparingRestart()) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "伺服器并沒有進入準備重啓狀態。");
            return;
        }

        plugin.getRestarter().cancelRestart();
    }
}
