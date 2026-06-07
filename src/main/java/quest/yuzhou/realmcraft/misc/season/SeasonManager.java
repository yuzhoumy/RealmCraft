package quest.yuzhou.realmcraft.misc.season;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.types.Rank;
import quest.yuzhou.realmcraft.types.Season;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeasonManager {
    private final RealmCraft plugin;
    private List<Season> seasons;
    private Season currentSeason;
    private int currentDay;
    private LocalDate startDate;
    private final BukkitTask updateTask;
    private HashMap<Season, HashMap<Integer, HashMap<Rank, List<ItemStack>>>> rewardItemMap;

    private FileConfiguration fileConfiguration;
    private File file;

    private static final int DAYS_PER_SEASON = Season.getDaysInSeason();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SeasonManager(RealmCraft plugin) {
        this.plugin = plugin;
        loadConfiguration();
        calculateCurrentSeason();

        // Check if season changed while server was offline
        checkMissedSeasonChange();
        // Check every minute (1200 ticks = 60 seconds) for day changes
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::update, 0L, 1200L);
    }

    private void loadConfiguration() {
        file = new File(plugin.getDataFolder(), "season.yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        // Load seasons from config
        seasons = new ArrayList<>();
        ConfigurationSection seasonsSection = fileConfiguration.getConfigurationSection("seasons");

        if (seasonsSection == null) {
            plugin.getLogger().severe("No seasons configured! Please check config.yml");
            return;
        }

        List<String> keyList = new ArrayList<>(seasonsSection.getKeys(false));
        for (int i = 0; i < keyList.size(); i++) {
            seasons.add(new Season(i + 1, keyList.get(i)));
        }

        // Load start date
        String startDateStr = fileConfiguration.getString("start-date", "01/01/2025");
        try {
            startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
        } catch (Exception e) {
            plugin.getLogger().severe("Invalid start date format! Using default: 01/01/2025");
            startDate = LocalDate.of(2025, 1, 1);
        }

        plugin.getLogger().info("Loaded " + seasons.size() + " seasons");
        plugin.getLogger().info("Start date: " + startDate.format(DATE_FORMATTER));

        // Load reward items
        this.rewardItemMap = new HashMap<>();
        ConfigurationSection config = fileConfiguration.getConfigurationSection("seasons");

        // Create a temporary map to match season names to Season objects
        HashMap<String, Season> seasonNameMap = new HashMap<>();
        for (Season season : seasons) {
            seasonNameMap.put(season.name(), season);
        }

        for (String seasonKey : config.getKeys(false)) {
            // Get the Season object from our temporary map
            Season season = seasonNameMap.get(seasonKey);
            if (season == null) {
                plugin.getLogger().warning("Season '" + seasonKey + "' in rewards config not found in seasons list!");
                continue;
            }

            HashMap<Integer, HashMap<Rank, List<ItemStack>>> dayRewardMap = new HashMap<>();
            for (String dayKey : config.getConfigurationSection(seasonKey).getKeys(false)) {
                HashMap<Rank, List<ItemStack>> rankRewardMap = new HashMap<>();
                for (String rankKey : config.getConfigurationSection(seasonKey).getConfigurationSection(dayKey).getKeys(false)) {
                    List<ItemStack> itemStacks = new ArrayList<>();
                    for (String itemString : config.getConfigurationSection(seasonKey).getConfigurationSection(dayKey).getStringList(rankKey)) {
                        String[] split = itemString.split("-");

                        // Validate item format
                        if (split.length < 3) {
                            plugin.getLogger().severe("Invalid item format in " + seasonKey + "." + dayKey + "." + rankKey + ": '" + itemString + "'");
                            plugin.getLogger().severe("Expected format: TYPE-NAME-AMOUNT (e.g., MATERIAL-COLDORE-64)");
                            continue; // Skip this item
                        }

                        ItemStack mmoItem = MMOItems.plugin.getItem(split[0], split[1]);
                        if (mmoItem == null) {
                            plugin.getLogger().severe("Item not found: " + itemString + " in " + seasonKey + "." + dayKey + "." + rankKey);
                            continue; // Skip this item instead of crashing
                        }

                        try {
                            mmoItem.setAmount(Integer.parseInt(split[2]));
                        } catch (NumberFormatException e) {
                            plugin.getLogger().severe("Invalid amount in item: " + itemString + " in " + seasonKey + "." + dayKey + "." + rankKey);
                            e.printStackTrace();
                            continue; // Skip this item
                        }

                        itemStacks.add(mmoItem);
                    }
                    Rank rank = plugin.getRankManager().getRankById(rankKey);
                    if (rank == null) {
                        plugin.getLogger().severe("Rank must be either wood/iron/silver/gold/diamond instead of " + rankKey);
                        continue;
                    }
                    rankRewardMap.put(rank, itemStacks);
                }

                int day = 0;
                if (!dayKey.equalsIgnoreCase("final")) {
                    try {
                        String[] parts = dayKey.split("-");
                        if (parts.length < 2) {
                            plugin.getLogger().severe("The day '" + dayKey + "' is invalid in " + seasonKey);
                            continue;
                        }
                        day = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        plugin.getLogger().severe("The day '" + dayKey + "' has invalid number in " + seasonKey);
                        e.printStackTrace();
                        continue;
                    }
                    if (day % 7 != 0 || day > 21) {
                        plugin.getLogger().severe("Day must be either day-7, day-14 or day-21, not " + dayKey);
                        continue;
                    }
                }

                dayRewardMap.put(day, rankRewardMap);
            }
            rewardItemMap.put(season, dayRewardMap);
        }

        plugin.getLogger().info("Loaded reward items for " + rewardItemMap.size() + " seasons");
    }

    public void stop() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }

    private void update() {
        Season previousSeason = currentSeason;

        calculateCurrentSeason();

        // Check if season changed
        if (!currentSeason.equals(previousSeason)) {
            onSeasonChange(previousSeason, currentSeason);

            // Update last known season
            fileConfiguration.set("last-known-season", currentSeason.seasonNumber());
            try {
                fileConfiguration.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Error while saving season.yml");
                e.printStackTrace();
            }
        }
    }

    private void calculateCurrentSeason() {
        LocalDate now = LocalDate.now();
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, now);

        if (daysSinceStart < 0) {
            // Before start date - use first season, day 1
            currentSeason = seasons.get(0);
            currentDay = 1;
            return;
        }

        // Calculate which season we're in
        long totalSeasons = seasons.size();
        long seasonCycle = daysSinceStart / DAYS_PER_SEASON; // Which season cycle (0, 1, 2...)
        int seasonIndex = (int) (seasonCycle % totalSeasons);
        long dayInSeason = daysSinceStart % DAYS_PER_SEASON;

        currentSeason = seasons.get(seasonIndex);
        currentDay = (int) dayInSeason + 1; // Days are 1-indexed
    }

    /**
     * Check if a season change was missed while the server was offline
     */
    private void checkMissedSeasonChange() {
        // Get the saved last known season from config
        int lastKnownSeasonNumber = fileConfiguration.getInt("last-known-season", -1);

        if (lastKnownSeasonNumber == -1) {
            // First time running, save current season
            fileConfiguration.set("last-known-season", currentSeason.seasonNumber());
            try {
                fileConfiguration.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Error while saving season.yml");
                e.printStackTrace();
            }
            return;
        }

        // Check if current season is different from last known
        if (lastKnownSeasonNumber != currentSeason.seasonNumber()) {
            Season lastKnownSeason = getSeasonByNumber(lastKnownSeasonNumber);
            if (lastKnownSeason != null) {
                plugin.getLogger().info("Detected season change while server was offline!");
                onSeasonChange(lastKnownSeason, currentSeason);
            }

            // Update last known season
            fileConfiguration.set("last-known-season", currentSeason.seasonNumber());
            try {
                fileConfiguration.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Error while saving season.yml");
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when the season changes
     * This method can be overridden or extended for custom behavior
     */
    private void onSeasonChange(Season oldSeason, Season newSeason) {
        plugin.getLogger().info("Season changed from " + oldSeason.name()
                + " to " + newSeason.name());

        Bukkit.broadcastMessage(plugin.prefix + ChatColor.YELLOW + oldSeason.name() + " 賽季結束了。");
        Bukkit.broadcastMessage(plugin.prefix + "§f歡迎各位拾荒者來到全新的賽季： §a" + newSeason.name() + "§f！");

        // Call the custom season change handler
        handleSeasonChange(oldSeason, newSeason);
    }

    public void notifyPlayerClaimKit(Player player) {

        int hasPassedIntroStage = 0;
        try {
            hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(player.getUniqueId());
        } catch (SQLException e) {
            player.sendMessage(plugin.prefix + ChatColor.RED + "發生嚴重錯誤，請立即通知管理員。");
            plugin.getLogger().severe("Error while getting player's has passed intro stage:" + player);
            e.printStackTrace();
        }
        if (hasPassedIntroStage <= plugin.getNewbieQuestManager().getQuestsAmount()) return;

        player.playSound(player, Sound.ENTITY_MOOSHROOM_CONVERT, 10, 1);
        if (currentDay < 7) {
            if (currentSeason.seasonNumber() <= 2)
                return;
            player.sendMessage(plugin.prefix + "§6§l賽季更新：§e進入了新的賽季！賽季已從 §f" + getPreviousSeason().name() + " §e變爲 §f" + getCurrentSeason().name() + " §e！");
            player.sendMessage(plugin.prefix + "§e積分、段位皆已重置！");
            player.sendMessage(plugin.prefix + "§d§l季末獎勵禮包 §e現已開放領取。如果你在上個賽季有達到任何段位，請輸入 §a/rc season §e領取禮包。");
        } else if (currentDay < 14) {
            player.sendMessage(plugin.prefix + "§6§l賽季第一周禮包 §e現已開放領取！請輸入 §a/rc season");
        } else if (currentDay < 21) {
            player.sendMessage(plugin.prefix + "§5§l賽季第二周禮包 §e現已開放領取！請輸入 §a/rc season");
        } else if (currentDay < 28) {
            player.sendMessage(plugin.prefix + "§4§l賽季第三周禮包 §e現已開放領取！請輸入 §a/rc season");
        }
    }

    /**
     * Override this method to add custom logic when seasons change
     * Examples: change weather, spawn mobs, give rewards, etc.
     */
    public void handleSeasonChange(Season oldSeason, Season newSeason) {

        plugin.getLogger().info("handleSeasonChange called: " + oldSeason.name()
                + " -> " + newSeason.name());

        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.ENTITY_MOOSHROOM_CONVERT, 10, 1));
        Bukkit.broadcastMessage(plugin.prefix + "§6§l賽季更新：§e進入了新的賽季！賽季已從 §f" + oldSeason.name() + " §e變爲 §f" + newSeason.name() + " §e！");
        Bukkit.broadcastMessage(plugin.prefix + "§e積分、段位皆已重置！");
        Bukkit.broadcastMessage(plugin.prefix + "§d§l季末獎勵禮包 §e現已開放領取。如果你在上個賽季有達到任何段位，請輸入 §a/rc season §e領取禮包。");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "discord bcast **:whale: 賽季更新：**進入了新的賽季！賽季已從 " + oldSeason.name() + " 變爲 " + newSeason.name() + " ！\n積分、段位皆已重置！\n**季末獎勵禮包** 現已開放領取。如果你在上個賽季有達到任何段位，請輸入 `/rc season` 領取禮包。");
        int affectedRows = 0;
        try {
            affectedRows = plugin.getDatabase().archiveAndResetSeasonForAllPlayers();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error occur while archive and reset season for all players.");
            e.printStackTrace();
            Bukkit.broadcastMessage(plugin.prefix + ChatColor.RED + "發生嚴重錯誤，請立即通報管理員。");
        }
        plugin.getLogger().info("Season archived successfully, affected rows: " + affectedRows);

    }

    /**
     * Get the current day in the format "day/totalDays"
     * Example: "18/28"
     */
    public String getDayString() {
        return currentDay + "/" + DAYS_PER_SEASON;
    }

    /**
     * Get current day number (1-28)
     */
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * Get current season
     */
    public Season getCurrentSeason() {
        return currentSeason;
    }

    /**
     * Get total days in season (always 28)
     */
    public int getTotalDaysInSeason() {
        return DAYS_PER_SEASON;
    }

    /**
     * Get progress percentage (0.0 to 1.0)
     */
    public double getSeasonProgress() {
        return (double) currentDay / DAYS_PER_SEASON;
    }

    /**
     * Get progress as percentage string
     */
    public String getProgressPercentage() {
        return String.format("%.1f%%", getSeasonProgress() * 100);
    }

    /**
     * Get days remaining in current season
     */
    public int getDaysRemaining() {
        return DAYS_PER_SEASON - currentDay;
    }

    /**
     * Get the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Get when the next season starts
     */
    public LocalDate getNextSeasonDate() {
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        long daysUntilNextSeason = DAYS_PER_SEASON - (daysSinceStart % DAYS_PER_SEASON);
        return LocalDate.now().plusDays(daysUntilNextSeason);
    }

    /**
     * Get Start Date of this season
     */
    public LocalDate getThisSeasonStartDate() {
        int currentIndex = seasons.indexOf(currentSeason);
        return startDate.plusDays((long) currentIndex * DAYS_PER_SEASON);
    }

    /**
     * Get the next season
     */
    public Season getNextSeason() {
        int currentIndex = seasons.indexOf(currentSeason);
        int nextIndex = (currentIndex + 1) % seasons.size();
        return seasons.get(nextIndex);
    }

    /**
     * Get previous season
     */
    public Season getPreviousSeason() {
        int currentIndex = seasons.indexOf(currentSeason);
        int previousIndex = currentIndex - 1;
        return seasons.get(previousIndex);
    }

    /**
     * Get all seasons
     */
    public List<Season> getAllSeasons() {
        return new ArrayList<>(seasons);
    }

    /**
     * Manually set the start date (admin command)
     */
    public void setStartDate(LocalDate newStartDate) {
        this.startDate = newStartDate;
        fileConfiguration.set("start-date", newStartDate.format(DATE_FORMATTER));
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error while saving season.yml");
            e.printStackTrace();
        }

        // Recalculate current season
        calculateCurrentSeason();

        plugin.getLogger().info("Start date updated to: " + newStartDate.format(DATE_FORMATTER));
    }

    /**
     * Get season by number
     */
    public Season getSeasonByNumber(int seasonNumber) {
        for (Season season : seasons) {
            if (season.seasonNumber() == seasonNumber) {
                return season;
            }
        }
        return null;
    }

    public Season getSeasonByName(String name) {
        for (Season season : seasons) {
            if (season.name().equalsIgnoreCase(name)) {
                return season;
            }
        }
        return null;
    }

    public HashMap<Season, HashMap<Integer, HashMap<Rank, List<ItemStack>>>> getRewardItemMap() {
        return rewardItemMap;
    }

    public String getRewardItemMapToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6§l═══════════════════════════════════\n");
        sb.append("§e§l           賽季獎勵總覽\n");
        sb.append("§6§l═══════════════════════════════════\n\n");

        for (Map.Entry<Season, HashMap<Integer, HashMap<Rank, List<ItemStack>>>> seasonEntry : rewardItemMap.entrySet()) {
            Season season = seasonEntry.getKey();
            sb.append("§b§l▶ ").append(season.name()).append("\n");
            sb.append("§7§m                                   \n");

            HashMap<Integer, HashMap<Rank, List<ItemStack>>> dayMap = seasonEntry.getValue();

            // Sort days: 0 (final) should be last, others in ascending order
            List<Integer> sortedDays = new ArrayList<>(dayMap.keySet());
            sortedDays.sort((d1, d2) -> {
                if (d1 == 0) return 1;  // 0 (final) goes last
                if (d2 == 0) return -1;
                return d1.compareTo(d2);
            });

            for (Integer day : sortedDays) {
                HashMap<Rank, List<ItemStack>> rankMap = dayMap.get(day);

                if (day == 0) {
                    sb.append("  §d§l季末獎勵禮包\n");
                } else {
                    sb.append("  §a§l第 ").append(day / 7).append(" 周禮包 §7(第 ").append(day).append(" 天)\n");
                }

                for (Map.Entry<Rank, List<ItemStack>> rankEntry : rankMap.entrySet()) {
                    Rank rank = rankEntry.getKey();
                    List<ItemStack> items = rankEntry.getValue();

                    sb.append("    §f").append(rank.name()).append(" 段位§7: ");

                    if (items.isEmpty()) {
                        sb.append("§c無獎勵");
                    } else {
                        List<String> itemDescriptions = new ArrayList<>();
                        for (ItemStack item : items) {
                            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                String displayName = item.getItemMeta().getDisplayName();
                                int amount = item.getAmount();
                                itemDescriptions.add(displayName + " §7x" + amount);
                            }
                        }
                        sb.append(String.join("§7, ", itemDescriptions));
                    }
                    sb.append("\n");
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        sb.append("§6§l═══════════════════════════════════\n");
        return sb.toString();
    }
}
