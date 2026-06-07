package quest.yuzhou.realmcraft.misc.strategicevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.Bounty;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.Contract;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.GateOfTheBrave;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.ResourceStorm;
import quest.yuzhou.realmcraft.types.ServerExecutable;
import quest.yuzhou.realmcraft.types.StrategicEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StrategicEventManager {

    private final List<StrategicEvent> strategicEventList;
    private final BukkitTask task;

    public StrategicEventManager(RealmCraft plugin) {
        this.strategicEventList = new ArrayList<>();
        this.strategicEventList.add(new Contract(plugin));
        this.strategicEventList.add(new Bounty(plugin));
        this.strategicEventList.add(new GateOfTheBrave(plugin));
        this.strategicEventList.add(new ResourceStorm(plugin));

        for (StrategicEvent strategicEvent : strategicEventList) {
            Bukkit.getPluginManager().registerEvents((Listener) strategicEvent, plugin);
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (Bukkit.getOnlinePlayers().size() < 5) return;

            int i = new Random().nextInt(strategicEventList.size());
            while (i > 0) {
                if (strategicEventList.get(i) instanceof ServerExecutable) {
                    plugin.broadcast(plugin.prefix + ChatColor.RED + "檢測到世界逐漸不穩定！這可能會引起一些風波……");
                    ((ServerExecutable) strategicEventList.get(i)).serverExecute();
                    i = -1;
                } else {
                    i = new Random().nextInt(strategicEventList.size());
                }
            }
        }, 20 * 60 * 5, 20 * 50 * 45); // delay 5 min, period 45 min
    }

    public List<StrategicEvent> getStrategicEventList() {
        return strategicEventList;
    }

    public StrategicEvent getStrategicEvent(String name) {
        for (StrategicEvent strategicEvent : strategicEventList) {
            if (strategicEvent.getDisplayName().equalsIgnoreCase(name)) {
                return strategicEvent;
            }
        }
        return null;
    }

    public void stop() {
        strategicEventList.forEach(StrategicEvent::stop);
        task.cancel();
    }
}
