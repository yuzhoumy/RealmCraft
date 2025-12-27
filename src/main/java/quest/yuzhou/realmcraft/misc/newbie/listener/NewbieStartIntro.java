package quest.yuzhou.realmcraft.misc.newbie.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;

public class NewbieStartIntro implements Listener {

    private final RealmCraft plugin;

    public NewbieStartIntro(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.PHYSICAL) return;

        Block block = event.getClickedBlock();

        if (block.getType() == Material.DARK_OAK_PRESSURE_PLATE) {
            if (
                block.getWorld() == plugin.fieldWorld &&
                block.getX() == -500 &&
                block.getY() == -54 &&
                block.getZ() == 841
            ) {
                plugin.getNewbieIntroduction().run(event.getPlayer());
            }
        }
    }

}
