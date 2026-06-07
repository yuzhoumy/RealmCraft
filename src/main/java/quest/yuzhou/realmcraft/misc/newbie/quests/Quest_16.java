package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_16 extends NewbieQuest {

    public Quest_16(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "使用煉藥系統",
                new String[]{
                        "製作藥水的原材料是水瓶和顔色液體。水瓶需要到主城水井兌換。",
                        "製作好的藥水，再加入烟草繼續釀造可以獲得相反效果的藥水，加入不明物質則可以轉換成滯留藥水。",
                },
                new String[]{
                        "現在，我會教你如何製作藥水。",
                        "首先，你需要一個釀造台。請到官方商店找「機器賣家」購買釀造台。",
                        "接著，你需要用「二階合成台」製作空瓶子。如果你沒有二階合成台，可以先去商店買一個。",
                        "製作完空瓶子後，到主城水井找「打水人」把空瓶子換成水瓶。",
                        "藥水的主要材料是各種顏色的液體。最基本的是「紫色液體」，只需要馬鈴薯和濃縮液態能源就能合成。",
                        "濃縮液態能源可以用 9 個液態能源合成，而液態能源能從「分揀機」中隨機獲得（分揀時會有機率掉出）。",
                        "準備好水瓶和紫色液體後，打開釀造台。用「水瓶 + 紫色液體」即可製作出你的第一個回復瓶！"
                }
        );
    }

    @EventHandler
    public void onComplete(InventoryClickEvent event) {
        if (!isQuestRunning((Player) event.getWhoClicked())) return;
        if (!event.getView().getTitle().contains("釀造台")) return;
        if (event.getCurrentItem().getType() != Material.POTION) return;
        if (!event.getCurrentItem().getItemMeta().getDisplayName().contains(Utilities.colorize("&6&l回復瓶"))) return;
        for (String lore : event.getCurrentItem().getItemMeta().getLore()) {
            if (lore.contains("這個物品已經生產完成")) {
                completeQuest((Player) event.getWhoClicked());
                return;
            }
        }
    }
}
