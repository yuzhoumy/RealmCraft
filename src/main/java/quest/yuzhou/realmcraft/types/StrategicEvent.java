package quest.yuzhou.realmcraft.types;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;

import java.util.Arrays;
import java.util.List;

public abstract class StrategicEvent {

    protected final RealmCraft plugin;
    private final Material displayMaterial;
    private final String displayName;
    protected String cost;
    private String[] description;


    public StrategicEvent(RealmCraft plugin, Material displayMaterial, String cost, String displayName, String[] description) {
        this.plugin = plugin;
        this.displayMaterial = displayMaterial;
        this.cost = cost;
        this.displayName = displayName;
        this.description = description;
    }

    public StrategicEvent(RealmCraft plugin, Material displayMaterial, String displayName) {
        this.plugin = plugin;
        this.displayMaterial = displayMaterial;
        this.displayName = displayName;
    }

    public ItemStack getMenuItem() {
        ItemStack item = new ItemStack(displayMaterial);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utilities.colorize("&e&l" + displayName));
        List<String> lore = new java.util.ArrayList<>(Arrays.stream(description).map((sentence) -> ChatColor.GRAY + sentence).toList());
        lore.add(0, ChatColor.GREEN + "花費：" + cost + " 等級");
        lore.add(0, "");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public abstract void onMenuClick(Player player);

    public void stop() {};

    public String getDisplayName() {
        return displayName;
    }
}
