package quest.yuzhou.realmcraft.player.menu.strategiceventmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.strategicevent.StrategicEvent;
import quest.yuzhou.realmcraft.player.menu.AbstractMenu;

import java.util.List;

public class StrategicEventMenu extends AbstractMenu {

    public StrategicEventMenu(RealmCraft plugin) {
        super(plugin);

        String title = plugin.getConfig().getString("strategic-event-menu-name");
        if (title == null) {
            plugin.getLogger().severe("Strategic event menu name not found in config.yml");
            title = "&c戰略事件";
        }
        menuName = title;
    }

    @Override
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Utilities.colorize(menuName));

        ItemStack returnItem = new ItemStack(Material.PAPER);
        ItemMeta returnMeta = returnItem.getItemMeta();
        returnMeta.setCustomModelData(10071);
        returnMeta.setDisplayName("返回");
        returnItem.setItemMeta(returnMeta);
        inventory.setItem(18, returnItem);

        List<StrategicEvent> strategicEventList = plugin.getStrategicEventManager().getStrategicEventList();
        for (int i = 0; i < strategicEventList.size(); i++) {
            ItemStack itemStack = strategicEventList.get(i).getMenuItem();
            inventory.setItem(i, itemStack);
        }
        player.openInventory(inventory);
    }
}
