package quest.yuzhou.realmcraft.misc;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import quest.yuzhou.realmcraft.RealmCraft;

public class PreventPlayerBreakHanging implements Listener {

    private final RealmCraft plugin;

    public PreventPlayerBreakHanging(RealmCraft plugin) {
        this.plugin = plugin;
    }

    // Prevent breaking hanging entities (ItemFrame, Painting, etc.)
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player player) {
            if ((player.getWorld() == plugin.mainWorld || player.getWorld() == plugin.fieldWorld) && !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    // Prevent placing hanging entities (ItemFrame, Painting, etc.)
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if ((player.getWorld() == plugin.mainWorld || player.getWorld() == plugin.fieldWorld) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    // Prevent placing ArmorStands
    @EventHandler
    public void onEntityPlace(EntityPlaceEvent event) {
        Player player = event.getPlayer();
        if (event.getEntityType() == EntityType.ARMOR_STAND) {
            if ((player.getWorld() == plugin.mainWorld || player.getWorld() == plugin.fieldWorld) && !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    // Prevent damaging ArmorStands and hanging entities
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();

        if (damaged instanceof ArmorStand || damaged instanceof Hanging) {
            if (event.getDamager() instanceof Player player) {
                if ((player.getWorld() == plugin.mainWorld || player.getWorld() == plugin.fieldWorld) && !player.isOp()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // Prevent interacting with ArmorStands (manipulating armor/items)
    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        if ((player.getWorld() == plugin.mainWorld || player.getWorld() == plugin.fieldWorld) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    // Prevent right-clicking hanging entities to rotate ItemFrames
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (entity instanceof Hanging) {
            if ((player.getWorld() == plugin.mainWorld || player.getWorld() == plugin.fieldWorld) && !player.isOp()) {
                event.setCancelled(true);
            }
        }
    }
}