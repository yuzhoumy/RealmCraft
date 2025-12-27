package quest.yuzhou.realmcraft.misc.newbie.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import quest.yuzhou.realmcraft.RealmCraft;

import java.sql.SQLException;

public class NewbieStartTutorial implements Listener {

    private final RealmCraft plugin;

    public NewbieStartTutorial(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerStep(PlayerMoveEvent event) {

        Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);

        if (block.getType() == Material.OXIDIZED_COPPER) {
            if (plugin
                    .getNewbieQuestManager()
                    .getPlayerRunningQuest(event.getPlayer()) != null
            ) return;

            if (
                    block.getWorld() == plugin.fieldWorld &&
                    block.getX() == -463 &&
                    block.getY() == -61 &&
                    block.getZ() == 703
            ) {
                Player player = event.getPlayer();

                try {
                    plugin.getDatabase().updatePlayerHasPassedIntroStage(player.getUniqueId(), 1);
                } catch (SQLException e) {
                    plugin.getLogger().severe("Error while updating player's has passed intro stage");
                    e.printStackTrace();
                }

                plugin.getNewbieQuestManager().start(player, 1, true);
                player.teleport(new Location(Bukkit.getWorld("plots"), -411, 11, -122));
                player.sendMessage(plugin.prefix + "新手教學已開始。");
            }
        }
    }
}
