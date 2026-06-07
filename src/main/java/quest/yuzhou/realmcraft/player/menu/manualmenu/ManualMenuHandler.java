package quest.yuzhou.realmcraft.player.menu.manualmenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class ManualMenuHandler implements Listener {

    private final RealmCraft plugin;

    public ManualMenuHandler(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(plugin.getMenuManager().getMenu(MenuManager.MenuType.MANUAL_MENU).getMenuName())) return;

        if (event.getCurrentItem() == null ||  event.getCurrentItem().getType() == Material.AIR) return;

        event.setCancelled(true);

        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("返回")) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
        }

        if (event.getCurrentItem().getType() == Material.KNOWLEDGE_BOOK) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            ManualMenu manualMenu = (ManualMenu) plugin.getMenuManager().getMenu(MenuManager.MenuType.MANUAL_MENU);

            manualMenu.getTutorialBooks().forEach((name, description) -> {

                String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                String bookName = ChatColor.stripColor(Utilities.colorize(name));
                if (itemName.equalsIgnoreCase(bookName)) {
                    player.playSound(player, Sound.ENTITY_ARROW_SHOOT, 10, 1);
                    player.sendMessage(Utilities.colorize("&8= - = - = &5&k&l囯 &b" + name + " &5&k&l囯 &8= - = - ="));
                    for (int i = 0; i < description.length; i++) {
                        player.sendMessage(Utilities.colorize("&e" + (i + 1) + ". &f" + description[i]));
                        player.sendMessage("");
                    }
                }

            });
        }
    }

}
