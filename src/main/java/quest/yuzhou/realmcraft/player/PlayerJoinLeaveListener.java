package quest.yuzhou.realmcraft.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

import java.sql.SQLException;

public class PlayerJoinLeaveListener implements Listener {

    private final RealmCraft plugin;

    public PlayerJoinLeaveListener(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        try {
            if (!plugin.getDatabase().playerExists(player.getUniqueId())) {
                plugin.getDatabase().addPlayer(player.getUniqueId());
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Error while adding player into database");
            e.printStackTrace();
        }

        int hasPassedIntroStage;

        try {
            hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(event.getPlayer().getUniqueId());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // DEBUG MSG
        if (plugin.isDebugOn()) plugin.getLogger().info(player.getName() + "'s has passed intro stage: " + hasPassedIntroStage);

        if (hasPassedIntroStage == 0) {
            player.teleport(new Location(plugin.fieldWorld, -506, -54, 830));
            if (player.hasPlayedBefore()) {
                player.sendMessage(plugin.prefix + ChatColor.GRAY + "系統檢測到你還未完成新手教學，便將你傳送至此。");
                return;
            }
        }

        int questsAmount = plugin.getNewbieQuestManager().getQuestsAmount();
        if (hasPassedIntroStage <= questsAmount && hasPassedIntroStage > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(plugin.prefix + "已自動繼續新手教學。");
                plugin.getNewbieQuestManager().start(player, hasPassedIntroStage, false);
            }, 600);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getSeasonManager().notifyPlayerClaimKit(player), 30 * 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (NewbieQuest newbieQuest : plugin.getNewbieQuestManager().getNewbieQuests()) {
            if (newbieQuest.getRunning().contains(event.getPlayer())) {
                newbieQuest.getRunning().remove(event.getPlayer());
                return;
            }
        }
    }

}
