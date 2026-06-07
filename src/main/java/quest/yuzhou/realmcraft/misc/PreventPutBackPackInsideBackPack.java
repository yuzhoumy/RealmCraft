package quest.yuzhou.realmcraft.misc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PreventPutBackPackInsideBackPack implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        // Determine which item is being moved
        ItemStack itemBeingMoved = null;

        if (event.isShiftClick() && event.getCurrentItem() != null) {
            // Shift-clicking moves current item to other inventory
            itemBeingMoved = event.getCurrentItem();
        } else if (event.getCursor() != null && !event.getCursor().getType().isAir()) {
            // Regular click with item on cursor
            itemBeingMoved = event.getCursor();
        }

        if (itemBeingMoved == null) return;
        if (!itemBeingMoved.hasItemMeta()) return;
        if (!itemBeingMoved.getItemMeta().hasDisplayName()) return;

        String itemName = itemBeingMoved.getItemMeta().getDisplayName();

        // Check both traditional and simplified Chinese
        if (!itemName.contains("隨身背包") && !itemName.contains("随身背包")) return;

        // Check if destination inventory is a backpack
        String viewTitle = event.getView().getTitle();

        // Check both traditional and simplified Chinese in title
        if (viewTitle != null && (viewTitle.contains("隨身背包") || viewTitle.contains("随身背包"))) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你不能將背包放入背包內！");
            Bukkit.getLogger().info("Prevented backpack from being placed in backpack!");
        }
    }
}