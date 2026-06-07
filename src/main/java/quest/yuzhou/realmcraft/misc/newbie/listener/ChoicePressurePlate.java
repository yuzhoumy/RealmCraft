package quest.yuzhou.realmcraft.misc.newbie.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;

import java.sql.SQLException;

public class ChoicePressurePlate implements Listener {

    private final RealmCraft plugin;

    public ChoicePressurePlate(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.PHYSICAL) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        plugin.getLogger().info(block.getLocation().toString());

        if (block.getType() == Material.DARK_OAK_PRESSURE_PLATE) {
            if ( // player wants to continue newbie tutorial
                    block.getWorld() == plugin.mainWorld &&
                            block.getX() == -22 &&
                            block.getY() == -24 &&
                            block.getZ() == 41
            ) {
                player.teleport(new Location(
                        plugin.mainWorld,
                        64,
                        64,
                        -44,
                        -135,
                        0
                ));
                try {
                    plugin.getDatabase().updatePlayerHasPassedIntroStage(player.getUniqueId(), 11);
                } catch (SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe("Error while updating player's has passed intro stage : " + player);
                    player.sendMessage(plugin.prefix + ChatColor.RED + "發生了嚴重錯誤，請立即通知管理員。");
                }
                plugin.getNewbieQuestManager().forceStop(player);
                plugin.getNewbieQuestManager().start(player, 11, true);
            } else if (
                    block.getWorld() == plugin.mainWorld &&
                            block.getX() == -36 &&
                            block.getY() == -24 &&
                            block.getZ() == 41
            ) {

                try {
                    plugin.getDatabase().updatePlayerHasPassedIntroStage(player.getUniqueId(), 19);
                } catch (SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe("Error while updating player's has passed intro stage : " + player);
                    player.sendMessage(plugin.prefix + ChatColor.RED + "發生了嚴重錯誤，請立即通知管理員。");
                }
                plugin.getNewbieQuestManager().briefExplanation(player);
            }
        }
    }
}
