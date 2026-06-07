package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_12 extends NewbieQuest {

    public Quest_12(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "使用烹飪爐",
                new String[]{
                        "烹飪爐是用來生產食物的機器。",
                        "烹飪爐所需的原材料一般由種植、野外搜刮獲得。",
                        "烹飪爐需要氣態燃料才能工作。",
                        "要獲得氣態燃料，你必須到玩家商店外面尋找“燃料運輸員”兌換。"
                },
                new String[]{
                        "現在，讓我們學習如何烹煮食物。",
                        "我給了你一個烹飪爐，把它放在地上吧。",
                        "當然，這個機器是需要燃料的，每樣食物都需要“氣態燃料”才能進行烹飪。",
                        "那麽，如何獲得“氣態燃料”呢？",
                        "你需要前往主城的玩家商店。在玩家商店外面，你會看到一位正在拉著推車的阿伯，“燃料運輸員”，他有賣“氣態燃料”。",
                        "請你現在：\n1. 兌換 1 個氣態燃料\n2. 打開烹飪鱸，用你從上一個任務獲得的马铃薯，烹飪“香烤马铃薯”。"
                }
        );
    }

    @EventHandler
    public void onComplete(InventoryClickEvent event) {
        if (!isQuestRunning((Player) event.getWhoClicked())) return;
        if (!event.getView().getTitle().contains("烹飪爐")) return;
        if (event.getCurrentItem().getType() != Material.GOLDEN_CARROT && event.getCurrentItem().getType() != Material.COOKED_PORKCHOP) return;
        for (String lore : event.getCurrentItem().getItemMeta().getLore()) {
            if (lore.contains("這個物品已經生產完成")) {
                completeQuest((Player) event.getWhoClicked());
                return;
            }
        }
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS COOK " + player.getName());
    }

}
