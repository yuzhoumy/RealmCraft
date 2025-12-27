package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;
import quest.yuzhou.realmtrader.event.PlayerTradeEvent;

public class Quest_7 extends NewbieQuest {

    public Quest_7(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "兌換裝備/武器",
                new String[]{
                        "你可以在武器商店兌換一些武器與裝備。",
                        "武器/裝備的材料需要到野外采集。",
                        "武器商店賣的武器不多，野外有更多武器/裝備兌換處。"
                },
                new String[]{
                        "防身用具還是很重要的。武器商店就在旁邊而已，進去換一些武器吧。",
                        "你得到了16個新手裝備兌換幣，請到武器商店找到名爲“鍛造師學徒”的商人兌換。",
                        "當然，這只是新人的特殊待遇而已，以後的武器材料，大部分都需要到野外搜刮。"
                }
        );
    }

    @EventHandler
    public void onComplete(PlayerTradeEvent event) {
        if (!isQuestRunning(event.getPlayer())) {
            return;
        }
        if (event.getCurrentShop().getName().equalsIgnoreCase("rpgshop-newbieseller"))
            completeQuest(event.getPlayer());
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS XINSHOUZHUANGBEIDUIHUANQUAN " + player.getName() + " 16");
    }
}
