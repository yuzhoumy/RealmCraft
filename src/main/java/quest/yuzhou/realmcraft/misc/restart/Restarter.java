package quest.yuzhou.realmcraft.misc.restart;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;

import java.io.File;
import java.io.IOException;

public class Restarter {

    private final RealmCraft plugin;
    private final boolean refreshingResourcePoint;
    private boolean preparingRestart;
    private BukkitTask task1;
    private BukkitTask task2;
    private BukkitTask task3;

    public Restarter(RealmCraft plugin) {
        this.plugin = plugin;
        this.refreshingResourcePoint = plugin.getConfig().getBoolean("refresh-resource-point-when-restart");
        this.preparingRestart = false;
        if (refreshingResourcePoint) refreshResourcePoint();
    }

    public void prepareRestart(boolean laterRefreshResourcePoint) {
        preparingRestart = true;
        String refreshMessage = laterRefreshResourcePoint ? "，並刷新所有資源點。" : "。";

        Bukkit.broadcastMessage(plugin.prefix + ChatColor.YELLOW + ChatColor.BOLD + "伺服器進入準備重啓狀態，部分活動（如戰略事件等等）不能再觸發/開始新的了。");

        task1 = new BukkitRunnable() {
            int minute = 10; //loop for 9 minutes
            @Override
            public void run() {
                Bukkit.broadcastMessage(plugin.prefix + "伺服器將在 " + minute + " 分鐘后重新啓動" + refreshMessage);
                if (minute == 1) {
                    cancel();
                    return;
                }

                minute--;
            }
        }.runTaskTimer(plugin, 0, 1200);

        task2 = new BukkitRunnable() {
            int second = 15;
            @Override
            public void run() {
                if (second == 0) {
                    cancel();
                    return;
                }
                Bukkit.broadcastMessage(plugin.prefix + "伺服器將在 " + second + " 秒后重新啓動" + refreshMessage);
                Bukkit.getOnlinePlayers().forEach((player) -> player.playSound(player, Sound.UI_BUTTON_CLICK, 10, 2));

                second--;
            }
        }.runTaskTimer(plugin, 11700, 20); // run after 9 minutes 45 seconds

        task3 = new BukkitRunnable() {
            @Override
            public void run() {
                if (laterRefreshResourcePoint) {
                    plugin.getConfig().set("refresh-resource-point-when-restart", true);
                    plugin.saveConfig();
                }
                checkConfigAndCreateRestartFlag();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }.runTaskLater(plugin, 12000); // run after 10 minutes
    }

    public void refreshResourcePoint() {
        checkConfigAndCreateRestartFlag();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "discord broadcast :wrench:**開始刷新資源點** 請耐心等待數分鐘。");
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rp clean-and-refresh all true");
                plugin.getLogger().info("ResourcePoint command was run.");
            }
        }.runTaskLater(plugin, 300);
    }

    // to be passed by RCResourcePoint/RCPaste Plugin
    public void doneRefreshResourcePoint() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "discord broadcast :wrench:**資源點刷新完畢** 重啓後，玩家即可繼續游玩。");
        plugin.getConfig().set("refresh-resource-point-when-restart", false);
        plugin.saveConfig();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
    }

    public boolean isRefreshingResourcePoint() {
        return refreshingResourcePoint;
    }

    public boolean isPreparingRestart() {
        return preparingRestart;
    }

    private void checkConfigAndCreateRestartFlag() {
        if (plugin.getConfig().getBoolean("create-restart-flag")) {
            try {
                // create file when restarting, so server will not shut down.
                // cuz im using run.bat to open srv
                new File("restart.flag").createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Serious error when creating restart.flag:");
                e.printStackTrace();
            }
        }
    }

    public void cancelRestart() {
        try {
            task1.cancel();
            task2.cancel();
            task3.cancel();
        } catch (Exception e) {
            plugin.getLogger().severe("Error while cancelling tasks. cancelRestart() was run when server is not in preparing restart state?");
            e.printStackTrace();
        }
        preparingRestart = false;
        Bukkit.broadcastMessage(plugin.prefix + "重啓排程已取消，準備重啓狀態已終止。");
    }
}
