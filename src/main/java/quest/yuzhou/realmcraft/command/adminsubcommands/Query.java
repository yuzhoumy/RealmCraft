package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Query implements SubCommand {

    @Override
    public String getName() {
        return "query";
    }

    @Override
    public String getDescription() {
        return "查看玩家在資料庫中的資料。";
    }

    @Override
    public String getSyntax() {
        return "/rca query <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {

        if (args.length != 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&c格式不對，請參照 &b" + getSyntax()));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
        List<String> playerInfo = new ArrayList<>();

        try {
            playerInfo = plugin.getDatabase().queryPlayerInfo(offlinePlayer);
        } catch (NullPointerException e) {
            sender.sendMessage(plugin.prefix + "找不到玩家。");
            plugin.getLogger().info("Player not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        playerInfo.forEach(sender::sendMessage);

    }
}
