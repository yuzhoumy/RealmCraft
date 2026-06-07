package quest.yuzhou.realmcraft.misc.chestunlocker;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;

import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ChestUnlocker {

    private final RealmCraft plugin;
    private final List<LocalTime> unlockTimeList;
    private final List<BukkitTask> tasks;

    public ChestUnlocker(RealmCraft plugin) {
        this.plugin = plugin;
        this.tasks = new ArrayList<>();

        unlockTimeList = new ArrayList<>();
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "chest-unlocker.yml"));
        for (String unlockTime : fileConfiguration.getStringList("unlock-time")) {
            LocalTime localTime = null;
            try {
                localTime = LocalTime.parse(unlockTime);
            } catch (DateTimeParseException e) {
                plugin.getLogger().severe(unlockTime + " is not a valid LocalTime! Please use hh/mm/ss");
                e.printStackTrace();
            }
            unlockTimeList.add(localTime);
        }

        for (LocalTime localTime : unlockTimeList) {
            long minute30reminderDelay = getPositiveDelay(localTime.minusMinutes(15));
            long minute5reminderDelay = getPositiveDelay(localTime.minusMinutes(5));
            long delay = getPositiveDelay(localTime); // get delay in ticks;
            tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, () -> remind(30), minute30reminderDelay, 86400 * 20));
            tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, () -> remind(5), minute5reminderDelay, 86400 * 20));
            tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, this::unlockChests, delay, 86400 * 20)); // repeat each day
        }

        plugin.getLogger().info("Unlock chest time: " + String.join("\n", unlockTimeList.stream().map(LocalTime::toString).toList()));
    }

    private void unlockChests() {
        int affected = plugin.getRCChest().getChestAndCommandManager().clearBlockAbove();
        plugin.getLogger().info("Unlocked " + affected + " chests.");
        plugin.broadcast(plugin.prefix + "§b所有資源箱已解鎖！");
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 10, 2));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "discord bcast :unlock: **所有資源箱已解鎖！**");
    }

    /**
     * Remind player will unlock chest
     * @param minutesBeforeUnlock the countdown minute that will be broadcast to players
     */
    private void remind(int minutesBeforeUnlock) {
        plugin.broadcast(plugin.prefix + "§b所有資源箱將在 §f" + minutesBeforeUnlock + " §b分鐘后解鎖！");
    }

    private long getPositiveDelay(LocalTime localTime) {
        Duration delay = Duration.between(LocalTime.now(), localTime);
        if (delay.isNegative()) delay = delay.plusDays(1);
        return delay.toSeconds() * 20; // to ticks
    }

    public void stop() {
        tasks.forEach(BukkitTask::cancel);
    }

    public List<LocalTime> getUnlockTimeList() {
        return unlockTimeList;
    }
}
