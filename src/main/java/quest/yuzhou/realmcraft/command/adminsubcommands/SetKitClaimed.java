package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

import java.sql.SQLException;

public class SetKitClaimed implements SubCommand {

    @Override
    public String getName() {
        return "set-kit-claimed";
    }

    @Override
    public String getDescription() {
        return "設定玩家的已領取賽季每周獎勵狀態。";
    }

    @Override
    public String getSyntax() {
        return "/rca set-kit-claimed <rank> <day> <player> <boolean>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (args.length != 5) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "錯誤的格式，請參照 " + getSyntax());
            return;
        }

        int day = 0;
        try {
            day = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "請在第三個參數輸入 7, 14 或者 21");
            e.printStackTrace();
        }
        if (day % 7 != 0 || day > 21) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "請在第三個參數輸入 7, 14 或者 21");
            return;
        }

        if (!args[4].equalsIgnoreCase("true") && !args[4].equalsIgnoreCase("false")) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "第四個參數必須是true或者false。");
            return;
        }
        boolean claimed = Boolean.parseBoolean(args[4]);

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[3]);
        try {
            plugin.getDatabase().setKitClaimed(offlinePlayer.getUniqueId(), args[1], day, claimed);
        } catch (SQLException e) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "查詢資料庫時發生錯誤，請查看後臺報錯。");
            e.printStackTrace();
        }
        sender.sendMessage(plugin.prefix + ChatColor.GREEN + "成功將 " + offlinePlayer.getName() + " 的領取第 " + day + " 天 " + args[1] + " 獎勵禮包設置爲 " + args[4]);
    }
}
