package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

import java.sql.SQLException;

public class Quest_17 extends NewbieQuest {

    public Quest_17(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "PvP 訓練",
                new String[]{
                        "擊殺玩家可以讓你更快地獲得資源。",
                        "此外，擊殺玩家可以獲得積分。達到一定積分可以解鎖新的段位",
                        "賽季結算的時候，你會根據你的段位獲得對應獎勵。"
                },
                new String[]{
                        "這次的任務是擊殺玩家，而且是需要擊殺很多玩家。",
                        "因此，我會建議你確保家裏有一套備用裝備，以防萬一。",
                        "每擊殺一個玩家，你就可以獲得積分，同時被擊殺的玩家也會失去積分。",
                        "當你的積分達到一定數字，你的段位就提升了。賽季結算的時候，你會根據你的段位獲得對應獎勵。",
                        "如果你做好了準備，現在就可以去野外擊殺玩家了。",
                        "你的任務是擊殺玩家，直到突破10積分。"
                }
        );
    }

    @EventHandler
    public void onComplete(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) return;
        if (!isQuestRunning(event.getEntity().getKiller())) return;
        Player killer = event.getEntity().getKiller();
        int score = 0;
        try {
            score = plugin.getDatabase().getScore(killer);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error while getting player's score: " + killer);
        }
        if (score >= 10) {
            completeQuest(killer);
        }
    }
}
