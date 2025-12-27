package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_2 extends NewbieQuest{

    public Quest_2(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "從地下城返回主城",
                new String[]{
                        "從地下城返回主城需輸入指令：",
                        "/rc elevator"
                },
                new String[]{
                        "地下城是蓋在主城之下的，所以這裏上去就是主城啦。",
                        "而往返地面的唯一通道是電梯，你需要輸入 /rc elevator 傳送到那邊。進入電梯之後即進入主城。"
                }
        );
    }

    @EventHandler
    public void onComplete(PlayerCommandPreprocessEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (event.getMessage().equalsIgnoreCase("/rc elevator")) {
            completeQuest(event.getPlayer());
            event.getPlayer().sendMessage(Utilities.colorize("&b[政府官員]雖然到這裏就算作是完成任務，但請不要忘記進入電梯。"));
        }
    }

}
