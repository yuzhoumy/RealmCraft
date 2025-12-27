package quest.yuzhou.realmcraft.player.menu.tutorialmenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

import java.sql.SQLException;

public class TutorialMenuHandler implements Listener {

    private final RealmCraft plugin;

    public TutorialMenuHandler(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(Utilities.colorize(plugin.getMenuManager().getMenu(MenuManager.MenuType.TUTORIAL_MENU).getMenuName()))) return;

        if (event.getCurrentItem() == null ||  event.getCurrentItem().getType() == Material.AIR) return;

        event.setCancelled(true);

        ItemMeta meta = event.getCurrentItem().getItemMeta();
        Player player = (Player) event.getWhoClicked();
        
        if (meta.getDisplayName().equalsIgnoreCase("返回")) {
            player.closeInventory();
            plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
        }

        if (meta.getDisplayName().equalsIgnoreCase("跳過當前教學")) {
            player.closeInventory();
            int hasPassedIntroStage = 0;
            try {
                hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(player.getUniqueId());
                plugin.getDatabase().updatePlayerHasPassedIntroStage(player.getUniqueId(), hasPassedIntroStage + 1);
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Error while adding player's has passed intro stage : " + player);
                player.sendMessage(plugin.prefix + ChatColor.RED + "發生嚴重錯誤，請立即通知管理員");
            }
            plugin.getNewbieQuestManager().forceStop(player);
            plugin.getNewbieQuestManager().start(player, hasPassedIntroStage + 1, true);
        }
        
        if (meta.getDisplayName().contains("【已完成】") || meta.getDisplayName().contains("【進行中】")) {
            plugin.getNewbieQuestManager().getNewbieQuests().get(event.getSlot()).fastSpeak(player);
            player.closeInventory();
        }
    }
}
