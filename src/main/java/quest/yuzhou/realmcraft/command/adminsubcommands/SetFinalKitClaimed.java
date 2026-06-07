package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

import java.sql.SQLException;

public class SetFinalKitClaimed implements SubCommand {

    @Override
    public String getName() {
        return "set-final-kit-claimed";
    }

    @Override
    public String getDescription() {
        return "設定玩家領取季末獎勵狀態。";
    }

    @Override
    public String getSyntax() {
        return "/rca set-final-kit-claimed <player> <boolean>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {

        if (args.length != 3) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "格式不對，請參照 " + getSyntax());
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

        if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "第四個參數必須是true或者false。");
            return;
        }
        boolean claimed = Boolean.parseBoolean(args[2]);

        try {
            plugin.getDatabase().setLastSeasonFinalKitClaimed(offlinePlayer.getUniqueId(), claimed);
        } catch (SQLException e) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "查詢資料庫時發生錯誤，請查看後臺報錯。");
            e.printStackTrace();
        }
        sender.sendMessage(plugin.prefix + ChatColor.GREEN + "成功設置 " + offlinePlayer.getName() + " 的領取季末獎勵狀態為 " + claimed);
    }
}
