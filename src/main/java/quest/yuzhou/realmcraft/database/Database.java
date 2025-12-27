package quest.yuzhou.realmcraft.database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.PlayerRanking;
import quest.yuzhou.realmcraft.types.Rank;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {

    private final RealmCraft plugin;
    private final Connection connection;

    public Database(RealmCraft plugin, String path) throws SQLException {
        this.plugin = plugin;

        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            // Create initial table structure
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    point INTEGER NOT NULL DEFAULT 0,
                    rank TEXT NOT NULL DEFAULT 'unset',
                    hasPassedIntroStage INTEGER NOT NULL DEFAULT 0)
            """);

            // Add killCount column if it doesn't exist (for backward compatibility)
            addColumnIfNotExists("players", "killCount", "INTEGER NOT NULL DEFAULT 0");

            // Add killStreak column if it doesn't exist (for backward compatibility)
            addColumnIfNotExists("players", "killStreak", "INTEGER NOT NULL DEFAULT 0");

            // Add kit claim columns (5 ranks * 3 days = 15 columns)
            // Format: kitClaimed_<rank>_<day>
            String[] ranks = {"wood", "iron", "silver", "gold", "diamond"};
            String[] days = {"day7", "day14", "day21"};

            for (String rank : ranks) {
                for (String day : days) {
                    String columnName = "kitClaimed_" + rank + "_" + day;
                    addColumnIfNotExists("players", columnName, "INTEGER NOT NULL DEFAULT 0");
                }
            }

            addColumnIfNotExists("players", "lastSeasonFinalKitClaimed", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists("players", "lastSeasonPoint", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfNotExists("players", "lastSeasonRank", "TEXT NOT NULL DEFAULT 'unset'");
        }
    }

    /**
     * Helper method to add a column if it doesn't exist
     * Ensures backward compatibility with existing databases
     */
    private void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, columnName);

        if (!rs.next()) {
            // Column doesn't exist, add it
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
                plugin.getLogger().info("已新增欄位 " + columnName + " 至 " + tableName + " 表格");
            }
        }
        rs.close();
    }

    private void dropColumnIfExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getColumns(null, null, tableName, columnName);

        if (rs.next()) {
            // Column exists, drop it
            try (Statement statement = connection.createStatement()) {
                statement.execute("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
                plugin.getLogger().info("已刪除欄位 " + columnName + " 從 " + tableName + " 表格");
            }
        }
        rs.close();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean playerExists(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void addPlayer(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid) VALUES (?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public void updatePlayerHasPassedIntroStage(UUID uuid, int stage) throws SQLException {

        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET hasPassedIntroStage = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, stage);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getPlayerHasPassedIntroStage(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT hasPassedIntroStage FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("hasPassedIntroStage");
            }
        }
        return 0;
    }

    /**
     * Increment kill count for a player
     * @param uuid Player's UUID
     * @param amount Number of kills to add (can be negative to subtract)
     */
    public void addKillCount(UUID uuid, int amount) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE players SET killCount = killCount + ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }

        // Notify player if online
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            int newKillCount = getKillCount(uuid);
            if (amount > 0) {
                player.sendMessage(Utilities.colorize(plugin.prefix + "&f擊殺數 +&b" + amount + " &f(總計: &b" + newKillCount + "&f)"));
            } else if (amount < 0) {
                player.sendMessage(Utilities.colorize(plugin.prefix + "&c擊殺數 -&b" + Math.abs(amount) + " &c(總計: &b" + newKillCount + "&c)"));
            }
        }
    }

    /**
     * Get kill count for a player
     * @param uuid Player's UUID
     * @return Kill count
     */
    public int getKillCount(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
            return 0;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT killCount FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("killCount");
            }
        }
        return 0;
    }

    /**
     * Set kill count for a player directly
     * @param uuid Player's UUID
     * @param killCount New kill count value
     */
    public void setKillCount(UUID uuid, int killCount) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE players SET killCount = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, killCount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Increment kill streak for a player
     * @param uuid Player's UUID
     * @param amount Number to add to kill streak (usually 1)
     */
    public void addKillStreak(UUID uuid, int amount) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE players SET killStreak = killStreak + ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        }

        // Notify player if online
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            int newStreak = getKillStreak(uuid);
            player.sendMessage(Utilities.colorize(plugin.prefix + "&e連殺紀錄：&6" + newStreak));
        }
    }

    /**
     * Get kill streak for a player
     * @param uuid Player's UUID
     * @return Current kill streak
     */
    public int getKillStreak(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
            return 0;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT killStreak FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("killStreak");
            }
        }
        return 0;
    }

    /**
     * Clear (reset) kill streak for a player to 0
     * @param uuid Player's UUID
     */
    public void clearKillStreak(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE players SET killStreak = 0 WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }

        // Notify player if online
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            player.sendMessage(Utilities.colorize(plugin.prefix + "&c連殺紀錄已中斷！"));
        }
    }

    public void addScore(UUID uuid, int amount) throws SQLException, NullPointerException {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        int point = 0;
        String currentRankId = "unset";

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT point, rank FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                point = resultSet.getInt("point");
                currentRankId = resultSet.getString("rank");
            }
        }

        point += amount;
        Rank newRank = plugin.getRankManager().getRankByPoint(point);
        if (newRank == null) {
            plugin.getLogger().warning("未能根據點數 " + point + " 找到對應的段位，將預設為 'unset'");
            newRank = plugin.getRankManager().getRankById("unset");
        }
        String newRankId = newRank.id();

        try ( PreparedStatement updateStatement = connection.prepareStatement("UPDATE players SET point = ?, rank = ? WHERE uuid = ?")) {
            updateStatement.setInt(1, point);
            updateStatement.setString(2, newRankId);
            updateStatement.setString(3, uuid.toString());
            updateStatement.executeUpdate();
        }

        if (offlinePlayer.isOnline()) {
            Player player = (Player) offlinePlayer;
            if (amount > 0) {
                player.sendMessage(Utilities.colorize(plugin.prefix + "&f您得到了 &b" + amount + " &f積分。"));
            } else {
                player.sendMessage(Utilities.colorize(plugin.prefix + "&c您失去了 &b" + Math.abs(amount) + " &c積分。"));
            }
            player.sendMessage(Utilities.colorize(plugin.prefix + "&f目前積分：&b" + point));

            // 取得舊段位的設定
            Rank oldRank = plugin.getRankManager().getRankById(currentRankId);
            if (oldRank == null) {
                oldRank = new Rank("unset", "無段位", 0);
            }
            if (!oldRank.id().equals(newRank.id())) {
                // 段位變更：升級或降級
                if (newRank.threshold() > oldRank.threshold()) {
                    player.sendMessage(Utilities.colorize("&e恭喜您已升級至 " + newRank.name() + " &e段位！"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                } else {
                    player.sendMessage(Utilities.colorize("&f很不幸地，您已掉至 " + newRank.name() + " &f段位。。。"));
                }
            } else {
                // 段位未變：提示距離下個段位還需多少分
                Rank next = plugin.getRankManager().getNextRank(newRank);
                if (next != null) {
                    int need = next.threshold() - point;
                    player.sendMessage(Utilities.colorize("&f您目前的段位是 " + newRank.name() +
                            " &f。還需要 &b" + need + " &f積分升級至 " + next.name() + " &f段位。"));
                } else {
                    player.sendMessage(Utilities.colorize("&f您目前的段位是 " + newRank.name() +
                            " &f。恭喜您已經達到最高段位！"));
                }
            }
        }
    }

    /**
     * 取得指定段位中積分前幾名玩家（依分數由高到低排序）
     * @param rank 要查詢的段位（例如 "wood", "iron" 等）
     * @param limit 最多顯示數量
     * @param offset 從第幾名開始（通常為 0）
     * @return 玩家排名資料列表
     */
    public List<PlayerRanking> getTopPlayers(String rank, int limit, int offset) throws SQLException {
        List<PlayerRanking> list = new ArrayList<>();

        try (
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT uuid, point FROM players WHERE rank = ? ORDER BY point DESC LIMIT ? OFFSET ?")
        ) {
            ps.setString(1, rank);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuidStr = rs.getString("uuid");
                int score = rs.getInt("point");
                list.add(new PlayerRanking(UUID.fromString(uuidStr), score));
            }
        }
        return list;
    }

    /**
     * 取得擊殺數前幾名玩家（依擊殺數由高到低排序）
     * @param limit 最多顯示數量
     * @param offset 從第幾名開始（通常為 0）
     * @return 玩家排名資料列表
     */
    public List<PlayerRanking> getTopPlayersByKillCount(int limit, int offset) throws SQLException {
        List<PlayerRanking> list = new ArrayList<>();

        try (
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT uuid, killCount FROM players ORDER BY killCount DESC LIMIT ? OFFSET ?")
        ) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuidStr = rs.getString("uuid");
                int killCount = rs.getInt("killCount");
                list.add(new PlayerRanking(UUID.fromString(uuidStr), killCount));
            }
        }
        return list;
    }

    /**
     * 取得連殺紀錄前幾名玩家（依連殺數由高到低排序）
     * @param limit 最多顯示數量
     * @param offset 從第幾名開始（通常為 0）
     * @return 玩家排名資料列表
     */
    public List<PlayerRanking> getTopPlayersByKillStreak(int limit, int offset) throws SQLException {
        List<PlayerRanking> list = new ArrayList<>();

        try (
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT uuid, killStreak FROM players ORDER BY killStreak DESC LIMIT ? OFFSET ?")
        ) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuidStr = rs.getString("uuid");
                int killStreak = rs.getInt("killStreak");
                list.add(new PlayerRanking(UUID.fromString(uuidStr), killStreak));
            }
        }
        return list;
    }

    public Rank getRankByPlayer(OfflinePlayer player) throws SQLException {

        if (!playerExists(player.getUniqueId())) {
            addPlayer(player.getUniqueId());
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT rank FROM players WHERE uuid = ?" )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return plugin.getRankManager().getRankById(resultSet.getString("rank"));
            }
        }

        return null;
    }

    /**
     * Get a player's last season rank
     * @param uuid Player's UUID
     * @return Last season rank
     */
    public Rank getLastSeasonRank(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT lastSeasonRank FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String rankId = resultSet.getString("lastSeasonRank");
                return plugin.getRankManager().getRankById(rankId);
            }
        }

        return plugin.getRankManager().getRankById("unset");
    }

    /**
     * Get a player's last season point
     * @param uuid Player's UUID
     * @return Last season point
     */
    public int getLastSeasonPoint(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
            return 0;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT lastSeasonPoint FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("lastSeasonPoint");
            }
        }

        return 0;
    }

    /**
     * Archive current season data and reset for new season (for all players)
     * - Copies rank to lastSeasonRank
     * - Copies point to lastSeasonPoint
     * - Resets rank to 'unset'
     * - Resets point to 0
     * - Clears all kit claims
     * @return Number of players affected
     */
    public int archiveAndResetSeasonForAllPlayers() throws SQLException {
        int affectedRows = 0;

        try (Statement statement = connection.createStatement()) {
            // Archive rank and point, then reset them
            affectedRows = statement.executeUpdate(
                    "UPDATE players SET lastSeasonRank = rank, lastSeasonPoint = point, rank = 'unset', point = 0"
            );

            plugin.getLogger().info("已為 " + affectedRows + " 位玩家封存並重置賽季數據");
        }

        // Also clear all kit claims for new season
        clearAllKitClaimsForAllPlayers();

        return affectedRows;
    }

    /**
     * Check if a player has claimed a specific kit
     * @param uuid Player's UUID
     * @param rank Kit rank (wood, iron, silver, gold, diamond)
     * @param day Kit day (day7, day14, day21)
     * @return true if kit has been claimed, false otherwise
     */
    public boolean hasClaimedKit(UUID uuid, String rank, String day) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
            return false;
        }

        String columnName = "kitClaimed_" + rank + "_day" + day;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " + columnName + " FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(columnName) == 1;
            }
        }
        return false;
    }

    public boolean hasClaimedLastSeasonFinalKit(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
            return false;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT lastSeasonFinalKitClaimed FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("lastSeasonFinalKitClaimed") == 1;
        }
        return false;
    }

    public void setLastSeasonFinalKitClaimed(UUID uuid) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET lastSeasonFinalKitClaimed = 1 WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Mark a kit as claimed for a player
     * @param uuid Player's UUID
     * @param rank Kit rank (wood, iron, silver, gold, diamond)
     * @param day Kit day (7, 14, 21)
     */
    public void setKitClaimed(UUID uuid, String rank, int day) throws SQLException {
        if (!playerExists(uuid)) {
            addPlayer(uuid);
        }

        String columnName = "kitClaimed_" + rank + "_day" + day;

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE players SET " + columnName + " = 1 WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Clear all kit claims for all players (use at season reset)
     */
    public void clearAllKitClaimsForAllPlayers() throws SQLException {
        String[] ranks = {"wood", "iron", "silver", "gold", "diamond"};
        String[] days = {"day7", "day14", "day21"};

        // Build UPDATE query to reset all kit columns to 0
        StringBuilder sql = new StringBuilder("UPDATE players SET ");
        List<String> columns = new ArrayList<>();

        for (String rank : ranks) {
            for (String day : days) {
                columns.add("kitClaimed_" + rank + "_day" + day + " = 0");
            }
        }

        sql.append(String.join(", ", columns));

        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql.toString());
            plugin.getLogger().info("已重置 " + affectedRows + " 位玩家的禮包領取紀錄");
        }
    }

    public int getScore(OfflinePlayer player) throws SQLException {
        if (!playerExists(player.getUniqueId())) {
            addPlayer(player.getUniqueId());
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT point FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("point");
            }
        }
        return 0;
    }

    public List<String> queryPlayerInfo(OfflinePlayer player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> playerInfo = new ArrayList<>();

            if (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    String value = resultSet.getString(i);
                    playerInfo.add(columnName + " : " + value);
                }
                return playerInfo;
            }
        }
        throw new NullPointerException("Player not found");
    }

    public boolean playerHasEnoughScore(Player player, int score) throws SQLException {
        return getScore(player) >= score;
    }

}