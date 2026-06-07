package quest.yuzhou.realmcraft.player.menu.seasonmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.AbstractMenu;
import quest.yuzhou.realmcraft.types.Rank;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SeasonMenu extends AbstractMenu {

    public SeasonMenu(RealmCraft plugin) {
        super(plugin);

        String title = plugin.getConfig().getString("season-menu-name");
        if (title == null) {
            plugin.getLogger().severe("Season menu name in config.yml not found.");
            title = "&e&l賽季";
        }
        menuName = Utilities.colorize(title);
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, menuName);

        ItemStack returnItem = new ItemStack(Material.PAPER);
        ItemMeta returnMeta = returnItem.getItemMeta();
        returnMeta.setCustomModelData(10071);
        returnMeta.setDisplayName("返回");
        returnItem.setItemMeta(returnMeta);

        ItemStack day7lockItem = new ItemStack(Material.PAPER);
        ItemMeta day7lockMeta = day7lockItem.getItemMeta();
        day7lockMeta.setDisplayName("§b禮包：第一周");
        List<String> day7lockLore = new ArrayList<>();
        if (plugin.getSeasonManager().getCurrentDay() >= 7) {
            day7lockLore.add("§e§o已解鎖！");
            day7lockMeta.setCustomModelData(10076);
        } else {
            day7lockLore.add("§4§o將在本季第 7 天解鎖 （ " + ChronoUnit.DAYS.between(LocalDate.now(), plugin.getSeasonManager().getThisSeasonStartDate().plusDays(7)) + " 天后）");
            day7lockMeta.setCustomModelData(10075);
        }
        day7lockMeta.setLore(day7lockLore);
        day7lockItem.setItemMeta(day7lockMeta);

        ItemStack day14lockItem = new ItemStack(Material.PAPER);
        ItemMeta day14lockMeta = day14lockItem.getItemMeta();
        day14lockMeta.setDisplayName("§b禮包：第二周");
        List<String> day14lockLore = new ArrayList<>();
        if (plugin.getSeasonManager().getCurrentDay() >= 14) {
            day14lockLore.add("§e§o已解鎖！");
            day14lockMeta.setCustomModelData(10076);
        } else {
            day14lockLore.add("§4§o將在本季第 14 天解鎖 （ " + ChronoUnit.DAYS.between(LocalDate.now(), plugin.getSeasonManager().getThisSeasonStartDate().plusDays(14)) + " 天后）");
            day14lockMeta.setCustomModelData(10075);
        }
        day14lockMeta.setLore(day14lockLore);
        day14lockItem.setItemMeta(day14lockMeta);

        ItemStack day21lockItem = new ItemStack(Material.PAPER);
        ItemMeta day21lockMeta = day21lockItem.getItemMeta();
        day21lockMeta.setDisplayName("§b禮包：第三周");
        List<String> day21lockLore = new ArrayList<>();
        if (plugin.getSeasonManager().getCurrentDay() >= 21) {
            day21lockLore.add("§e§o已解鎖！");
            day21lockMeta.setCustomModelData(10076);
        } else {
            day21lockLore.add("§4§o將在本季第 21 天解鎖 （ " + ChronoUnit.DAYS.between(LocalDate.now(), plugin.getSeasonManager().getThisSeasonStartDate().plusDays(21)) + " 天后）");
            day21lockMeta.setCustomModelData(10075);
        }
        day21lockMeta.setLore(day21lockLore);
        day21lockItem.setItemMeta(day21lockMeta);

        createSeasonItem(inventory);
        createFinalChestItem(player, inventory);

        for (int i : new int[]{7, 14, 21}) {
            createChestItem(player, plugin.getRankManager().getRankById("wood"), i, inventory);
            createChestItem(player, plugin.getRankManager().getRankById("iron"), i, inventory);
            createChestItem(player, plugin.getRankManager().getRankById("silver"), i, inventory);
            createChestItem(player, plugin.getRankManager().getRankById("gold"), i, inventory);
            createChestItem(player, plugin.getRankManager().getRankById("diamond"), i, inventory);
        }

        inventory.setItem(45, returnItem);
        inventory.setItem(10, day7lockItem);
        inventory.setItem(19, day14lockItem);
        inventory.setItem(28, day21lockItem);

        player.openInventory(inventory);
    }

    private void createSeasonItem(Inventory inventory) {
        ItemStack seasonItem = new ItemStack(Material.PAPER);
        ItemMeta seasonMeta = seasonItem.getItemMeta();
        seasonMeta.setCustomModelData(10035);
        seasonMeta.setDisplayName(ChatColor.YELLOW + plugin.getSeasonManager().getCurrentSeason().name());
        List<String> seasonLore = new ArrayList<>();
        seasonLore.add("§f進度："  + plugin.getSeasonManager().getDayString() + "天 §7" + plugin.getSeasonManager().getProgressPercentage());
        seasonLore.add("§a賽季結束時，積分、段位會重置。");
        seasonLore.add("§a并且，你會根據你當季最高達到的段位獲得 §d§l季末結算獎勵");
        seasonMeta.setLore(seasonLore);
        seasonItem.setItemMeta(seasonMeta);
        inventory.setItem(4, seasonItem);
    }

    private void createChestItem(Player player, Rank rank, int day, Inventory inventory) {
        if (day % 7 != 0 || day > 21) throw new IllegalArgumentException("Day must be either 7, 14 or 21");
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = new ArrayList<>();
        int startingSlot;
        String prefix = "";
        String chestDisplayName;

        switch (rank.id()) {
            case "wood" -> {
                startingSlot = 11;
                itemMeta.setCustomModelData(10175);
                chestDisplayName = "§6木 段位禮包";
            }
            case "iron" -> {
                startingSlot = 12;
                itemMeta.setCustomModelData(10176);
                chestDisplayName = "§7鐵 段位禮包";
            }
            case "silver" -> {
                startingSlot = 13;
                itemMeta.setCustomModelData(10177);
                chestDisplayName = "§f銀 段位禮包";
            }
            case "gold" -> {
                startingSlot = 14;
                itemMeta.setCustomModelData(10178);
                chestDisplayName = "§e金 段位禮包";
            }
            case "diamond" -> {
                startingSlot = 15;
                itemMeta.setCustomModelData(10179);
                chestDisplayName = "§b鑽 段位禮包";
            }
            default -> throw new IllegalArgumentException("Rank must be either wood, iron, silver, gold, diamond.");
        }

        if (plugin.getSeasonManager().getCurrentDay() >= day) {
            try {
                if (plugin.getDatabase().hasClaimedKit(player.getUniqueId(), rank.id(), String.valueOf(day))) {
                    itemMeta.setCustomModelData(10180);
                    prefix = "§c【已領取】";
                } else {
                    if (plugin.getDatabase().getRankByPlayer(player).threshold() >= rank.threshold()) {
                        prefix = "§e【可領取】";
                    } else {
                        prefix = "§4【未達到段位】";

                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error while getting player has claimed kit and player's score: " + player);
                e.printStackTrace();
            }
        } else {
            prefix = "§4【未解鎖】";
        }

        itemMeta.setDisplayName(prefix + "§e第 " + day / 7 + " 周 " + chestDisplayName);
        lore.add("§f此禮包需要至少 " + rank.threshold() + " 積分才能領取。");
        lore.add("§3§o左鍵 §7預覽");
        lore.add("§3§o右鍵 §7領取");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(startingSlot + ((day / 7 - 1) * 9), itemStack);
    }

    private void createFinalChestItem(Player player, Inventory inventory) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = new ArrayList<>();

        itemMeta.setDisplayName("§d§l季末獎勵禮包");
        itemMeta.setCustomModelData(10237);
        try {
            Rank rank = plugin.getDatabase().getLastSeasonRank(player.getUniqueId());
            if (plugin.getDatabase().hasClaimedLastSeasonFinalKit(player.getUniqueId()) || rank.id().equalsIgnoreCase("unset")) {
                lore.add("§e✧ 本季：§f" + plugin.getSeasonManager().getCurrentSeason().name() + " §e禮包");
                lore.add("§7將會在 " + plugin.getSeasonManager().getNextSeasonDate() + " 解鎖！");
                lore.add("§e此禮包内容豐富度根據你在本季最終達到的段位而定，");
                lore.add("§e所以請在這個賽季好好表現哦。");
                lore.add("§3§o左鍵 §7預覽");
            } else {
                lore.add("§a§l現可領取！");
                lore.add("§e✧上個賽季  §f" + plugin.getSeasonManager().getSeasonByNumber(plugin.getSeasonManager().getCurrentSeason().seasonNumber() - 1) + " §e的禮包");
                lore.add("§e此禮包内容豐富度根據你在上個賽季最終達到的段位而定，");
                lore.add("§e你在上個賽季最終達到了 §b" + plugin.getDatabase().getLastSeasonRank(player.getUniqueId()).name() + " §e段位");
                lore.add("§3§o右鍵 §7領取");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while getting player's has claimed last season final kit or last season rank: " + player);
            e.printStackTrace();
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(40, itemStack);
    }
}
