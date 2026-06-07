package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_9 extends NewbieQuest {

    public Quest_9(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "預覽資源箱",
                new String[]{
                        "左鍵點擊資源箱，可以預覽其類型以及可能開出的物品。",
                        "你可以到Discord群組/玩家手冊查看資源箱物品列表"
                },
                new String[]{
                        "恭喜你，這是一個食物箱，顧名思義就是會專門開出食物的箱子。",
                        "資源箱有很多種，每種開出的物資都不一樣。資源箱主要分爲8種，森林箱、沙漠箱、雪地箱、異界箱、土著箱、未來箱、食物箱和機械箱。",
                        "你肯定會問，這麽多種類，這麽雜亂，我要怎麽分辨呢？我要怎麽知道裏面有什麽？",
                        "其實，你可以通過左鍵點擊箱子進行預覽。",
                        "你不僅可以知道該資源箱是什麽種類，還可以知道該箱子可能會開出什麽物資。",
                        "現在，請你用左鍵預覽剛才你所開的寶箱。"
                }
        );

    }

    @EventHandler
    public void onComplete(PlayerInteractEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Location location = event.getClickedBlock().getLocation();
        if (
                location.getBlockX() == 9 &&
                location.getBlockY() == -51 &&
                location.getBlockZ() == -3 &&
                location.getWorld() == plugin.mainWorld
        ) {
            completeQuest(event.getPlayer());
        }
    }
}
