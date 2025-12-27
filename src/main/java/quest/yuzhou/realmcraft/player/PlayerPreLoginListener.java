package quest.yuzhou.realmcraft.player;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import quest.yuzhou.realmcraft.RealmCraft;

public class PlayerPreLoginListener implements Listener {

    private final RealmCraft plugin;

    public PlayerPreLoginListener(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getRestarter().isRefreshingResourcePoint()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.YELLOW + "資源點刷新中，請耐心等待數分鐘。");
        }
    }
}
