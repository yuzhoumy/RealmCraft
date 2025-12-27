package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quest_4 extends NewbieQuest {

    public Quest_4(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "回到自己的地皮",
                new String[]{
                        "要先去到通往地下城的電梯（在主城的中心）",
                        "進入電梯后，輸入/p home"
                },
                new String[]{
                        "接下來，要回到自己的地皮，也一樣要先去到電梯那裏。",
                        "電梯坐落於主城的中心，我相信當你走出城堡時，就已經看到了。",
                        "進入地下城之後，輸入 /p home，就傳送到地皮啦。"
                }
        );
    }

    @EventHandler
    public void onComplete(PlayerCommandPreprocessEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        String message = event.getMessage();
        if (message.startsWith("/p") || message.startsWith("/plot") || message.startsWith("/plots")) {
            List<String> messageArray = new ArrayList<>(Arrays.asList(message.split(" ")));
            messageArray.remove(0);
            String newMessage = String.join(" ",messageArray);
            if (newMessage.equalsIgnoreCase("home")) {
                completeQuest(event.getPlayer());
            }
        }
    }

}
