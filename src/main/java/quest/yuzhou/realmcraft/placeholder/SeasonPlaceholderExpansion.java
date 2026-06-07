package quest.yuzhou.realmcraft.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.season.SeasonManager;

import java.time.format.DateTimeFormatter;

public class SeasonPlaceholderExpansion extends PlaceholderExpansion {

    private final RealmCraft plugin;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SeasonPlaceholderExpansion(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "season";
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
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        SeasonManager manager = plugin.getSeasonManager();

        // %season_day%
        if (params.equalsIgnoreCase("day")) {
            return manager.getDayString();
        }

        // %season_current%
        if (params.equalsIgnoreCase("current")) {
            return manager.getCurrentSeason().name();
        }

        // %season_number%
//        if (params.equalsIgnoreCase("number")) {
//            return String.valueOf(manager.getCurrentSeason().seasonNumber());
//        }

        // %season_day_number%
        if (params.equalsIgnoreCase("day_number")) {
            return String.valueOf(manager.getCurrentDay());
        }

        // %season_progress%
        if (params.equalsIgnoreCase("progress")) {
            return manager.getProgressPercentage();
        }

        // %season_remaining%
//        if (params.equalsIgnoreCase("remaining")) {
//            return String.valueOf(manager.getDaysRemaining());
//        }

        // %season_next%
        if (params.equalsIgnoreCase("next")) {
            return manager.getNextSeason().name();
        }

        // %season_next_date%
        if (params.equalsIgnoreCase("next_date")) {
            return manager.getNextSeasonDate().format(DATE_FORMATTER);
        }

        // %season_start_date%
        if (params.equalsIgnoreCase("start_date")) {
            return manager.getStartDate().format(DATE_FORMATTER);
        }

        return null;
    }
}
