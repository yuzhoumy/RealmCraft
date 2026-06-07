package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class Quest_10 extends NewbieQuest {

    public Quest_10(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "在野外尋找資源點",
                new String[]{
                        "尋找資源點將是你重要的獲取的資源方式，",
                        "但在野外務必小心，主城之外的範圍都是可以PVP的",
                        "如果寶箱上面有海燈籠，代表該寶箱已經被人開過了。"
                },
                new String[]{
                        "現在，我們實戰一次吧，你需要親自到野外尋找資源點",
                        "你往四面八方走都可以走出主城。但如果讓我推薦一條新手路綫，我會建議你從主城東北方的城門開始走。",
                        "野外是很危險的，許多拾荒者都在虎視眈眈地盯著你身上的物資……",
                        "一不小心被人幹掉的話，你的裝備通通噴光哦。",
                        "你心裏一定有個疑問，在野外要怎麽知道主城的方向呢？很簡單，你只需要往原點（坐標0，0）的方向走，就會到主城了",
                        "你需要小心翼翼地觀察野外的建築，看看裏面有沒有寶箱？",
                        "如果看到寶箱上面有海燈籠，那麽就代表這個寶箱已經被人開過了。反之，你可以開。",
                        "我給了你一個隨身背包。你可以將你的物資放在裏面，放在隨身背包裏的物品死亡的時候就不會噴掉。",
//                        "這是最後一次的新手教學了，之後的教學都會在 /rc handbook 裏面。裏面有許多關於伺服器的其他機制、玩法的詳細介紹。得空請一定要去看，能讓你少走十年彎路。",
                        "現在，讓我公佈你的任務——去到野外，打開一個寶箱。注意安全，祝你好運，拾荒者。"
                }
        );

    }

    @EventHandler
    public void onComplete(PlayerInteractEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        if (event.getPlayer().getWorld() != plugin.mainWorld) return;

        List<String> chestNames = plugin.getConfig().getStringList("chest-names");
        Chest chest = (Chest) event.getClickedBlock().getState();
        if (chest.getCustomName() == null) return;

        if (chestNames.contains(chest.getCustomName())) {
            completeQuest(event.getPlayer());
        }
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS &9&l随身背包 " + player.getName());
        unlockChestsForNewbie();
    }

    @Override
    protected void forceDoWhenStart(Player player) {

        player.teleport(new Location(
                plugin.mainWorld,
                64,
                64,
                -44,
                -135,
                0
        ));
    }

    private void unlockChestsForNewbie() {
        List<Block> blockAboveList = plugin.getRCChest().getChestAndCommandManager().getBlockAboveList();
        List<Block> filteredList = new ArrayList<>();

        for (Block block : blockAboveList) {
            if (block.getLocation().distance(new Location(plugin.mainWorld, 132, 71, -96)) >= 150) {
                filteredList.add(block);
            }
        }

        plugin.getRCChest().getChestAndCommandManager().setBlockAboveList(filteredList);

    }
}
