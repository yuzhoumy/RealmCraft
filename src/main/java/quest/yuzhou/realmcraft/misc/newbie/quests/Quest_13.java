package quest.yuzhou.realmcraft.misc.newbie.quests;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;
import quest.yuzhou.realmtrader.event.PlayerTradeEvent;

public class Quest_13 extends NewbieQuest {

    public Quest_13(RealmCraft plugin, int number) {
        super(
                plugin,
                number,
                "自己從商店買“鋼筋刀”",
                new String[]{
                        "本伺服器所有的裝備/武器的兌換方式都遵循以下形式：",
                        "核心 + 32材料 -> 裝備/武器",
                        "而核心的獲得方式則是：",
                        "32核心材料 + 32核心材料 -> 核心"
                },
                new String[]{
                        "由於在野外很容易噴裝，這次我將訓練你靠自己的能力獲得裝備。",
                        "我們就拿鋼筋刀來做示範吧。待會，我會教你怎麽自己換該武器。",
                        "雖然你已經有了，但保險起見，你可以一把拿來用，一把放家裏",
                        "在這之前，你需要瞭解一些基本的兌換公式：\n32核心材料 + 32核心材料 -> 核心\n核心 + 32武器材料 -> 武器",
                        "所以，你為鋼筋刀收集材料的路綫是這樣的……",
                        "你總共需要收集128個機械元件，和32個回收碳鋼。",
                        "然後，到武器商店裏面找商人，\n（現實系列）核心鑄造師，\n與他交易：32機械元件 + 32機械原件 -> 亮白之石\n再找 （現實系列）進戰武器鍛造師\n與他交易：亮白之石 + 32回收碳鋼 -> 鋼筋刀",
                        "現在，你需要……\n1. 去野外搜集資源。\n2. 去武器商店，兌換一把鋼筋刀",
                        "請注意！！！上述的材料只有在平原/森林才能獲得。沙漠、雪地是絕對找不到的……",
                        "現在就出發吧，拾荒者。"
                }
        );

    }

    @EventHandler
    public void onComplete(PlayerTradeEvent event) {
        if (!isQuestRunning(event.getPlayer())) return;
        if (event.getCurrentShop().name().equalsIgnoreCase("nearweapon-xianshi"))
            completeQuest(event.getPlayer());
        else {
            event.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "請聽從教學的指示，找 “現實-近戰武器鍛造師” 兌換武器哦。（在武器商店地下室）");
            event.setCancelled(true);
            event.getPlayer().closeInventory();
        }
    }
}
