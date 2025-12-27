package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class Quest_3 extends NewbieQuest {

    public Quest_3(RealmCraft plugin, int number) {
        super(
            plugin,
            number,
            "領取每日補給包",
            new String[]{
                    "每日補給包是政府發放的，",
                    "讓拾荒者們不會餓死。",
                    "除了去到城堡拿之外，也可以输入",
                    "/kit",
            },
            new String[]{
                    "往前進入主城之後，接下來，沿著你正後方的路徑走（不要進入傳送門！），去到城堡（在X=-21 Z=28）領取每日補給包。",
                    "這是一個政府對拾荒者們的補貼，裏面含有一些必需品（如食物、能源等）。",
                    "補給包裏面也有每日配給券。順帶一提，在官方商店買東西都需要用到這張配給券哦。"
            }
        );
    }

    @EventHandler
    public void onComplete(InventoryClickEvent event) {
        if (!isQuestRunning((Player) event.getWhoClicked())) return;
        if (!event.getView().getTitle().contains("禮包")) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("每日補給包"))
            completeQuest((Player) event.getWhoClicked());
    }
}
