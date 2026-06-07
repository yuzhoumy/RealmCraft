package quest.yuzhou.realmcraft.player.menu.seasonmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.MenuManager;
import quest.yuzhou.realmcraft.types.Rank;
import quest.yuzhou.realmcraft.types.Season;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SeasonMenuHandler implements Listener {

    private final RealmCraft plugin;
    private final HashMap<Season, HashMap<Integer, HashMap<Rank, List<ItemStack>>>> rewardItemMap;

    public SeasonMenuHandler(RealmCraft plugin) {
        this.plugin = plugin;
        this.rewardItemMap = plugin.getSeasonManager().getRewardItemMap();
    }


    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(plugin.getMenuManager().getMenu(MenuManager.MenuType.SEASON_MENU).getMenuName())) {

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (itemName.equalsIgnoreCase("返回")) {
                plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
                return;
            }

            if (itemName.equalsIgnoreCase("§d§l季末獎勵禮包")) {
                if (event.isRightClick()) {
                    boolean claimed = false;
                    try {
                        claimed = plugin.getDatabase().hasClaimedLastSeasonFinalKit(player.getUniqueId());
                    } catch (SQLException e) {
                        player.sendMessage(plugin.prefix + ChatColor.RED + "發生了嚴重錯誤，請立即通報管理員。");
                        e.printStackTrace();
                    }
                    if (claimed) return;

                    String lastSeasonRankId = "unset";
                    try {
                        lastSeasonRankId = plugin.getDatabase().getLastSeasonRank(player.getUniqueId()).id();
                    } catch (SQLException e) {
                        player.sendMessage(plugin.prefix + ChatColor.RED + "發生了嚴重錯誤，請立即通報管理員。");
                        e.printStackTrace();
                    }
                    Rank rank = plugin.getRankManager().getRankById(lastSeasonRankId);
                    if (lastSeasonRankId.equalsIgnoreCase("unset")) {
                        player.sendMessage(plugin.prefix + ChatColor.RED + "你在上個賽季沒有達到任何段位！");
                        player.playSound(player, Sound.ENTITY_CHICKEN_HURT, 10, 1);
                        player.closeInventory();
                        return;
                    }
                    List<ItemStack> rewards = rewardItemMap
                            .get(plugin.getSeasonManager().getPreviousSeason())
                            .get(0)
                            .get(rank);
                    System.out.println(rewards);
                    // check if player's inventory fit items
                    if (!Utilities.hasInventorySpace(player, rewards)) {
                        player.sendMessage(plugin.prefix + "§c你的背包空間不足！清空出一點空間！");
                        return;
                    }

                    try {
                        plugin.getDatabase().setLastSeasonFinalKitClaimed(player.getUniqueId(), true);
                    } catch (SQLException e) {
                        player.sendMessage(plugin.prefix + ChatColor.RED + "發生了嚴重錯誤，請立即通報管理員。");
                        e.printStackTrace();
                    }

                    player.closeInventory();
                    player.playSound(player, "realmcraft:creepy", 10, 1);
                    Bukkit.broadcastMessage(plugin.prefix + "§a" + player.getName() + " §d正在開啓上個賽季的季末獎勵……");
                    Bukkit.broadcastMessage(plugin.prefix + "§a" + player.getName() + " §d在上個賽季中達到了 " + rank.name() + " 段位，獲得以下獎勵：");
                    List<String> itemNameList = new ArrayList<>();
                    for (ItemStack itemStack : rewards) {
                        itemNameList.add(itemStack.getItemMeta().getDisplayName() + " §ax " + itemStack.getAmount());
                    }
                    Bukkit.broadcastMessage(plugin.prefix + String.join(" , ", itemNameList));
                } else if (event.isLeftClick()) { // preview final chest
                    openChoicePage(player);
                }
            }

            if (itemName.contains("段位禮包")) {
                int day = Integer.parseInt(itemName.split(" 周 ")[0].split("§e第 ")[1]) * 7;

                String chestRank = itemName.split(" 周 ")[1];
                String rankId = "";
                switch (chestRank) {
                    case "§6木 段位禮包" -> rankId = "wood";
                    case "§7鐵 段位禮包" -> rankId = "iron";
                    case "§f銀 段位禮包" -> rankId = "silver";
                    case "§e金 段位禮包" -> rankId = "gold";
                    case "§b鑽 段位禮包" -> rankId = "diamond";
                }
                Rank rank = plugin.getRankManager().getRankById(rankId);

                List<ItemStack> rewards = rewardItemMap
                        .get(plugin.getSeasonManager().getCurrentSeason())
                        .get(day)
                        .get(rank);

                if (event.isLeftClick())
                    openPreviewPage(player, rewards);
                else if (event.isRightClick() && itemName.contains("§e【可領取】")) {
                    player.closeInventory();
                    if (Utilities.hasInventorySpace(player, rewards)) { // player's inventory can't fit all items
                        try {
                            plugin.getDatabase().setKitClaimed(player.getUniqueId(), rank.id(), day, true);
                        } catch (SQLException e) {
                            plugin.getLogger().severe("Error while updating player claimed kit: " + player);
                            e.printStackTrace();
                        }

                        // player getting rewards
                        player.playSound(player, "realmcraft:chest_open", 10, 1);
                        player.sendMessage(plugin.prefix + "§e正在開啓 §f" + rank.name() + " 段位的 §e第 " + day / 7 + " §e周的禮包 ……");
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.broadcastMessage(plugin.prefix + "§b" + player.getName() + " §e開啓了 §f" + rank.name() + " 段位的 §e第 " + day / 7 + " §e周的禮包。"), 20 * 4); // 4 sec
                        rewards.forEach(item -> player.getInventory().addItem(item));
                    } else {
                        player.sendMessage(plugin.prefix + "§c你的背包空間不足！清空出一點空間！");
                    }
                }
            }
        } else if (title.equalsIgnoreCase("§5預覽")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            event.setCancelled(true);

            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("返回")) {
                plugin.getMenuManager().getMenu(MenuManager.MenuType.SEASON_MENU).open((Player) event.getWhoClicked());
            }
        } else if (title.equalsIgnoreCase("§d請根據你要查看的段位選擇寶箱。")) {

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            event.setCancelled(true);

            String rankName = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1];
            for (Rank rank : plugin.getRankManager().getRankList()) {
                if (rank.name().equalsIgnoreCase(rankName)) {
                    List<ItemStack> rewards = rewardItemMap
                            .get(plugin.getSeasonManager().getCurrentSeason())
                            .get(0)
                            .get(rank);
                    openPreviewPage((Player) event.getWhoClicked(), rewards);
                    return;
                }
            }
        }
    }

    private void openChoicePage(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, "§d請根據你要查看的段位選擇寶箱。");
        ItemStack woodItem = new ItemStack(Material.PAPER);
        ItemMeta woodMeta = woodItem.getItemMeta();
        woodMeta.setDisplayName("§6 木 段位 §f季末獎勵禮包");
        woodMeta.setCustomModelData(10175);
        woodItem.setItemMeta(woodMeta);

        ItemStack ironItem = new ItemStack(Material.PAPER);
        ItemMeta ironMeta = ironItem.getItemMeta();
        ironMeta.setDisplayName("§7 鐵 段位 §f季末獎勵禮包");
        ironMeta.setCustomModelData(10176);
        ironItem.setItemMeta(ironMeta);

        ItemStack silverItem = new ItemStack(Material.PAPER);
        ItemMeta silverMeta = silverItem.getItemMeta();
        silverMeta.setDisplayName("§f 銀 段位 §f季末獎勵禮包");
        silverMeta.setCustomModelData(10177);
        silverItem.setItemMeta(silverMeta);

        ItemStack goldItem = new ItemStack(Material.PAPER);
        ItemMeta goldMeta = goldItem.getItemMeta();
        goldMeta.setDisplayName("§e 金 段位 §f季末獎勵禮包");
        goldMeta.setCustomModelData(10178);
        goldItem.setItemMeta(goldMeta);

        ItemStack diamondItem = new ItemStack(Material.PAPER);
        ItemMeta diamondMeta = diamondItem.getItemMeta();
        diamondMeta.setDisplayName("§b 鑽 段位 §f季末獎勵禮包");
        diamondMeta.setCustomModelData(10179);
        diamondItem.setItemMeta(diamondMeta);

        inventory.setItem(0, woodItem);
        inventory.setItem(1, ironItem);
        inventory.setItem(2, silverItem);
        inventory.setItem(3, goldItem);
        inventory.setItem(4, diamondItem);
        player.openInventory(inventory);
    }

    private void openPreviewPage(Player player, List<ItemStack> rewards) {
        Inventory inventory = Bukkit.createInventory(player, 54, "§5預覽");
        for (int i = 0; i < rewards.size(); i++) {
            inventory.setItem(i, rewards.get(i));
        }
        ItemStack returnItem = new ItemStack(Material.PAPER);
        ItemMeta returnMeta = returnItem.getItemMeta();
        returnMeta.setCustomModelData(10003);
        returnMeta.setDisplayName("返回");
        returnItem.setItemMeta(returnMeta);
        inventory.setItem(53, returnItem);
        player.openInventory(inventory);
    }

}
