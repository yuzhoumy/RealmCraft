package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_15 extends NewbieQuest {

    public Quest_15(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "使用幸運箱",
                new String[]{
                        "幸運箱屬於抽獎系統的一部分。",
                        "幸運箱分爲五個等級，每個等級的幸運箱開出來的東西都稍有出入。",
                        "這五種幸運箱，每次開啓分別需消耗10、20、30、40、50等級。",
                        "幸運箱在主城的賭場裏面。"
                },
                new String[]{
                        "經歷了驚險刺激的戰鬥，是時候來點娛樂了……",
                        "在主城的賭場裏面，有一些抽獎箱，被稱爲幸運箱。",
                        "幸運箱分爲五個等級，分別是青草、杏花、玫瑰、木槿和蝴蝶蘭。",
                        "等級越高的幸運箱，開出的東西也就越好。",
                        "開啓幸運箱是需要消耗經驗值的。這五種幸運箱，每次開啓分別需消耗10、20、30、40、50等級。",
                        "你目前的經驗值剛剛好夠開“青草”幸運箱。",
                        "請你現在回到主城，進入賭場，開啓該幸運箱。"
                }
        );
    }

    @EventHandler
    public void onComplete(PlayerInteractEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Location location = event.getClickedBlock().getLocation();
        if (
                location.getBlockX() == 3 &&
                location.getBlockY() == 71 &&
                location.getBlockZ() == -59
        ) {
            completeQuest(event.getPlayer());
        }
    }
}
