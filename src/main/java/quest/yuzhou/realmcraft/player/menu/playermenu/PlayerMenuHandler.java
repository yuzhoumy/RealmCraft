package quest.yuzhou.realmcraft.player.menu.playermenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class PlayerMenuHandler implements Listener {

    private final RealmCraft plugin;

    public PlayerMenuHandler(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(Utilities.colorize(plugin.getMenuManager().getMenu(MenuManager.MenuType.PLAYER_MENU).getMenuName()))) return;

        if (event.getCurrentItem() == null ||  event.getCurrentItem().getType() == Material.AIR) return;

        event.setCancelled(true);

        if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("返回")) {
                Player player = (Player) event.getWhoClicked();
                plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
            }
        }
    }

}
