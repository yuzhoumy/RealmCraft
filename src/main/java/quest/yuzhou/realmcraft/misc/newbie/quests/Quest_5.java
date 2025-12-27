package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;

public class Quest_5 extends NewbieQuest {

    public Quest_5(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "使用分揀機",
                new String[]{
                        "分揀機可以從廢料中獲取基礎資源",
                        "手持廢料資源，右鍵分揀機即可"
                },
                new String[]{
                        "現在，你手上有一個分揀機和一些廢料。",
                        "你可以把廢料放進分揀機裏，等分揀機滿了后，會很神奇地噴出有用的材料。這個機器在前期是非常有用的。",
                        "如何知道一個物品可不可以被分揀呢？你只需要查看該物品的簡介，如果類型那裏寫“廢料資源”，就可以放入分揀機。",
                        "請把分揀機放在地上，然後用廢料右鍵分揀機。"
                }
        );
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.COMPOSTER) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Material material = player.getInventory().getItemInMainHand().getType();
        if (
                material == Material.JUNGLE_LEAVES ||
                material == Material.COCOA_BEANS ||
                material == Material.DRIED_KELP_BLOCK
        ) {
            completeQuest(player);
        }
    }

    @Override
    protected void doWhenStart(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give MISCELLANEOUS FENJIAN " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give RUBBISH COMMONTRASH " + player.getName() + " 16");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give RUBBISH FIRETRASH " + player.getName() + " 16");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi give RUBBISH METALTRASH " + player.getName() + " 16");
    }
}
