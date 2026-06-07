package quest.yuzhou.realmcraft.misc.strategicevent.strategicevents;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.ServerExecutable;
import quest.yuzhou.realmcraft.types.StrategicEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GateOfTheBrave extends StrategicEvent implements Listener, ServerExecutable {

    private final Map<Player, Location> joinerList = new LinkedHashMap<>(); // initiator is first element
    private final Location arenaCentral;
    private final List<Location> arena = new ArrayList<>();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd日MM月yyyy年 HH時mm分ss秒");
    private final int minPlayer;
    private final int minPoint;
    private final int totalPrize;
    private int endingStage;
    private long cooldown;

    public GateOfTheBrave(RealmCraft plugin) {
        super(
                plugin,
                Material.TRIDENT,
                "40",
                "勇者之門",
                new String[]{}
        );

        endingStage = -1;
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "strategic-event.yml"));
        minPlayer = config.getInt("gate-of-the-brave.min-player");
        minPoint = config.getInt("gate-of-the-brave.min-point");
        totalPrize = config.getInt("gate-of-the-brave.total-prize");
        arenaCentral = new Location(plugin.fieldWorld, 565.5, -58, 101.5, 0, 0);
        arena.add(new Location(plugin.fieldWorld, 565.5, -60, 75.5, 0, 0));
        arena.add(new Location(plugin.fieldWorld, 585.5, -59, 81.5, 45, 0));
        arena.add(new Location(plugin.fieldWorld, 591.5, -60, 101.5, 90, 0));
        arena.add(new Location(plugin.fieldWorld, 585.5, -59, 121.5, 135, 0));
        arena.add(new Location(plugin.fieldWorld,565.5, -60, 127.5, 180, 0));
        arena.add(new Location(plugin.fieldWorld, 545.5, -59, 121.5, -135, 0));
        arena.add(new Location(plugin.fieldWorld, 539.5, -60, 101.5, -90, 0));
        arena.add(new Location(plugin.fieldWorld, 545.5, -59, 81.5, -45, 0));

        setDescription(
                new String[]{
                        "主世界内，你與所有積分大於 " + minPoint + " 的玩家會被傳送到一個臨時競技場。",
                        "你們需要決一死戰，兩分鐘后仍然存活的人可以被平分" + totalPrize + "積分",
                        "當然，如果只剩下一個人，勇者之門會提早關閉，并且獎池積分全部給他。",
                        "此功能只能在主世界使用。"
                });
    }

    @Override
    public void onMenuClick(Player player) {

        if (plugin.getRestarter().isPreparingRestart()) {
            player.sendMessage(plugin.prefix + ChatColor.RED + "伺服器即將重啓，不能開始新的勇者之門。");
            return;
        }

        if (!joinerList.isEmpty()) {
            player.sendMessage(plugin.prefix + ChatColor.RED + "競技場目前正在被使用中，請耐心等待當前的勇者之門結束。");
            return;
        }

        if (player.getWorld() != plugin.mainWorld) {
            player.sendMessage(plugin.prefix + ChatColor.RED + "你只能在主世界中開啓勇者之門！");
            return;
        }

        if (System.currentTimeMillis() < cooldown) {
            String date = simpleDateFormat.format(new Date(cooldown));
            player.sendMessage(plugin.prefix + ChatColor.RED + "勇者之門目前還在冷卻中，下次可用時間是 " + ChatColor.GOLD + ChatColor.UNDERLINE + date);
            return;
        }

        if (player.getLevel() >= 40) {

            Location location = player.getLocation();
            joinerList.put(player, location);
            for (Player mainWorldPlayer : plugin.mainWorld.getPlayers()) {
                if (mainWorldPlayer == player) continue;
                try {
                    if (plugin.getDatabase().playerHasEnoughScore(mainWorldPlayer, minPoint)) {
                        joinerList.put(mainWorldPlayer, mainWorldPlayer.getLocation());
                    }
                } catch (SQLException e) {
                    mainWorldPlayer.sendMessage(plugin.prefix + ChatColor.RED + "在檢查你的積分的時候數據庫發生嚴重錯誤，請立即通知管理員");
                    plugin.getLogger().severe("Error while checking player's points");
                    e.printStackTrace();
                }
            }

            // cancel if not enough ppl
            if (joinerList.size() < minPlayer) {
                joinerList.clear();
                player.sendMessage(plugin.prefix + ChatColor.RED + "人數不足，目前主世界内積分大於" + minPoint + "的玩家只有不到 " + minPlayer + " 個人。無法開始。");
                return;
            }

            player.setLevel(player.getLevel() - Integer.parseInt(cost));
            run(player);

        } else {
            player.sendMessage(plugin.prefix + ChatColor.RED + "你的等級不足，發動一次勇者之門會消耗40等級。");
        }

    }

    @Override
    public void serverExecute() {
        if (plugin.getRestarter().isPreparingRestart()) {
            return;
        }

        if (!joinerList.isEmpty()) {
            return;
        }

        if (System.currentTimeMillis() < cooldown) {
            return;
        }

        for (Player mainWorldPlayer : plugin.mainWorld.getPlayers()) {
            try {
                if (plugin.getDatabase().playerHasEnoughScore(mainWorldPlayer, minPoint)) {
                    joinerList.put(mainWorldPlayer, mainWorldPlayer.getLocation());
                }
            } catch (SQLException e) {
                mainWorldPlayer.sendMessage(plugin.prefix + ChatColor.RED + "在檢查你的積分的時候數據庫發生嚴重錯誤，請立即通知管理員");
                plugin.getLogger().severe("Error while checking player's points");
                e.printStackTrace();
            }
        }

        // cancel if not enough ppl
        if (joinerList.size() < minPlayer) {
            joinerList.clear();
            return;
        }

        run(null);
    }

    private void run(@Nullable Player executor) {
        if (executor == null) {
            plugin.broadcast(plugin.prefix + Utilities.colorize("&3&l### &7&k芷 &b&l勇者之門 &b開啓了。 &7&k妤 &3&l###"));
        } else {
            plugin.broadcast(plugin.prefix + Utilities.colorize("&3&l### &7&k芷 &b&l勇者之門 &b被 &e" + executor.getName() + " &b啓動了。 &7&k妤 &3&l###"));
            executor.sendMessage(plugin.prefix + ChatColor.DARK_RED + ChatColor.BOLD + "你將在60秒後進入競技場。");
        }
        plugin.broadcast(plugin.prefix + ChatColor.RED + "所有積分大於 " + minPoint + " 的玩家都必須進入勇者之門戰鬥！");
        plugin.broadcast(plugin.prefix + ChatColor.RED + "系統一共選擇了 " + joinerList.size() + " 位拾荒者勇者之門内廝殺！");
        Bukkit.getOnlinePlayers().forEach((p) -> p.playSound(p, Sound.ITEM_GOAT_HORN_SOUND_5, 10F, 0.5F));
        joinerList.keySet().forEach((p) -> {
            p.sendMessage(plugin.prefix + ChatColor.RED + ChatColor.BOLD + "你將在15秒後傳送到一個競技場長達 2 分鐘，你需要在那場混戰中存活下來。");
            p.sendMessage(plugin.prefix + ChatColor.RED + ChatColor.BOLD + "如果你成功存活到最後，你可以與幸存者平分獎池： " + totalPrize + " 積分。");
            plugin.getSilent().put(p, "玩家正在勇者之門中");
        });

//            Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                joinerList.forEach((p, loc) -> {
//                    p.sendMessage(plugin.prefix + ChatColor.BLUE + "勇者之門説明：");
//                    p.sendMessage("1. 你會被傳送到一個競技場時長");
//                    p.playSound(p, Sound.UI_BUTTON_CLICK, 10, 2);
//                });
//            }, 100);

        // second counter (for 5 sec bef start)
        new BukkitRunnable() {

            int second = 5;
            @Override
            public void run() {

                if (second == 0) {
                    List<Player> playerlist = joinerList.keySet().stream().toList();
                    for (int i = 0; i < joinerList.size(); i++) {
                        if (i == 0) {
                            playerlist.get(i).teleport(arenaCentral);
                            continue;
                        }

                        playerlist.get(i).teleport(arena.get(i % 8));
                    }

                    joinerList.keySet().forEach((p) -> {
                        p.sendTitle("勇者之門", "", 0, 40, 20);
                        p.playSound(p, Sound.ITEM_TRIDENT_THUNDER, 10F, 0.5F);
                    });

                    cancel();
                } else {

                    joinerList.keySet().forEach((p) -> {
                        p.sendTitle(ChatColor.LIGHT_PURPLE + "" + second, "", 0, 20, 0);
                        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT, 10, 1);
                    });
                    second--;
                }
            }

        }.runTaskTimer(plugin, 220, 20);


        // 10 second counter (for last 60 sec)
        new BukkitRunnable() {

            int second = 60;
            @Override
            public void run() {

                if (endingStage != -1) {
                    cancel();
                    return;
                }

                if (joinerList.isEmpty() || second == 0) {
                    cancel();
                    return;
                }

                joinerList.keySet().forEach((p) -> p.sendMessage(plugin.prefix + "時間還剩 " + second + " 秒"));
                second -= 10;
            }
        }.runTaskTimer(plugin, 1500, 200);//delay 1500 ticks (bef start 15 sec + 60 sec)

        // 15 sec counter (15 sec bef end)
        new BukkitRunnable() {

            int second = 15;

            @Override
            public void run() {

                if (endingStage != -1) {
                    cancel();
                    return;
                }

                if (joinerList.isEmpty()) return;
                joinerList.keySet().forEach((p) -> {
                    p.sendTitle("", ChatColor.LIGHT_PURPLE + "" + second, 0, 20, 0);
                    p.playSound(p, Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
                });
                if (second == 1) {
                    cancel();
                    return;
                }
                second--;
            }
        }.runTaskTimer(plugin, 2400, 20); // delay 2400 ticks, (bef start 15 sec+60+45), jump to 15 sec bef end

        if (!joinerList.isEmpty())
            Bukkit.getScheduler().runTaskLater(plugin, this::end, 2700);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        if (!joinerList.containsKey(victim)) return;

        victim.teleport(joinerList.get(victim));
        plugin.getSilent().remove(victim);
        joinerList.remove(victim);
        plugin.broadcast(plugin.prefix + ChatColor.RED + victim.getName() + " 在勇者之門裏陣亡了……");

        if (joinerList.size() == 1) end();
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {

        if (endingStage == -1) return;

        if (
                event.getDamager() instanceof Player damager &&
                event.getEntity() instanceof Player victim &&
                (
                    joinerList.containsKey(damager) ||
                    joinerList.containsKey(victim)
                )
        ) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (endingStage == -1) return;
        Player player = event.getPlayer();
        if (joinerList.containsKey(player)) {
            player.teleport(joinerList.get(player));
            joinerList.remove(player);
            plugin.getSilent().remove(player);
            joinerList.keySet().forEach((p) -> p.sendMessage(plugin.prefix + ChatColor.RED + player.getName() + " 退出了游戲，離開了勇者之門。需支付 10 積分作爲早退賠償金"));

            try {
                plugin.getDatabase().addScore(player.getUniqueId(), -10);
            } catch (SQLException e) {
                plugin.getLogger().severe("Error while deducting player's point (Deduct point when player left gate of the brace)");
                e.printStackTrace();
            }

            if (joinerList.size() == 1) end();
        }
    }

    private void end() {
        endingStage = 0;
        new BukkitRunnable() {
            @Override
            public void run() {

                cooldown = System.currentTimeMillis() + YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "strategic-event.yml")).getInt("gate-of-the-brave.cooldown");

                if (joinerList.isEmpty()) {
                    cancel();
                    return;
                }

                switch (endingStage) {
                    case 0 -> joinerList.keySet().forEach(p -> {
                        p.sendMessage(plugin.prefix + Utilities.colorize("&6勇者之門將在10秒後關閉，請撿起地上的掉落物。"));
                        p.playSound(p, Sound.ITEM_GOAT_HORN_SOUND_0, 10F, 0.5F);
                    });
                    case 1 -> {
                        joinerList.forEach(Entity::teleport);
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&3&l### &7&k芷 &b&l勇者之門 &b關閉了。 &7&k妤 &3&l###"));
                        Bukkit.getOnlinePlayers().forEach((p) -> p.playSound(p, Sound.ITEM_GOAT_HORN_SOUND_7, 10F, 0.5F));
                    }
                    case 2 -> {
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&6||| &7----&f幸存者名單&7---- &6|||"));
                        for (int i = 0; i < joinerList.size(); i++) {
                            plugin.broadcast(plugin.prefix + (i + 1) + ". " + new ArrayList<>(joinerList.keySet()).get(i).getName());
                        }
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&6||| &7---------------- &6|||"));
                    }
                    case 3 -> {
                        int amount = Math.round((float) totalPrize / joinerList.size());
                        plugin.broadcast(plugin.prefix + Utilities.colorize("&a以上玩家將獲得獎勵：&e" + amount + " 積分"));
                        joinerList.keySet().forEach((player) -> {
                            try {
                                plugin.getDatabase().addScore(player.getUniqueId(), amount);
                            } catch (SQLException e) {
                                player.sendMessage(plugin.prefix + ChatColor.RED + "發生嚴重錯誤，請立即通報管理員。");
                                plugin.getLogger().severe("Error while adding point for " + player);
                                e.printStackTrace();
                            }
                            plugin.getSilent().remove(player);
                        });
                        joinerList.clear();
                        cancel();
                        endingStage = -1;
                    }
                    default -> {
                        joinerList.clear();
                        cancel();
                        endingStage = -1;
                    }
                }
                endingStage++;
            }
        }.runTaskTimer(plugin, 0, 200);
    }
}
