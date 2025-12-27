package quest.yuzhou.realmcraft.misc.strategicevent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.Bounty;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.Contract;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.GateOfTheBrave;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.ResourceStorm;

import java.util.ArrayList;
import java.util.List;

public class StrategicEventManager {

    private final List<StrategicEvent> strategicEventList;

    public StrategicEventManager(RealmCraft plugin) {
        this.strategicEventList = new ArrayList<>();
        this.strategicEventList.add(new Contract(plugin));
        this.strategicEventList.add(new Bounty(plugin));
        this.strategicEventList.add(new GateOfTheBrave(plugin));
        this.strategicEventList.add(new ResourceStorm(plugin));

        for (StrategicEvent strategicEvent : strategicEventList) {
            Bukkit.getPluginManager().registerEvents((Listener) strategicEvent, plugin);
        }

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
    }
}
