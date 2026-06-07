package quest.yuzhou.realmcraft.misc.instance;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.Instance;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InstanceManager {

    RealmCraft plugin;
    Map<String, Instance> instances;

    public InstanceManager(RealmCraft plugin) {
        this.plugin = plugin;
        this.instances = loadInstances();
        instances.forEach((s, instance) -> Bukkit.getPluginManager().registerEvents(instance, plugin));
    }

    private Map<String, Instance> loadInstances() {
        ConfigurationSection instancesSection = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "instance.yml")).getConfigurationSection("instances");
        Map<String, Instance> result = new HashMap<>();

        for (String key : instancesSection.getKeys(false)) {
            ConfigurationSection thisInstance = instancesSection.getConfigurationSection(key);
            String displayName = Utilities.colorize(thisInstance.getString("display-name"));
            String bossType = thisInstance.getString("boss-type");
            int maxBroadcastDistance = thisInstance.getInt("max-broadcast-distance");
            int waveAmount = thisInstance.getInt("wave-amount");
            int maxWaveTime = thisInstance.getInt("max-wave-time");
            int intervalBetweenWaves = thisInstance.getInt("interval-between-waves");
            int cooldown = thisInstance.getInt("cooldown");
            int prize = thisInstance.getInt("prize");
            int penalty = thisInstance.getInt("penalty");
            Location buttonLocation = loadLocation(thisInstance.getString("button-location"));
            Location bossSpawnPoint = loadLocation(thisInstance.getString("boss-spawn-point"));

            result.put(key, new Instance(plugin, key, displayName, bossType, maxBroadcastDistance,  waveAmount, maxWaveTime, intervalBetweenWaves, cooldown, prize, penalty, buttonLocation, bossSpawnPoint));

        }
        return result;
    }

    private Location loadLocation(String configString) {
        String[] strings = configString.split(",");
        World world = Bukkit.getWorld(strings[0]);
        if (world == null) {
            plugin.getLogger().severe("World '" + strings[0] + "' is null, check instance.yml");
        }
        return new Location(world, Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));
    }

    public Map<String, Instance> getInstances() {
        return instances;
    }
}
