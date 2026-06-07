package quest.yuzhou.realmcraft.player.menu.strategiceventmenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.StrategicEvent;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

import java.util.List;

public class StrategicEventMenuHandler implements Listener {

    private final RealmCraft plugin;

    public StrategicEventMenuHandler(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(Utilities.colorize(plugin.getMenuManager().getMenu(MenuManager.MenuType.STRATEGIC_EVENT_MENU).getMenuName()))) return;

        if (event.getCurrentItem() == null ||  event.getCurrentItem().getType() == Material.AIR) return;

        if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("返回")) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
        }

        event.setCancelled(true);

        List<StrategicEvent> strategicEventList = plugin.getStrategicEventManager().getStrategicEventList();

        for (StrategicEvent strategicEvent : strategicEventList) {
            if (strategicEvent
                    .getMenuItem()
                    .getItemMeta()
                    .getDisplayName()
                    .equalsIgnoreCase(event
                            .getCurrentItem()
                            .getItemMeta()
                            .getDisplayName()
                    )
            ) {
                strategicEvent.onMenuClick((Player) event.getWhoClicked());
                event.getWhoClicked().closeInventory();
            }
        }
    }
}
