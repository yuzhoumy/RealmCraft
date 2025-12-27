package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;
import quest.yuzhou.realmcraft.database.Database;

import java.sql.SQLException;

public class SetHasPassedIntroStage implements SubCommand {

    @Override
    public String getName() {
        return "set-has-passed-intro-stage";
    }

    @Override
    public String getDescription() {
        return "修改玩家新手教學的階段";
    }

    @Override
    public String getSyntax() {
        return "/rca set-has-passed-intro-stage <player> <integer>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {

        if (args.length != 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.prefix + "&c格式不對，請參照 &b" + getSyntax()));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
        int hasPassedIntroStage = Integer.parseInt(args[2]);
        int exceedAmount = plugin.getNewbieQuestManager().getQuestsAmount() + 1;

        if (hasPassedIntroStage > exceedAmount || hasPassedIntroStage < 0) {
            sender.sendMessage(plugin.prefix + "請輸入介於0~" + exceedAmount + "的數字");
            return;
        }

        Database database = plugin.getDatabase();
        try {
            database.updatePlayerHasPassedIntroStage(offlinePlayer.getUniqueId(), hasPassedIntroStage);
            sender.sendMessage(plugin.prefix + "成功更改玩家是否通過新手教學狀態");
        } catch (SQLException e) {
            sender.sendMessage(plugin.prefix + "發生錯誤，請看後臺。");
            plugin.getLogger().warning("An error occur while updatePlayerHasPassedIntro.");
            e.printStackTrace();
        }
    }
}
