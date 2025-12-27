package quest.yuzhou.realmcraft.player.menu.mainmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class MainMenuHandler implements Listener {

    private final RealmCraft plugin;

    public MainMenuHandler(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).getMenuName()))
            return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        event.setCancelled(true);

        Material clickedMaterial = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();
        player.playSound(player, Sound.UI_BUTTON_CLICK, 10, 2);

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        switch (clickedMaterial) {
            case PLAYER_HEAD -> {
                ItemMeta skullMeta = event.getCurrentItem().getItemMeta();
                if (skullMeta.getDisplayName().contains("Discord")) {
                    player.closeInventory();
                    player.sendMessage(plugin.prefix + Utilities.colorize("我們的Discord群組：&b https://discord.gg/b246U4B7Rg"));
                } else {
                    plugin.getMenuManager().getMenu(MenuManager.MenuType.PLAYER_MENU).open(player);
                }
            }
            case KNOWLEDGE_BOOK -> {
                player.closeInventory();
                player.sendMessage(plugin.prefix + Utilities.colorize("&d領域世界玩家手冊：&b https://realmcraftserver.gitbook.io/handbook/"));
            }
            case PAPER -> {
                if (itemName.equalsIgnoreCase(ChatColor.BLUE + "官方網站")) {
                    player.closeInventory();
                    player.sendMessage(plugin.prefix + "領域世界官方網站：尚未開放");
                } else if (itemName.equalsIgnoreCase(ChatColor.RED + "垃圾桶")) {
                    Bukkit.dispatchCommand(player, "dispose");
                } else if (itemName.equalsIgnoreCase(ChatColor.RED + "音樂：已關閉")) {
                    plugin.getMusicPlayer().unmute(player);
                    plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
                } else if (itemName.equalsIgnoreCase(ChatColor.RED + "音樂：已開啓")) {
                    plugin.getMusicPlayer().mute(player);
                    plugin.getMenuManager().getMenu(MenuManager.MenuType.MAIN_MENU).open(player);
                }
            }
            case FEATHER -> plugin.getMenuManager().getMenu(MenuManager.MenuType.TUTORIAL_MENU).open(player);
            case IRON_INGOT -> {
                if (itemName.equalsIgnoreCase(ChatColor.GOLD + "説明書"))
                    plugin.getMenuManager().getMenu(MenuManager.MenuType.MANUAL_MENU).open(player);
                else if (itemName.equalsIgnoreCase(ChatColor.DARK_PURPLE + "賽季"))
                    plugin.getMenuManager().getMenu(MenuManager.MenuType.SEASON_MENU).open(player);
            }
            case DIAMOND_SWORD -> plugin.getMenuManager().getMenu(MenuManager.MenuType.STRATEGIC_EVENT_MENU).open(player);
            case WOODEN_SWORD -> {
                player.closeInventory();
                player.sendMessage(plugin.prefix + Utilities.colorize("&d技能檢索表：&b https://realmcraftserver.gitbook.io/handbook/item/skill-refering-index"));
            }
        }
    }

}
