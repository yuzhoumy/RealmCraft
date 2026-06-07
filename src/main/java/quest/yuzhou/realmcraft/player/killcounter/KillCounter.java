package quest.yuzhou.realmcraft.player.killcounter;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import quest.yuzhou.realmcraft.RealmCraft;

import java.sql.SQLException;
import java.util.UUID;

public class KillCounter implements Listener {

    private final RealmCraft plugin;
//    private final HashMap<Player, Integer> todayKill;

    public KillCounter(RealmCraft plugin) {
        this.plugin = plugin;
//        this.todayKill = new HashMap<>();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player attacker = event.getEntity().getKiller();
        UUID victimUUID = event.getEntity().getUniqueId();
        UUID attackerUUID = attacker.getUniqueId();


        try {
            plugin.getDatabase().clearKillStreak(victimUUID);
            plugin.getDatabase().addKillCount(attackerUUID, 1);
            plugin.getDatabase().addKillStreak(attackerUUID, 1);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error while adding player's kill count victim: " + event.getEntity() + " attacker: " + attacker);
        }

//        if (!todayKill.containsKey(attacker)) {
//            todayKill.put(attacker, 1);
//        } else {
//            todayKill.replace(attacker, todayKill.get(attacker) + 1);
//        }
    }

//    public int getPlayerTodayKill(Player player) {
//        return todayKill.get(player) != null ? todayKill.get(player) : 0;
//    }
//
//    public HashMap<Player, Integer> getTodayKill() {
//        return todayKill;
//    }
}
