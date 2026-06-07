package quest.yuzhou.realmcraft.misc.emergencypack;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyPackChestListener implements Listener {

    private final RealmCraft plugin;
    private final int price;
    private final List<Player> confirming;
    private long cooldown = 0;

    public EmergencyPackChestListener(RealmCraft plugin) {
        this.plugin = plugin;
        this.confirming = new ArrayList<>();
        this.price = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "emergency-pack.yml")).getInt("price");
    }

    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent event) {

        if (cooldown > System.currentTimeMillis()) return;
        cooldown = System.currentTimeMillis() + 2000;

        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        Location chestLocation = event.getClickedBlock().getLocation();
        if (
                chestLocation.getWorld() == plugin.mainWorld &&
                        chestLocation.getBlockX() == -51 &&
                        chestLocation.getBlockY() == 72 &&
                        chestLocation.getBlockZ() == 9
        ) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            int score = 0;
            try {
                score = plugin.getDatabase().getScore(player);
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Unexpected error occur when getting player's score: " + player);
                player.sendMessage(plugin.prefix + ChatColor.RED + "發生嚴重錯誤，請立即通知管理員");
            }

            if (score > 100) {
                player.sendMessage(plugin.prefix + ChatColor.RED + " 只有積分不超過100的玩家可以購買！");
                return;
            }

            if (!confirming.contains(player)) {
                player.sendMessage(plugin.prefix + Utilities.colorize(" &e你確定要花費 &b " + price +" &e積分購買 &c急救包 &e嗎？"));
                player.sendMessage(plugin.prefix + ChatColor.YELLOW + " 如果確定，請再點擊一次箱子");
                player.playSound(player, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 10F, 0.5F);
                confirming.add(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> confirming.remove(player), 200);
            } else {

                confirming.remove(player);

                try {
                    plugin.getDatabase().addScore(player.getUniqueId(), -price);
                } catch (SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe(" Unable to deduct player's score : " + player);
                }

                List<String> contents = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "emergency-pack.yml")).getStringList("contents");

                for (String item : contents) {
                    String[] split = item.split("-");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give " + split[0] + " " + split[1] + " " + player.getName() + " " + split[2]);
                }

                player.sendMessage(plugin.prefix + ChatColor.GREEN + " 購買急救包成功！");


            }
        }
    }

}
