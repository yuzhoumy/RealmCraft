package quest.yuzhou.realmcraft.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class Handbook implements SubCommand {

    @Override
    public String getName() {
        return "handbook";
    }

    @Override
    public String getDescription() {
        return "打開伺服器説明書";
    }

    @Override
    public String getSyntax() {
        return "/rc handbook";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令。");
            return;
        }

        plugin.getMenuManager().getMenu(MenuManager.MenuType.MANUAL_MENU).open(player);
    }
}
