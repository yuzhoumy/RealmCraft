package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class DoneRefreshResourcePoint implements SubCommand {

    @Override
    public String getName() {
        return "done-refresh-resource-point";
    }

    @Override
    public String getDescription() {
        return "當資源點刷新插件完成操作，將完成訊息傳過來，此插件負責重新啓動。";
    }

    @Override
    public String getSyntax() {
        return "/rca done-refresh-resource-point";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        plugin.getRestarter().doneRefreshResourcePoint();
    }
}
