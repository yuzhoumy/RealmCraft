package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class Debug implements SubCommand {

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "開啓/關閉除錯模式。開啓後會顯示各種報告訊息。";
    }

    @Override
    public String getSyntax() {
        return "/rca debug";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        plugin.setDebug(!plugin.isDebugOn());

        if (plugin.isDebugOn()) {
            sender.sendMessage(plugin.prefix + "除錯模式已開啓。");
        } else {
            sender.sendMessage(plugin.prefix + "除錯模式已關閉。");
        }
    }
}
