package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_8 extends NewbieQuest {

    public Quest_8(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "開啓資源箱",
                new String[]{
                        "要打開資源箱，你需要右鍵該箱子，",
                        "并在指定範圍内等待數秒，物品才會噴出來。"
                },
                new String[]{
                        "尋找資源點將是你重要的獲取的資源方式，",
                        "在荒蕪的野外，有很多古代留下來的廢棄遺跡，比如：荒廢的汽車，荒廢的房子等等。",
                        "在這些建築物中，不少都含有寶箱。寶箱裏會開出一些物資，你需要靠著不斷開寶箱生存。",
                        "在你眼前的就是一個例子——野外可能會生成的食物資源點。",
                        "裏面有一個寶箱，你能找到它在哪裏並右鍵打開它嗎？"
                }
        );

    }

    @EventHandler
    public void onComplete(PlayerInteractEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

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

    @Override
    protected void forceDoWhenStart(Player player) {
        player.teleport(new Location(
                plugin.mainWorld,
                26,
                -52,
                -2,
                90,
                0
        ));
        new Location(plugin.mainWorld, 9, -50, -3).getBlock().setType(Material.AIR);
    }
}
