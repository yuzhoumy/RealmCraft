package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class PrepareRestart implements SubCommand {

    @Override
    public String getName() {
        return "prepare-restart";
    }

    @Override
    public String getDescription() {
        return "使用後，十五分鐘后重啓。當參數 <refresh resource point> 為 true ，重啓之後將會刷新資源點，然後再自動重啓一次。";
    }

    @Override
    public String getSyntax() {
        return "/rca prepare-restart <refresh resource point>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (args.length == 1) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "正確用法：" + getSyntax());
            return;
        }
        if (plugin.getRestarter().isPreparingRestart()) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "已經正在準備重啓了。");
            return;
        }
        boolean refreshResourcePoint = Boolean.parseBoolean(args[1]);
        plugin.getRestarter().prepareRestart(refreshResourcePoint);
    }
}
