package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_14 extends NewbieQuest {

    public Quest_14(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "PvE訓練",
                new String[]{
                        "PvE是獲得經驗值主要的方式。",
                        "一些怪物也會掉落實用的掉落物。"
                },
                new String[]{
                        "現在我們要打怪了。",
                        "在這個伺服器中，打怪的主要目的是獲取經驗值。而經驗值的用處，你會在下個任務瞭解到。",
                        "除此之外，怪物的掉落物也很有用。用途包括作爲食物、補充能源、拿來加工等等。",
                        "在上一個任務中，你只兌換了一個裝備，如果你覺得需要，你可以換完整套裝備再去PvE",
                        "現在，你的目標是到野外擊殺怪物，直到你達到10等級。"
                }
        );
    }

    @EventHandler
    public void onComplete(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        if (!isQuestRunning(killer)) return;

        if (killer.getLevel() >= 10)
            completeQuest(killer);
    }
}
