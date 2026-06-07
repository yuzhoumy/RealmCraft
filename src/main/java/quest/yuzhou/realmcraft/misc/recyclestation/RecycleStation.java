package quest.yuzhou.realmcraft.misc.recyclestation;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecycleStation implements Listener {

    private final RealmCraft plugin;
    Location recycleStationLocation;
    String world;
    int x;
    int y;
    int z;
    HashMap<String, Integer> prices;
    List<String> recycleTypes;
    boolean inUse;
    Location beforeLocation;

    public RecycleStation(RealmCraft plugin) {
        this.plugin = plugin;

        String[] recycleStationLocationString = plugin.getConfig().getString("recycle-station-location").split(",");
        recycleStationLocation = new Location(
                Bukkit.getWorld(recycleStationLocationString[0]),
                Double.parseDouble(recycleStationLocationString[1]),
                Double.parseDouble(recycleStationLocationString[2]),
                Double.parseDouble(recycleStationLocationString[3])
        );

        String[] leverLocation = plugin.getConfig().getString("recycle-station-lever-location").split(",");
        world = leverLocation[0];
        x = Integer.parseInt(leverLocation[1]);
        y = Integer.parseInt(leverLocation[2]);
        z = Integer.parseInt(leverLocation[3]);

        prices = new HashMap<>();
        for (String s : plugin.getConfig().getStringList("recycle-station-prices")) {
            String[] split = s.split("-");
            if (split.length != 2) {
                throw new IllegalStateException("Recycle station prices not configured correctly!");
            }
            prices.put(split[0], Integer.parseInt(split[1]));
        }

        recycleTypes = plugin.getConfig().getStringList("recycle-types");
        beforeLocation = null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.LEVER) return;

        Location blockLocation = event.getClickedBlock().getLocation();

        if (
                blockLocation.getBlockX() == x &&
                blockLocation.getBlockY() == y &&
                blockLocation.getBlockZ() == z &&
                blockLocation.getWorld().getName().equalsIgnoreCase(world)
        ) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            if (inUse) {
                player.sendMessage(plugin.prefix + ChatColor.RED + " 目前正有人使用回收站！");
                return;
            }
            inUse = true;
            beforeLocation = player.getLocation();

            Inventory inventory = Bukkit.createInventory(player, 36, ChatColor.RED + "回收站：請放入要回收的武器/裝備");
            player.teleport(recycleStationLocation);
            player.openInventory(inventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.RED + "回收站：請放入要回收的武器/裝備")) {

            if (!inUse) return;

            int itemCount = 0;
            int totalPrice = 0;

            List<ItemStack> recycleItems = new ArrayList<>();
            Player player = (Player) event.getPlayer();
            player.playSound(player, Sound.ENTITY_RABBIT_DEATH, 10F, 0.5F);

            for (ItemStack itemStack : event.getInventory().getContents()) {
                if (itemStack == null) continue;

                boolean isRecyclable = false;
                // Check if this item is recyclable

                if (itemStack.hasItemMeta()) {
                    ItemMeta meta = itemStack.getItemMeta();
                    if (!meta.hasLore()) continue;
                    List<String> lore = meta.getLore();
                    for (String sentence : lore) {
                        if (isRecyclable) break;
                        isRecyclable = sentence.contains("類型") && recycleTypes.stream().anyMatch(sentence::contains);
                    }
                }

                if (!isRecyclable) {
                    player.getInventory().addItem(itemStack); // Return non-recyclable items
                    continue;
                }

                // Add to recycle list and count
                recycleItems.add(itemStack);
                itemCount += itemStack.getAmount();

                // Calculate price based on level
                for (String line : itemStack.getItemMeta().getLore()) {
                    for (Map.Entry<String, Integer> entry : prices.entrySet()) {
                        if (line.contains(Utilities.colorize(entry.getKey()))) {
                            totalPrice += entry.getValue();
                            break; // Stop after first match per line
                        }
                    }
                }
            }

            for (ItemStack recycleItem : recycleItems) {
                player.sendMessage(plugin.prefix + ChatColor.GREEN + "成功回收：" + recycleItem.getItemMeta().getDisplayName());
            }

            if (itemCount == 0) {
                player.sendMessage(plugin.prefix + ChatColor.RED + "沒有任何裝備能被回收！");
            } else {
                try {
                    plugin.getDatabase().addScore(player.getUniqueId(), totalPrice);
                } catch (SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe("Error while adding player's score: " + player);
                    player.sendMessage(plugin.prefix + ChatColor.RED + "發生了致命錯誤，請立即通報管理員！");
                }
                player.sendMessage(plugin.prefix + ChatColor.BLUE + "成功回收 " + itemCount + " 個物品");
            }
            inUse = false;
            player.teleport(beforeLocation);
            beforeLocation = null;
        }
    }
}
