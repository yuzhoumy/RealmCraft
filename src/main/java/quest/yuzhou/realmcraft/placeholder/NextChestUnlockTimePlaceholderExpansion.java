package quest.yuzhou.realmcraft.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import quest.yuzhou.realmcraft.RealmCraft;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class NextChestUnlockTimePlaceholderExpansion extends PlaceholderExpansion {

    private final RealmCraft plugin;

    public NextChestUnlockTimePlaceholderExpansion(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "nextChestUnlockTime";
    }

    @Override
    public String getAuthor() {
        return "Yuzhou";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        List<LocalTime> unlockChestTimeList = plugin.getChestUnlocker().getUnlockTimeList();
        Collections.sort(unlockChestTimeList);

        LocalTime now = LocalTime.now();
        LocalTime nextUnlockTime = null;

        // Find the next unlock time after now
        for (LocalTime unlockTime : unlockChestTimeList) {
            if (unlockTime.isAfter(now)) {
                nextUnlockTime = unlockTime;
                break;
            }
        }

        // If no unlock time found today, use the first one tomorrow
        if (nextUnlockTime == null) {
            nextUnlockTime = unlockChestTimeList.get(0);
        }

        Duration duration = Duration.between(now, nextUnlockTime);
        if (duration.isNegative()) duration = duration.plusDays(1);

        int hours = (int) duration.toHours();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
