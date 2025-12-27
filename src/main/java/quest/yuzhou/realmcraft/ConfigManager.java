package quest.yuzhou.realmcraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final RealmCraft plugin;
    private final List<String> configList;

    public ConfigManager(RealmCraft plugin) {
        this.plugin = plugin;
        configList = new ArrayList<>();

        try {
            File[] files = plugin.getDataFolder().listFiles();
            if (files == null) {
                plugin.getLogger().severe("Data folder is null or inaccessible.");
                return;
            }
            for (File file : files) {
                if (file.getName().endsWith(".yml") && !file.getName().equalsIgnoreCase("config.yml"))
                    configList.add(file.getName());
            }
        } catch (NullPointerException e) {
            plugin.getLogger().severe("Error while loading config file names from data folder");
            e.printStackTrace();
        }
    }

    // Create default configs (add more if needed)
    public void loadAllConfigs() {
        plugin.getLogger().info("Loading all config");
        plugin.saveDefaultConfig();
        configList.forEach(this::loadConfig);
    }

    // Load or create a config file
    private void loadConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);

        plugin.getLogger().info("Loading config " + configFile.getPath());

        // 檢查文件是否存在，不存在時才從資源中複製
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
            plugin.getLogger().info("Saved " + fileName + " from resources.");
        }
    }
}
