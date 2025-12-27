package quest.yuzhou.realmcraft.player.menu;

import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;

public abstract class AbstractMenu {

    protected final RealmCraft plugin;
    protected String menuName;

    public AbstractMenu(RealmCraft plugin) {
        this.plugin = plugin;
    }

    public abstract void open(Player player);
    public String getMenuName() {
        return menuName;
    }
}
