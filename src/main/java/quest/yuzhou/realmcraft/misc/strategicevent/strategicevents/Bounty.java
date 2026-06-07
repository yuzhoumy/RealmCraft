package quest.yuzhou.realmcraft.misc.strategicevent.strategicevents;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.StrategicEvent;
import quest.yuzhou.realmcraft.types.BountyRecord;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bounty extends StrategicEvent implements Listener {

    private final int minMinute;
    private final int maxMinute;
    private final int costPerMinute;
    private final Map<Player, Player> onQueue;
    private final List<BountyRecord> bountyRecordList;
    private final BukkitTask locationNotificationTask;

    public Bounty(RealmCraft plugin) {
        super(
                plugin,
                Material.ENDER_EYE,
                "懸賞"
        );

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/strategic-event.yml"));
        minMinute = config.getInt("bounty.min-minute");
        maxMinute = config.getInt("bounty.max-minute");
        int minCost = minMinute * config.getInt("bounty.cost-per-minute");
        int maxCost = maxMinute * config.getInt("bounty.cost-per-minute");
        costPerMinute = config.getInt("bounty.cost-per-minute");
        setCost(minCost + " ~ " + maxCost);
        setDescription(new String[]{
                "當你被玩家殺死后，你可以選擇是否懸賞他。",
                "被懸賞的玩家會發光，并且伺服器會提示你他的位置在哪裏。",
                "你必須在你所設定的時間内殺死通緝犯，若成功，可以獲得100積分。",
                "如果失敗，你會被扣除與你所花費的等級一樣多的等級。",
                "其他玩家也可以一同加入懸賞。如果通緝犯在懸賞期間内",
                "被同行者殺死，則擊殺者和發動懸賞者各獲得50積分"
        });
        this.onQueue = new HashMap<>();
        this.bountyRecordList = new ArrayList<>();

        // 實時發送通緝犯的位置
        locationNotificationTask = new BukkitRunnable() {
            @Override
            public void run() {

                if (bountyRecordList.isEmpty()) return;
                if (plugin.getRestarter().isPreparingRestart()) {
                    cancel();
                    return;
                }

                for (BountyRecord bountyRecord : bountyRecordList) {
                    Player initiator = bountyRecord.initiator();
                    Player recipient = bountyRecord.recipient();

                    int x = recipient.getLocation().getBlockX();
                    int y = recipient.getLocation().getBlockY();
                    int z = recipient.getLocation().getBlockZ();

                    if (initiator.getWorld().equals(recipient.getWorld())) {
                        initiator.sendMessage(Utilities.colorize("&6通緝犯 " + bountyRecord.recipient().getName() + " &e當前位置：&b " + x + " " + y + " " + z));
                        initiator.sendMessage(Utilities.colorize(Utilities.colorize("&e他在你的 &b" + Utilities.degreeToClock(initiator.getLocation(), recipient.getLocation()) + " &e點鐘方向，距離你 &b" + Math.round(bountyRecord.recipient().getLocation().distance(initiator.getLocation())) + " &e格。")));
                    } else {
                        initiator.sendMessage(plugin.prefix + ChatColor.RED + "哦不……通緝犯逃亡到別的世界了！去別的世界找找看吧！");
                    }

                    bountyRecord.followers().forEach((player) -> {
                        if (player.getWorld().equals(recipient.getWorld())) {
                            player.sendMessage(Utilities.colorize("&6通緝犯 " + recipient.getName() + " &e當前位置：&b " + x + " " + y + " " + z));
                            player.sendMessage(Utilities.colorize(Utilities.colorize("&e他在你的 &b" + Utilities.degreeToClock(player.getLocation(), recipient.getLocation()) + " &e點鐘方向，距離你 &b" + Math.round(bountyRecord.recipient().getLocation().distance(player.getLocation())) + " &e格。")));
                        } else {
                            player.sendMessage(plugin.prefix + ChatColor.RED + "哦不……他逃亡到別的世界了！去別的世界找找看吧！");
                        }
                    });

                    bountyRecord.initiator().playSound(bountyRecord.initiator(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1);
                    bountyRecord.followers().forEach((player -> player.playSound(player, Sound.ENTITY_ENDER_EYE_DEATH, 10, 1)));
                }
            }
        }.runTaskTimer(plugin, 200, 250);

    }

    @Override
    public void onMenuClick(Player player) {
        player.sendMessage(plugin.prefix + ChatColor.RED + "此功能只有當你被人殺死之後才能使用！");
        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 10, 2);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        BountyRecord toRemove = null;
        // end bounty
        for (BountyRecord bountyRecord : bountyRecordList) {
            if (victim == bountyRecord.recipient()) {
                toRemove = bountyRecord;
                victim.setGlowing(false);
                plugin.broadcast(plugin.prefix + Utilities.colorize("&e" + bountyRecord.initiator().getName() + " &b&l對&e " + bountyRecord.recipient().getName() + " &b&l的懸賞已結束："));
                Bukkit.getOnlinePlayers().forEach((player) -> player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 2));

                if (victim.getKiller() != null) {
                    Player killer = victim.getKiller();
                    if (killer == bountyRecord.initiator()) {
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&e通緝犯 " + bountyRecord.recipient().getName() + " &4被 &e懸賞者 " + bountyRecord.initiator().getName() + " &4擊敗了。"));
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&e懸賞者 " + bountyRecord.initiator().getName() + " &f獲得獎勵 100 積分"));
                        try {
                            plugin.getDatabase().addScore(bountyRecord.initiator().getUniqueId(), 100);
                        } catch (SQLException e) {
                            plugin.getLogger().severe("Error while adding point for " + bountyRecord.initiator());
                            e.printStackTrace();
                        }
                    } else if (bountyRecord.followers().contains(killer)) {
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&e通緝犯 " + bountyRecord.recipient().getName() + " &4被 &e跟隨者 " + killer.getName() + " &4擊敗了。"));
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&e跟隨者 " + killer.getName() + " &f和 &e懸賞者 " + bountyRecord.initiator().getName() + " &f各獲得獎勵 50 積分。"));
                        try {
                            plugin.getDatabase().addScore(bountyRecord.initiator().getUniqueId(), 50);
                            plugin.getDatabase().addScore(killer.getUniqueId(), 50);
                        } catch (SQLException e) {
                            plugin.getLogger().severe("Error while adding point for " + bountyRecord.initiator() + " and " + killer);
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 退回所有积分&钱
                    int cost = bountyRecord.minute() * costPerMinute;
                    plugin.broadcast(plugin.prefix + Utilities.colorize("&e通緝犯 " + bountyRecord.recipient().getName() + " &6意外死亡了。"));
                    bountyRecord.initiator().sendMessage(plugin.prefix + ChatColor.RED + "你已被退回一開始花費的等級，共計" + cost + "等級");

                    victim.setLevel(victim.getLevel() + cost);
                }
            }
        }
        if (toRemove != null) {
            bountyRecordList.remove(toRemove);
            return;
        }

        // start bounty
        // check if player is already on a bounty

        if (plugin.getRestarter().isPreparingRestart()) {
            victim.sendMessage(plugin.prefix + ChatColor.RED + "伺服器即將重啓，不能開始新的懸賞。");
            return;
        }

        if (victim.getKiller() != null) {

            Player killer = victim.getKiller();

            if (victim != killer) {
                for (BountyRecord bountyRecord : bountyRecordList) {
                    if (
                            bountyRecord.initiator() == victim ||
                            bountyRecord.recipient() == victim ||
                            bountyRecord.followers().contains(victim)
                    ) {
                        victim.sendMessage(plugin.prefix + ChatColor.RED + "你正在參與一場懸賞，你不能同時參加兩場懸賞。");
                        return;
                    }
                }
            }
            if (plugin.getSilent().containsKey(killer)) return;

            new BukkitRunnable() {
                @Override
                public void run() {
                    onQueue.put(victim, killer);
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&b&l哦不！你被可惡的 &6" + killer.getName() + "&b&l擊殺了！想不想以牙還牙？"));
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&e是否懸賞 &6" + killer.getName() + "&e？"));
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&e在懸賞期間内，如果你擊殺了他，你可以獲得 &f$10000"));
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&e每分鐘懸賞需花費 &f" + costPerMinute + " &e等級。"));
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&e懸賞時長不得低於&6&n" + minMinute + "分鐘&e，也不能大於&6&n" + maxMinute + "分鐘。"));
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&e如果要懸賞，&e&n請在聊天欄打出懸賞時長（分鐘）（打數字就好）。"));
                    victim.sendMessage(plugin.prefix + Utilities.colorize("&7你有15秒決定。"));
                    victim.playSound(victim, Sound.BLOCK_NOTE_BLOCK_FLUTE, 10, 2);
                }
            }.runTaskLater(plugin, 200);

            Bukkit.getScheduler().runTaskLater(plugin, () -> onQueue.remove(victim), 500);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!onQueue.containsKey(event.getPlayer())) return;

        event.setCancelled(true);

        Player victim = event.getPlayer();
        Player killer = onQueue.get(victim);
        int minute = 0;
        int victimScore = 0;

        try {
            minute = Integer.parseInt(event.getMessage());
        } catch (NumberFormatException e) {
            victim.sendMessage(plugin.prefix + Utilities.colorize("&6你輸入的似乎不是數字。如果要懸賞 &e" + killer.getName() + " &6，請輸入你要懸賞的時長（分鐘）。&c&l輸入數字即可。"));
        }

        if (minute < minMinute || minute > maxMinute) {
            victim.sendMessage(plugin.prefix + Utilities.colorize("&c時間太長/太短了。請輸入介於 " + minMinute + " 與 " + maxMinute + " 之間的數字。"));
            return;
        }

        victimScore = victim.getLevel();

        if (victimScore < minute * costPerMinute) {
            victim.sendMessage(plugin.prefix + ChatColor.RED + "你的等級不足。你必須至少有 " + minute * costPerMinute + " 等級。考慮短一點的時長吧？");
            return;
        }

        victim.setLevel(victim.getLevel() - (minute * costPerMinute));

        startBounty(victim, killer, minute);

    }

    private void startBounty(Player initiator, Player recipient, int time) {
        bountyRecordList.add(new BountyRecord(initiator, recipient, new ArrayList<>(), time));
        plugin.broadcast(plugin.prefix + Utilities.colorize("&d號外！號外！&e" + initiator.getName() + " &d對 &e" + recipient.getName() + " &d發起了懸賞！"));
        plugin.broadcast(plugin.prefix + Utilities.colorize("&d想成爲同行者一同追殺 &e" + recipient.getName() + " &d嗎？在 " + time +" 分鐘内成功擊殺 &e" + recipient.getName() + " &d的話可以獲得獎金 &e$5000 &d。輸入 &2/rc join-bounty " + recipient.getName() + " &d加入懸賞。"));
        Bukkit.getOnlinePlayers().forEach((player) -> {
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_3, 10F, 0.5F);
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.5F);
        });
        recipient.setGlowing(true);

        // check bounty failed?
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BountyRecord bountyRecord : bountyRecordList) {
                    if (bountyRecord.initiator() == initiator) {
                        int cost = bountyRecord.minute() * costPerMinute;

                        bountyRecordList.remove(bountyRecord);
                        recipient.setGlowing(false);
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&e" + bountyRecord.initiator().getName() + " &b&l對&e " + bountyRecord.recipient().getName() + " &b&l的懸賞已結束："));
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&e通緝犯 " + bountyRecord.recipient().getName() + " &c已經逃之夭夭……由於 &e懸賞者 " + bountyRecord.initiator().getName() + " &c執行任務失敗，獲得懲罰：扣除 " + cost + " 等級。"));
                        Bukkit.getOnlinePlayers().forEach((player) -> player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 2));

                        bountyRecord.initiator().setLevel(bountyRecord.initiator().getLevel() - cost);

                        return;
                    }
                }
            }
        }.runTaskLater(plugin, (long) time * 20 * 60);
    }

    public void joinBounty(Player follower, String recipient) {

        for (BountyRecord bountyRecord : bountyRecordList) {

            if (bountyRecord.followers().contains(follower) || bountyRecord.initiator() == follower || bountyRecord.recipient() == follower) {
                follower.sendMessage(plugin.prefix + ChatColor.RED + "你已經是其中一位同行者了。或者你已經參加了另一個懸賞。");
                return;
            }

            if (bountyRecord.recipient().getName().equalsIgnoreCase(recipient)) {
                bountyRecord.followers().add(follower);
                plugin.broadcast(plugin.prefix + Utilities.colorize("&e" + follower.getName() + " &d加入了對 &c" + bountyRecord.recipient() + " &d的懸賞！"));
                return;
            }
        }

        follower.sendMessage(plugin.prefix + ChatColor.RED + "無法找到名爲 " + recipient + " 的通緝犯。請檢查名字拼寫。");
        follower.sendMessage("/rc join-bounty <通緝犯名字>" + ChatColor.GOLD + "<--- 這裏要寫對！");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (BountyRecord bountyRecord : bountyRecordList) {
            if (bountyRecord.recipient() == event.getPlayer()) {
                plugin.broadcast(plugin.prefix + Utilities.colorize("&e" + bountyRecord.initiator().getName() + " &b&l對&e " + bountyRecord.recipient().getName() + " &b&l的懸賞已結束："));
                plugin.broadcast(plugin.prefix + Utilities.colorize("&e通緝犯 " + bountyRecord.recipient().getName() + " &c退出了游戲。通緝犯在懸賞時退出游戲，獲得懲罰：扣除20等級。"));
                Bukkit.getOnlinePlayers().forEach((player) -> player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 2));

                event.getPlayer().setLevel(event.getPlayer().getLevel() - 20);
            }
        }
    }

    @Override
    public void stop() {
        locationNotificationTask.cancel();
    }
}
