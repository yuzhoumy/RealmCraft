package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quest_1 extends NewbieQuest {

    public Quest_1(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "認領地下城中的地皮",
                new String[]{
                        "地下城是政府為拾荒者建造的避難所，",
                        "每個拾荒者在地下城中只能獲得一塊地皮。",
                },
                new String[]{
                        "你好，拾荒者。從剛才的介紹中你也明白了野外的危險性，所以獲得一個安身之地是很重要的。",
                        "你現在就在地下城内，環境要比地面上好的很多，天空是假的，投影的。",
                        "但這裏受到政府的嚴格管控，普通外人不能進來，所以不用擔心。",
                        "現在，申請一塊地皮吧。請輸入 /p auto"
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
            if (newMessage.equalsIgnoreCase("auto")) {
                completeQuest(event.getPlayer());
                event.getPlayer().sendMessage(Utilities.colorize("&b[政府官員] &f這裏就是你在這個星球的據點了。之後你可以將你的物資存放在這裏，在這裏蓋家。"));
            }
        }
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS HANDBOOK " + player.getName());
    }
}
