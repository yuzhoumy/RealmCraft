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

public class RankingPlaceholderExpansion extends PlaceholderExpansion {

    private final RealmCraft plugin;

    public RankingPlaceholderExpansion(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "rank"; // 使用 %rank_段位_排名% 的格式，例如 %rank_wood_1%
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

        // %rank_score% 返回當前玩家的積分
        if (identifier.equalsIgnoreCase("score")) {
            try {
                int score = plugin.getDatabase().getScore(player);
                return String.valueOf(score);
            } catch (SQLException e) {
                plugin.getLogger().severe("Error while getting player score");
                e.printStackTrace();
                return "錯誤";
            }
        }

        // 識別字串應該以底線分隔，例如 wood_1
        String[] parts = identifier.split("_");
        if (parts.length != 2) return "";
        String rankCategory = parts[0].toLowerCase();
        int position;
        try {
            position = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "";
        }
        // 檢查 rankCategory 是否合法
        if (plugin.getRankManager().getRankList()
                .stream()
                .anyMatch(rank -> rank.name().equalsIgnoreCase(rankCategory))) {
            return "";
        }
        List<PlayerRanking> list = new ArrayList<>();
        try {
            list = plugin.getDatabase().getTopPlayers(rankCategory, 15, 0);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while loading top rank players");
            e.printStackTrace();
        }
        if (position < 1 || position > list.size()) {
            return "";
        }
        PlayerRanking playerRanking = list.get(position - 1);
        OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(playerRanking.uuid());
        return Utilities.colorize("&e" + position + ": &f" + topPlayer.getName() + " &b" + playerRanking.score());
    }

}
