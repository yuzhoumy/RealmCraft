package quest.yuzhou.realmcraft.player;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.resourcepoint.types.ResourceStormEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Rewards implements Listener {

    private final RealmCraft plugin;
    private final int rewardScoreConstant;
    private final List<Location> resourceStormLocations; // 50 radius within resource point, 15 min within event, add more 30 point

    public Rewards(RealmCraft plugin) {
        this.plugin = plugin;
        this.rewardScoreConstant = plugin.getConfig().getInt("reward-score-constant");
        this.resourceStormLocations = new ArrayList<>();
    }

    @EventHandler
    public void onResourceStorm(ResourceStormEvent event) {
        resourceStormLocations.addAll(event.getResourcePointLocations());
        Bukkit.getScheduler().runTaskLater(plugin, () -> resourceStormLocations.removeAll(event.getResourcePointLocations()), 20 * 60 * 15); //15 min
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        int victimScore = 0;
        int killerScore = 0;

        try {
            victimScore = plugin.getDatabase().getScore(victim);
            killerScore = plugin.getDatabase().getScore(killer);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error while getting victim or killer's score: " + victim + " / " + killer);
        }

        int reward = (int) Math.round(rewardScoreConstant * (1 - (1 / (1 + Math.pow(10, (double) ((victimScore - killerScore)) / 400)))));
        int penalty = (int) Math.round(rewardScoreConstant * (-(1 / (1 + Math.pow(10, (double) ((killerScore - victimScore)) / 400)))));

        if (killer.getWorld().equals(plugin.mainWorld))
            if (resourceStormLocations.stream().anyMatch(location -> killer.getLocation().distance(location) <= 50))
                reward += 30;

        try {
            plugin.getDatabase().addScore(victim.getUniqueId(), penalty);
            plugin.getDatabase().addScore(killer.getUniqueId(), reward);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error while adding victim or killer's point: " + victim + " / " + killer);
        }

        victim.sendMessage(plugin.prefix + Utilities.colorize(" &c你被 &e" + killer.getName() + " &c殺死了！你失去了 &b" + -penalty + " &c積分！"));
        killer.sendMessage(plugin.prefix + Utilities.colorize( " &c你成功擊殺了 &e" + victim.getName() + " &c！你得到了 &b" + reward + " &c積分！"));

    }

}
