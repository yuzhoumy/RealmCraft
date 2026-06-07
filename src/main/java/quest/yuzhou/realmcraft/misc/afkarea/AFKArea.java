package quest.yuzhou.realmcraft.misc.afkarea;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;

import java.io.File;

public class AFKArea {

    int minX;
    int maxX;
    int minY;
    int maxY;
    int minZ;
    int maxZ;
    private final World world;
    private final int reward;
    private final BukkitTask task;

    public AFKArea(RealmCraft plugin) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "afk-area.yml"));
        String worldString = fileConfiguration.getString("world");
        world = worldString == null ? plugin.mainWorld : Bukkit.getWorld(worldString);

        String[] vertex1 = fileConfiguration.getString("vertex1").split(",");
        String[] vertex2 = fileConfiguration.getString("vertex2").split(",");

        minX = Math.min(Integer.parseInt(vertex1[0]), Integer.parseInt(vertex2[0]));
        maxX = Math.max(Integer.parseInt(vertex1[0]), Integer.parseInt(vertex2[0]));
        minY = Math.min(Integer.parseInt(vertex1[1]), Integer.parseInt(vertex2[1]));
        maxY = Math.max(Integer.parseInt(vertex1[1]), Integer.parseInt(vertex2[1]));
        minZ = Math.min(Integer.parseInt(vertex1[2]), Integer.parseInt(vertex2[2]));
        maxZ = Math.max(Integer.parseInt(vertex1[2]), Integer.parseInt(vertex2[2]));

        reward = fileConfiguration.getInt("reward");

        task = new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : world.getPlayers()) {
                    Location location = player.getLocation();
                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();

                    if (
                            x >= minX && x <= maxX &&
                            y >= minY && y <= maxY &&
                            z >= minZ && z <= maxZ
                    ) {
                        player.giveExp(reward);
                        player.sendMessage(plugin.prefix + ChatColor.GREEN + "你已獲得 " + reward + " 經驗值");
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1200);
    }

    public void stop() {
        task.cancel();
    }
}
