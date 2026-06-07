package quest.yuzhou.realmcraft.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.PlayerRanking;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KillCounterPlaceholderExpansion extends PlaceholderExpansion {

    private final RealmCraft plugin;

    public KillCounterPlaceholderExpansion(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "kill";
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
        String[] parts = identifier.split("_");
        switch (parts[0]) {
            case "count": {
                if (parts.length == 1) {
                    try {
                        return String.valueOf(plugin.getDatabase().getKillCount(player.getUniqueId()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin.getLogger().severe("Error while getting player's kill count");
                        return "错误";
                    }
                } else if (parts.length == 2) {
                    int position;
                    List<PlayerRanking> topPlayers = new ArrayList<>();

                    try {
                        position = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        return "错误";
                    }

                    try {
                        topPlayers = plugin.getDatabase().getTopPlayersByKillCount(15, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin.getLogger().severe("Error while loading top kill count players.");
                    }

                    if (position < 1 || position > topPlayers.size()) {
                        return "错误";
                    }

                    PlayerRanking playerRanking = topPlayers.get(position - 1);
                    OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(playerRanking.uuid());
                    return Utilities.colorize("&e" + position + ": &f" + topPlayer.getName() + " &b" + playerRanking.score());
                }
            }
            case "streak": {
                if (parts.length == 1) {
                    try {
                        return String.valueOf(plugin.getDatabase().getKillStreak(player.getUniqueId()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin.getLogger().severe("Error while getting player's kill streak");
                        return "错误";
                    }
                } else if (parts.length == 2) {
                    int position;
                    List<PlayerRanking> topPlayers = new ArrayList<>();

                    try {
                        position = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        return "错误";
                    }

                    try {
                        topPlayers = plugin.getDatabase().getTopPlayersByKillStreak(15, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        plugin.getLogger().severe("Error while loading top kill count players.");
                    }

                    if (position < 1 || position > topPlayers.size()) {
                        return "错误";
                    }

                    PlayerRanking playerRanking = topPlayers.get(position - 1);
                    OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(playerRanking.uuid());
                    return Utilities.colorize("&e" + position + ": &f" + topPlayer.getName() + " &b" + playerRanking.score());
                }
            }
        }
        return "错误";
    }
}
