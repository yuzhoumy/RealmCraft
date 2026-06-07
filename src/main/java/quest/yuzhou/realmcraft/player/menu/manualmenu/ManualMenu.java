package quest.yuzhou.realmcraft.player.menu.manualmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.AbstractMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualMenu extends AbstractMenu {

    private final Map<String, String[]> tutorialBooks = new HashMap<>();

    public ManualMenu(RealmCraft plugin) {
        super(plugin);

        String title = plugin.getConfig().getString("manual-menu-name");
        if (title == null) {
            plugin.getLogger().severe("Manual menu name in config.yml not found.");
            title = "&e説明書";
        }
        menuName = Utilities.colorize(title);

        File tutorialBookConfigFile = new File(plugin.getDataFolder() , "tutorial-book.yml");
        if (!tutorialBookConfigFile.exists()) {
            plugin.getLogger().severe("tutorial-books.yml not found at " + tutorialBookConfigFile.getAbsolutePath());
        }
        FileConfiguration tutorialBookConfig = YamlConfiguration.loadConfiguration(tutorialBookConfigFile);
        for (String key : tutorialBookConfig.getKeys(false)) {
            ConfigurationSection configurationSection = tutorialBookConfig.getConfigurationSection(key);
            try {
                tutorialBooks.put(configurationSection.getString("title"),
                        configurationSection.getStringList("description").toArray(new String[0])
                );
            } catch (NullPointerException e) {
                plugin.getLogger().severe("Corrupted config format in tutorial-book.yml. Key: " + key);
            }
        }
    }

    @Override
    public void open(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, menuName);

        ItemStack returnItem = new ItemStack(Material.PAPER);
        ItemMeta returnMeta = returnItem.getItemMeta();
        returnMeta.setCustomModelData(10071);
        returnMeta.setDisplayName("返回");
        returnItem.setItemMeta(returnMeta);
        inventory.setItem(18, returnItem);

        final int[] tutorialBooksStartSlot = {0};
        tutorialBooks.forEach((name, description) -> {

            ItemStack bookItem = new ItemStack(Material.KNOWLEDGE_BOOK);
            ItemMeta bookMeta = bookItem.getItemMeta();
            bookMeta.setDisplayName(Utilities.colorize(name));
            List<String> bookLore = new ArrayList<>();
            bookLore.add(ChatColor.GRAY + "點我查看");
            bookMeta.setLore(bookLore);
            bookItem.setItemMeta(bookMeta);

            inventory.setItem(tutorialBooksStartSlot[0], bookItem);
            tutorialBooksStartSlot[0]++;

        });

        player.openInventory(inventory);
    }

    public Map<String, String[]> getTutorialBooks() {
        return tutorialBooks;
    }
}
