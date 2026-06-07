package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_11 extends NewbieQuest {

    public Quest_11(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "使用水培台",
                new String[]{
                        "水培台可以用來種植莊稼。",
                        "材料（如：種子）需要在野外的寶箱才能獲得。",
                        "從水培台獲得的成品，可以用來製作食物、藥物等等，這會在之後的教學詳解。"
                },
                new String[]{
                        "呼，恭喜你完成了剛才驚險刺激的尋寶。",
                        "由此可見，在這裏食物的消耗量是多麽的大，我們必須想辦法擁有穩定的食物來源",
                        "要製作食物，我們必須要有莊稼作爲原材料。現在，我將教你如何種植莊稼。",
                        "我給了你一個水培台，這是你待會將會用於種植的機器。",
                        "由於你現在（應該）深處野外，我强烈建議你先將水培台放進隨身背包裏，免得你在外面噴裝就完了。",
                        "那麽現在——你有兩個任務：\n1. 四處開寶箱，獲取5個發芽的馬鈴薯。\n2. 回到你的基地，放置並右鍵打開水培台，種植5個馬鈴薯。",
                        "盡量找手推車、廢棄餐廳、板條箱這類的資源點，這些都是食物箱！（只有這幾種資源點會開食物類材料）",
                        "我也建議你參考Discord群裏的資源點圖鑒（食物資源點），https://discord.com/channels/1065919551562326026/1444342591670976512/1444370309141631027",
                        "還記得要怎麽回到主城嗎？只需要往坐標（0，0）的方向走就行了。小心四周，野外怪物很多。"
                }
        );

    }

    @EventHandler
    public void onComplete(InventoryClickEvent event) {
        if (!isQuestRunning((Player) event.getWhoClicked())) return;
        if (!event.getView().getTitle().contains("水培台")) return;
        if (event.getCurrentItem().getType() != Material.CARROT && event.getCurrentItem().getType() != Material.POTATO) return;
        for (String lore : event.getCurrentItem().getItemMeta().getLore()) {
            if (lore.contains("這個物品已經生產完成")) {
                completeQuest((Player) event.getWhoClicked());
                return;
            }
        }
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS SHUIPEITAI " + player.getName());


    }
}
