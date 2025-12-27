package quest.yuzhou.realmcraft.misc.newbie;


import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewbieQuest implements Listener {

    protected RealmCraft plugin;
    protected int number;
    protected String displayName;
    protected String[] lore;
    protected String[] description;
    protected BossBar bossBar;
    protected List<Player> running;

    public NewbieQuest(RealmCraft plugin, int number, String displayName, String[] lore, String[] description) {
        this.plugin = plugin;
        this.number = number;
        this.displayName = displayName;
        this.lore = lore;
        this.description = description;
        this.running = new ArrayList<>();
        this.bossBar = Bukkit.createBossBar(
                ChatColor.YELLOW + "新手教學" + number + " : " + ChatColor.AQUA + displayName,
                BarColor.PURPLE,
                BarStyle.SOLID,
                BarFlag.DARKEN_SKY
        );
    }

    public void start(Player player, Boolean doWhenStart) {
        bossBar.addPlayer(player);
        running.add(player);
        player.sendMessage(Utilities.colorize("&6= - - - &e【新手教學 " + number + "&e】&6 - - - ="));
        player.sendMessage(Utilities.colorize("&6-> &e" + displayName));
        player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 10F, 0.5F);
        if (doWhenStart) doWhenStart(player);
        forceDoWhenStart(player);

        final int[] loopNumber = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (loopNumber[0] == description.length) {
                    cancel();
                    return;
                }
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 10, 1);
                player.sendMessage(Utilities.colorize("&b[政府官員] &f" + description[loopNumber[0]]));
                loopNumber[0]++;
            }
        }.runTaskTimer(plugin, 60, 150);
    }

    public void forceStop(Player player) {
        running.remove(player);
        bossBar.removePlayer(player);
    }

    public void fastSpeak(Player player) {
        player.sendMessage(Utilities.colorize("&6= - - - &e【新手教學 " + number + "&e】&6 - - - ="));
        player.sendMessage(Utilities.colorize("&6-> &e" + displayName));
        player.sendMessage(Utilities.colorize("&d&l來回顧一下之前政府官員講了什麽……"));
        player.playSound(player, "iaalchemy:ambient.creepy", 10, 1);
        for (String sentence : description) {
            player.sendMessage(Utilities.colorize("&6⏲ &8[政府官員] &f" + sentence));
        }
    }

    public boolean isQuestRunning(Player player) {
        return running.contains(player);
    }

    protected void completeQuest(Player player) {
        running.remove(player);
        bossBar.removePlayer(player);

        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 10, 1.3F);
        player.sendMessage(plugin.prefix + Utilities.colorize("&f- - - &6<&e任務完成&6>&f - - -"));

        try {
            plugin.getDatabase().updatePlayerHasPassedIntroStage(player.getUniqueId(), number + 1);
        } catch (SQLException e) {
            player.sendMessage(plugin.prefix + ChatColor.RED + "未知的錯誤發生了，請立即通報管理員。");
            plugin.getLogger().severe("Can't update player's has passed intro stage!");
            e.printStackTrace();
        }

        // 如果是最後一個quest
        if (number == plugin.getNewbieQuestManager().getQuestsAmount()) {
            player.sendMessage(plugin.prefix + Utilities.colorize("&e領域世界新手教學到此結束。之後請參閲&a /rc handbook &e瞭解更多玩法，或參閲玩家手冊：&b https://ling-yu-shi-jie-realmcraft.gitbook.io/"));
            player.sendMessage(plugin.prefix + Utilities.colorize(""));
            return;
        }

        // tutorial 10
        if (number == 10) {
            player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
            player.teleport(new Location(plugin.mainWorld, -29, -24, 17));
            player.sendMessage(plugin.prefix + ChatColor.AQUA + "恭喜你！ " + player.getName() + " ，你已經完成一半的新手教學了！");
            player.sendMessage(plugin.prefix + ChatColor.AQUA + "接下里，你還要繼續新手教學嗎？");
            return;
        }

        player.sendMessage(plugin.prefix + ChatColor.AQUA + "10秒後自動開始下一個任務。");

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getNewbieQuestManager().start(player, number + 1, true);
            }
        }.runTaskLater(plugin, 150);
    }

    protected void doWhenStart(Player player) {}

    protected void forceDoWhenStart(Player player) {}

    public int getNumber() {
        return number;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getLore() {
        return lore;
    }

    public String[] getDescription() {
        return description;
    }

    public List<Player> getRunning() {
        return running;
    }
}
