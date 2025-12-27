package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

import java.sql.SQLException;

public class ForceQuest implements SubCommand {

    @Override
    public String getName() {
        return "force-quest";
    }

    @Override
    public String getDescription() {
        return "强制執行新手教學";
    }

    @Override
    public String getSyntax() {
        return "/rca force-quest [玩家名字]";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令。");
                return;
            }

            int hasPassedIntroStage = 0;

            try {
                hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(player.getUniqueId());
            } catch (SQLException e) {
                plugin.getLogger().severe("Error while getting player's has passed intro stage (force quest cmd)");
                e.printStackTrace();
            }

            // debug msg
            if (plugin.isDebugOn())
                plugin.getLogger().info("[DEBUG] has passed intro stage: " + hasPassedIntroStage);
            plugin.getNewbieQuestManager().forceStop(player);
            plugin.getNewbieQuestManager().start(player, hasPassedIntroStage, true);
        }

        if (args.length == 2) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!offlinePlayer.isOnline()) {
                sender.sendMessage(plugin.prefix + ChatColor.RED + "玩家不在綫！");
                return;
            }

            int hasPassedIntroStage = 0;

            try {
                hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(offlinePlayer.getUniqueId());
            } catch (SQLException e) {
                plugin.getLogger().severe("Error while getting player's has passed intro stage (force quest cmd)");
                e.printStackTrace();
            }

            plugin.getNewbieQuestManager().forceStop(offlinePlayer.getPlayer());
            plugin.getNewbieQuestManager().start(offlinePlayer.getPlayer(), hasPassedIntroStage, true);
        }
    }
}
