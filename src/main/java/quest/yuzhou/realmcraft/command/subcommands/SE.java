package quest.yuzhou.realmcraft.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class SE implements SubCommand {

    @Override
    public String getName() {
        return "se";
    }

    @Override
    public String getDescription() {
        return "打開“戰略事件”的菜單";
    }

    @Override
    public String getSyntax() {
        return "/rc se";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令。");
            return;
        }

        player.playSound(player, Sound.ITEM_ARMOR_EQUIP_DIAMOND, 10F, 0.5F);
        plugin.getMenuManager().getMenu(MenuManager.MenuType.STRATEGIC_EVENT_MENU).open(player);
    }
}
