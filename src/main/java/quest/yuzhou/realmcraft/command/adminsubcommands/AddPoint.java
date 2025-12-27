package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.command.SubCommand;

import java.sql.SQLException;

public class AddPoint implements SubCommand {

    @Override
    public String getName() {
        return "add-point";
    }

    @Override
    public String getDescription() {
        return "增加一個玩家的積分";
    }

    @Override
    public String getSyntax() {
        return "/rca add-point <player> <amount>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!(args.length == 3)) {
            sender.sendMessage(Utilities.colorize(plugin.prefix + "&c格式不對，請參照 &b" + getSyntax()));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Utilities.colorize(plugin.prefix + "&c數量必須是一個合法的數字"));
            return;
        }
        try {
            plugin.getDatabase().addScore(target.getUniqueId(), amount);
        } catch (SQLException | NullPointerException e) {
            sender.sendMessage(Utilities.colorize(plugin.prefix + "&4發生致命錯誤，請查看後臺。"));
            plugin.getLogger().severe("Error while adding player's points through /rca add-point command");
            e.printStackTrace();
            return;
        }

        sender.sendMessage(Utilities.colorize(plugin.prefix + "&e成功增加" + amount + "點數給玩家" + target.getName()));
    }
}
