package quest.yuzhou.realmcraft.misc.kit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;

public class KitChestListener implements Listener {

    private final RealmCraft plugin;

    public KitChestListener(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        Location chestLocation = event.getClickedBlock().getLocation();
        if (
            chestLocation.getWorld() == plugin.mainWorld &&
            chestLocation.getBlockX() == -10 &&
            chestLocation.getBlockY() == 79 &&
            chestLocation.getBlockZ() == 78
        ) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            Bukkit.dispatchCommand(player, "kit");
        }
    }

}
