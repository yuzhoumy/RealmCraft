package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class Reload implements SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "重新載入配置文件";
    }

    @Override
    public String getSyntax() {
        return "/rca reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        plugin.getConfigManager().loadAllConfigs();
        plugin.onDisable();
        plugin.onEnable();
        sender.sendMessage(plugin.prefix + "成功重載所有配置文件！本插件由 宇宙 (DC: yuzhou_) 製作。");
    }
}
