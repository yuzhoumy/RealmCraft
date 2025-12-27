package quest.yuzhou.realmcraft.misc.newbie;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.misc.newbie.quests.*;

import java.util.ArrayList;
import java.util.List;

public class NewbieQuestManager {

    private final RealmCraft plugin;
    private final List<NewbieQuest> newbieQuests = new ArrayList<>();

    public NewbieQuestManager(RealmCraft plugin) {
        this.plugin = plugin;
        newbieQuests.add(new Quest_1(plugin, 1));
        newbieQuests.add(new Quest_2(plugin, 2));
        newbieQuests.add(new Quest_3(plugin, 3));
        newbieQuests.add(new Quest_4(plugin, 4));
        newbieQuests.add(new Quest_5(plugin, 5));
        newbieQuests.add(new Quest_6(plugin, 6));
        newbieQuests.add(new Quest_7(plugin, 7));
        newbieQuests.add(new Quest_8(plugin, 8));
        newbieQuests.add(new Quest_9(plugin, 9));
        newbieQuests.add(new Quest_10(plugin, 10));
        newbieQuests.add(new Quest_11(plugin, 11));
        newbieQuests.add(new Quest_12(plugin, 12));
        newbieQuests.add(new Quest_13(plugin, 13));
        newbieQuests.add(new Quest_14(plugin, 14));
        newbieQuests.add(new Quest_15(plugin, 15));
        newbieQuests.add(new Quest_16(plugin, 16));
        newbieQuests.add(new Quest_17(plugin, 17));
        newbieQuests.add(new Quest_18(plugin, 18));
        for (NewbieQuest quest : newbieQuests) {
            plugin.getServer().getPluginManager().registerEvents(quest, plugin);
        }
    }

    public void start(Player player, int number, boolean doWhenStart) {
        for (NewbieQuest quest : newbieQuests) {
            if (quest.getNumber() == number) {
                quest.start(player, doWhenStart);
                return;
            }
        }
        throw new IndexOutOfBoundsException("No quest found with quest number " + number);
    }

    public void forceStop(Player player) {
        for (NewbieQuest quest : newbieQuests) {
            if (quest.isQuestRunning(player)) {
                quest.forceStop(player);
                return;
            }
        }
    }

    public int getQuestsAmount() {
        return newbieQuests.size();
    }

    public List<NewbieQuest> getNewbieQuests() {
        return newbieQuests;
    }

    public NewbieQuest getPlayerRunningQuest(Player player) {
        for (NewbieQuest quest : newbieQuests) {
            if (quest.isQuestRunning(player)) {
                return quest;
            }
        }
        return null;
    }



    public void briefExplanation(Player player) {

        String[] text = new String[]{
                "當你在擊殺玩家的時候，你會獲得積分。",
                "不僅如此，從你所擊殺的玩家身上，掉落的武器與裝備，你可以到城堡裏的回收站兌換積分。",
                "如果你需要購買武器與裝備，你需要去主城的“武器商店”。",
                "武器與裝備的材料，可以在野外的寶箱中獲得。",
                "在主世界，也有數個副本，會獎勵大量的武器/裝備材料。",
                "想要瞭解伺服器的更多玩法？請通過 /rc tutorial 與 /rc handbook 指令查看。現在就開始你的冒險吧，拾荒者！"
        };

        Location[] locations = {
                new Location(plugin.mainWorld, -8, 68, -5, -45, 0),
                new Location(plugin.mainWorld, -21, 84, 71),
                new Location(plugin.mainWorld, 74, 76, 56, -90, 0),
                new Location(plugin.mainWorld, 210, 94, -11, -45, 25),
                new Location(plugin.mainWorld, 736, 106, 547, 0, 45),
                new Location(plugin.mainWorld, 27, 67, -25, 45, 0)
        };


        new BukkitRunnable() {

            int step = 0;

            @Override
            public void run() {

                player.teleport(locations[step]);
                player.sendMessage(ChatColor.AQUA + "[政府官員]" + ChatColor.WHITE + text[step]);

                if (step == 0) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.setPlayerWeather(WeatherType.CLEAR);
                }

                if (step == text.length - 1) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.resetPlayerWeather();
                    cancel();
                    return;
                }

                step++;
            }

        }.runTaskTimer(plugin, 0, 200);
    }

}
