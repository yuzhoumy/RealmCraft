package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;
import quest.yuzhou.realmcraft.player.menu.MenuManager;

public class Quest_18 extends NewbieQuest {

    public Quest_18(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "使用探勘家",
                new String[]{
                        "探勘家是其中一個戰略事件。欲觸發，需輸入/rc se打開戰略事件界面。",
                        "每次發動探勘家，會消耗50等級。",
                        "探勘家會在地圖上生成數個獎勵豐厚的資源點，並將坐標廣播出來。"
                },
                new String[]{
                        "恭喜你，PvP能力不同常人。",
                        "覺得資源不夠嗎？聘請以位探勘家幫你找資源點吧。",
                        "在這個任務中，我會教你如何聘請探勘家。他會幫你在野外尋找一個沒被開過的，獎勵超級豐厚的資源點。",
                        "但有一個缺點…… 他每次找到資源點后，會將資源點的坐標廣播給所有人。也就是説，那裏很可能會爆發一場混戰。",
                        "每聘請一次探勘家，你需要花費50等級。付了積分之後，只需要等待1分鐘，探勘家就會找到資源點了。",
                        "我給了你50等級，剛剛好夠聘請一次。現在，請你輸入指令/rc se，然後點擊“探勘家。”"
                }
        );
    }

    @EventHandler
    public void onComplete(InventoryClickEvent event) {
        if (!isQuestRunning((Player) event.getWhoClicked())) return;
        if (!event.getView().getTitle().equalsIgnoreCase(Utilities.colorize(plugin.getMenuManager().getMenu(MenuManager.MenuType.STRATEGIC_EVENT_MENU).getMenuName()))) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("探勘家"))
            completeQuest((Player) event.getWhoClicked());
    }

    @Override
    protected void doWhenStart(Player player) {
        player.setLevel(player.getLevel() + 50);
    }
}
