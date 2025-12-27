package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;
import quest.yuzhou.realmtrader.event.PlayerTradeEvent;

public class Quest_6 extends NewbieQuest {

    public Quest_6(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "在官方商店交易物品",
                new String[]{
                        "你可以在主城的官方商店裏購買/售賣物品。",
                        "需要注意的是，買價略高于賣價。"
                },
                new String[]{
                        "你得到了一個一階合成臺，你可以用剛剛分揀出來的東西合成。",
                        "如果你剛才有正確使用分揀機的話，你現在手上應該有一些材料。不過沒關係，我給了你64個廢木。",
                        "現在，請合成16個合成木材，前往主城官方商店，找到名爲“資源回收”的商人，跟他換錢。",
                        "如果你忘記了怎麽回主城的話，可以輸入/rc tutorial，可以查看新手教學的進度和之前的教學的筆記哦。",
                }
        );
    }

    @EventHandler
    public void onComplete(PlayerTradeEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (
                event.getCurrentTradeItem().materials().get(0).getName().contains("合成木板") &&
                event.getCurrentShop().getName().equalsIgnoreCase("shop_recycle")
        ) {
            completeQuest(event.getPlayer());
        }
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give RUBBISH TRASHWOOD " + player.getName() + " 64");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS CRAFT1 " + player.getName());
    }
}
