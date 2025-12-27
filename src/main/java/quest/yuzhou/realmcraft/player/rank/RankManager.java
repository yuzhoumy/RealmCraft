package quest.yuzhou.realmcraft.player.rank;

import org.bukkit.configuration.file.YamlConfiguration;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.types.Rank;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankManager {

    private final RealmCraft plugin;
    private List<Rank> rankList;
    private final YamlConfiguration config;

    public RankManager(RealmCraft plugin) {
        this.plugin = plugin;
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "ranks.yml"));
        loadRanks();
        plugin.getLogger().info(config.toString());
    }

    @SuppressWarnings("unchecked")
    private void loadRanks() {
        rankList = new ArrayList<>();
        // 此處假設 ranks.yml 內是一個 List
        try {
            List<?> rankData = config.getMapList("ranks");
            plugin.getLogger().info("Looping each rank from rank list.");
            for (Object obj : rankData) {
                if (obj instanceof java.util.Map) {
                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                    String id = map.get("id").toString();
                    String name = map.get("name").toString();
                    int threshold = Integer.parseInt(map.get("threshold").toString());
                    rankList.add(new Rank(id, name, threshold));
                    plugin.getLogger().info("Loading rank: (" + id + "," + name + "," + threshold + ",");
                }
            }
        } catch (NullPointerException e) {
            plugin.getLogger().severe("Error while loading ranks. Did you write the config file correctly?");
            e.printStackTrace();
        }
        // 按門檻分數升序排序
        rankList.sort(Comparator.comparingInt(Rank::threshold));
    }

    /**
     * 根據分數取得玩家應該具備的段位，
     * 即取所有 threshold 不大於 score 的最後一個 Rank。
     */
    public Rank getRankByPoint(int score) {
        Rank result = null;
        for (Rank rank : rankList) {
            if (score >= rank.threshold()) {
                result = rank;
            } else {
                break;
            }
        }
        return result != null ? result : getRankById("unset");
    }

    /**
     * 根據段位 id 取得 Rank 物件，若找不到則傳回 null
     */
    public Rank getRankById(String id) {
        for (Rank rank : rankList) {
            if (rank.id().equalsIgnoreCase(id)) {
                return rank;
            }
        }
        return null;
    }

    /**
     * 取得給定 Rank 的下一個段位（若已是最高則傳回 null）
     */
    public Rank getNextRank(Rank current) {
        for (int i = 0; i < rankList.size(); i++) {
            if (rankList.get(i).id().equalsIgnoreCase(current.id())) {
                if (i + 1 < rankList.size()) {
                    return rankList.get(i + 1);
                }
            }
        }
        return null;
    }

    public List<Rank> getRankList() {
        return rankList;
    }

}
