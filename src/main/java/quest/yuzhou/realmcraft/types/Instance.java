package quest.yuzhou.realmcraft.types;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.DespawnMode;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;

import java.util.*;

public final class Instance implements Listener {

    RealmCraft plugin;
    boolean active;
    String name;
    String displayName;
    String bossType;
    String bossName;
    int maxBroadcastDistance;
    int wave = 1;
    int waveAmount;
    int maxWaveTime;
    int intervalBetweenWaves;
    int cooldown;
    int prize;
    int penalty;
    long nextAvailableTime = 0;
    Location buttonLocation;
    Location bossSpawnPoint;
    AbstractLocation abstractBossSpawnPoint;
    ActiveMob boss;
    Map<Player, Double> participantMap;

    public Instance(RealmCraft plugin, String name, String displayName, String bossType, int maxBroadcastDistance, int waveAmount, int maxWaveTime, int intervalBetweenWaves, int cooldown, int prize, int penalty, Location buttonLocation, Location bossSpawnPoint) {
        this.plugin = plugin;
        this.active = false;
        this.name = name;
        this.displayName = displayName;
        this.bossType = bossType;
        this.maxBroadcastDistance = maxBroadcastDistance;
        this.waveAmount = waveAmount;
        this.maxWaveTime = maxWaveTime;
        this.intervalBetweenWaves = intervalBetweenWaves;
        this.cooldown = cooldown;
        this.prize = prize;
        this.penalty = penalty;
        this.buttonLocation = buttonLocation;
        this.bossSpawnPoint = bossSpawnPoint;
        this.abstractBossSpawnPoint = new AbstractLocation(
                bossSpawnPoint.getWorld().getName(),
                bossSpawnPoint.getBlockX(),
                bossSpawnPoint.getBlockY(),
                bossSpawnPoint.getBlockZ()
        );
        this.participantMap = new HashMap<>();

        if (MythicBukkit.inst().getMobManager().getMythicMob(bossType).isPresent())
            bossName = MythicBukkit.inst().getMobManager().getMythicMob(bossType).get().getDisplayName().get();
        else
            plugin.getLogger().severe("Boss in instance " + name + " is invalid, perhaps mob type is invalid, please check instance.yml");
    }

    @EventHandler
    public void onPlayerClickButton(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (event.getClickedBlock().getType() != Material.STONE_BUTTON) return;

        Player player = event.getPlayer();
        Location location = event.getClickedBlock().getLocation();
            if (
                    location.getWorld() == buttonLocation.getWorld() &&
                    location.getBlockX() == buttonLocation.getBlockX() &&
                    location.getBlockY() == buttonLocation.getBlockY() &&
                    location.getBlockZ() == buttonLocation.getBlockZ()
            ) {

                if (plugin.getRestarter().isPreparingRestart()) {
                    player.sendMessage(displayName + ChatColor.RED + " 伺服器快重啓了…… 暫時不允許開始副本。");
                } else if (nextAvailableTime > System.currentTimeMillis()) {
                    player.sendMessage(displayName + Utilities.colorize(" &c這個副本的頭目仍在冷卻，請等 &a" + (double) Math.round((nextAvailableTime - System.currentTimeMillis()) / 1000.0 / 60.0 * 10) / 10 + " &c分鐘。"));
                } else if (!active) {
                    Bukkit.getOnlinePlayers().forEach((p) -> player.playSound(p, Sound.ENTITY_WITHER_SPAWN, 10f, 0.5f));
                    this.broadcastNearby(Utilities.colorize("&6此副本的頭目怪物 " + bossName + " &6將在 &a1 &6分鐘後苏醒！想要挑戰的玩家請來到 " + displayName + " 。你們將持續一場共有 &f" + waveAmount + " &6波的惡戰！"));
                    this.broadcastNearby(Utilities.colorize("&6每一波内，你必須在 &a" + maxWaveTime / 60 + " &6分鐘内擊敗頭目"));
                    wave = 1;
                    active = true;

                    new BukkitRunnable() {

                        int countdown = 50; // 10 sec has been delayed

                        @Override
                        public void run() {

                            broadcastNearby(Utilities.colorize("&6還剩 &b" + countdown + " &6秒，頭目就要蘇醒！"));

                            if (countdown == 10) {
                                cancel();
                                return;
                            }

                            countdown -= 10;
                        }
                    }.runTaskTimer(plugin, 10 * 20, 10 * 20);

                    Bukkit.getScheduler().runTaskLater(plugin, this::startWave, 50 * 20);
                } else {
                    player.sendMessage(displayName + Utilities.colorize(" &c這個副本的頭目已經正在與玩家火熱對峙中！等這一局結束後，等 &a" + (double) Math.round(cooldown / 60.0) + " &c分鐘再來吧。"));
                }
            }
    }

    @EventHandler
    public void onBossDeath(MythicMobDeathEvent event) {
        if (!active || boss == null) return;
        if (event.getMob().getUniqueId().equals(boss.getUniqueId())) {
            if (event.getKiller() instanceof Player killer) {
                this.broadcastNearby(Utilities.colorize(boss.getDisplayName() + " &e被&b " + killer.getDisplayName() + " &e擊敗了。"));
                if (wave < waveAmount) {
                    broadcastNearby(new String[]{
                            Utilities.colorize("&b第&a " + wave + " &b波已結束！"),
                            Utilities.colorize("&b第&a " + (wave + 1) + " &b波將在&a " + (intervalBetweenWaves + 10) + " &b秒后開始……")
                    });

                    wave++;

                    Bukkit.getScheduler().runTaskLater(plugin, this::startWave, intervalBetweenWaves * 20L);
                } else {
                    nextAvailableTime = System.currentTimeMillis() + (cooldown * 1000L);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.broadcast(displayName + ChatColor.GREEN + " 此副本的冷卻時間已結束，玩家們現可前來挑戰。"), cooldown * 20L);
                    boss = null;
                    active = false;

                    broadcastNearby(ChatColor.YELLOW + "你們成功打敗頭目了！");
                    broadcastParticipant(new String[]{
                            ChatColor.BOLD + "= - = - = - = - = - =",
                            ChatColor.GOLD + "· · · · · · · 結算 · · · · · · ·",
                            ChatColor.BOLD + "= - = - = - = - = - =",
                    });

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        broadcastParticipant(printContributionRateRanking(0));
                        participantMap.forEach(((player, damage) -> {

                            double contributionRate = getContributionRate(player);
                            int prizeAmount = (int) Math.round((double) prize * contributionRate);
                            player.giveExp(prizeAmount);

                            player.sendMessage(Utilities.colorize("&c&l在這場激戰中，你總共對 " + bossName + "&c&l 輸出了 &a" + Utilities.roundTwoDecimalPlaces(damage) + " &c&l點的傷害，傷害貢獻率為 &a" + contributionRate * 100 + "% &c&l。"));
                            player.sendMessage(Utilities.colorize("&c&l按照規則，你將會從獎池中（總共 " + prize + " 經驗值）獲得 &a" + contributionRate * 100 + "% &c&l的獎勵。"));
                            player.sendMessage(Utilities.colorize("&c&l你獲得了 &b" + prizeAmount + " &c&l 經驗值！"));

                        }));
                        Bukkit.getOnlinePlayers().forEach((player) -> player.sendMessage(displayName + Utilities.colorize(" &b頭目被打敗了！此副本進入 &a" + (cooldown / 60) + " &b分鐘冷卻時間")));
                        participantMap.clear();
                    }, 100);
                }
            } else {
                getNearbyPlayers().forEach((player) -> player.playSound(player, Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 10F, 0.5F));
                broadcastNearby(new String[]{
                        bossName + ChatColor.RED +" 意外死亡了！本次戰鬥不會發放給予獎勵或懲罰。",
                        ChatColor.RED + "由於是意外死亡，只需等待4分鐘冷卻時間。"
                });
                nextAvailableTime = System.currentTimeMillis() + 4 * 60 * 1000L;
                boss = null;
                active = false;
                participantMap.clear();
            }
        }
    }

    @EventHandler
    public void onBossDamaged(EntityDamageByEntityEvent event) {
        if (!active || boss == null) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        Player damager = getPlayerDamager(event.getDamager());

        if (damager == null) return;

        try {
            ActiveMob activeMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
            if (activeMob == null || !activeMob.getUniqueId().equals(boss.getUniqueId())) return;

            double damageAmount = event.getFinalDamage();

            if (participantMap.containsKey(damager)) {
                participantMap.replace(damager, participantMap.get(damager) + damageAmount);
            } else {
                participantMap.put(damager, damageAmount);
                broadcastParticipant(Utilities.colorize("&e" + damager.getName() + " &6加入了戰鬥！目前總共有 &f" + participantMap.size() + " &6名參與者"));
            }


        } catch (Exception e) {
            plugin.getLogger().warning("Error processing damage event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!participantMap.containsKey(event.getEntity())) return;

        Player victim = event.getEntity();

        double damageAmount = participantMap.get(victim);
        participantMap.remove(victim);
        participantMap.replaceAll((p, damage) -> damage + damageAmount);

        broadcastParticipant(new String[]{
                ChatColor.RED + victim.getDisplayName() + " 在戰鬥中身亡了！他所貢獻的傷害將會平分給所有參與者！",
                Utilities.colorize("&f&l你已獲得 &a" + damageAmount + " &f&l傷害貢獻")
        });
        participantMap.keySet().forEach(player -> player.sendMessage(displayName + Utilities.colorize("&f&l你目前的傷害貢獻率是 &a" + getContributionRate(player) * 100)));
    }

    @EventHandler
    public void onBossDespawn(MythicMobDespawnEvent event) {
        if (!active || boss == null) return;
        if (plugin.isDebugOn()) {
            if (event.getMob().getUniqueId().equals(boss.getUniqueId())) {
                plugin.getLogger().info(boss.getDisplayName() + " is despawned!");
            }
        }
    }

    private List<Player> getNearbyPlayers() {
        return bossSpawnPoint.getWorld().getPlayers().stream().filter(player -> player.getLocation().distance(bossSpawnPoint) < maxBroadcastDistance).toList();
    }

    private void broadcastParticipant(String message) {
        participantMap.keySet().forEach((player -> {
            player.sendMessage(displayName + " " + message);
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 10f, 0.5f);
        }));
    }

    private void broadcastParticipant(String[] message) {
        participantMap.keySet().forEach((player -> {
            for (String sentence : message) {
                player.sendMessage(displayName + " " + sentence);
            }
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 10f, 0.5f);
        }));
    }

    private void broadcastNearby(String message) {
        getNearbyPlayers().forEach((player -> {
            player.sendMessage(displayName + " " + message);
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 10f, 0.5f);
        }));
    }

    private void broadcastNearby(String[] message) {
        getNearbyPlayers().forEach((player -> {
            for (String sentence : message) {
                player.sendMessage(displayName + " " + sentence);
            }
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 10f, 0.5f);
        }));
    }

    private Player getPlayerDamager(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }

        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }

        if (damager instanceof TNTPrimed) {
            Entity source = ((TNTPrimed) damager).getSource();
            if (source instanceof Player) {
                return (Player) source;
            }
        }

        if (damager instanceof AreaEffectCloud) {
            ProjectileSource source = ((AreaEffectCloud) damager).getSource();
            if (source instanceof Player) {
                return (Player) source;
            }
        }

        if (damager instanceof Tameable) {
            AnimalTamer owner = ((Tameable) damager).getOwner();
            if (owner instanceof Player) {
                return (Player) owner;
            }
        }

        return null;
    }

    private double getContributionRate(Player player) {
        for (Map.Entry<Player, Double> entry : participantMap.entrySet()) {
            if (entry.getKey() == player) {
                return Utilities.roundTwoDecimalPlaces(entry.getValue() / participantMap.values().stream().mapToDouble(Double::doubleValue).sum());
            }
        }
        throw new IllegalStateException("Player " + player.getName() + " have 0 damage contribution");
    }

    private String[] printContributionRateRanking(int length) {
        List<Map.Entry<Player, Double>> entryList = new ArrayList<>(participantMap.entrySet());
        entryList.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        List<String> result = new ArrayList<>();

        if (length == 0) length = entryList.size();

        for (int i = 0; i < Math.min(length, entryList.size()); i++) {
            double rate = (entryList.get(i).getValue() / participantMap.values().stream().mapToDouble(Double::doubleValue).sum()) * 100;
            double rounded = Utilities.roundTwoDecimalPlaces(rate);
            result.add(Utilities.colorize("&e" + (i + 1) + ". &6" +
                    entryList.get(i).getKey().getDisplayName() + " &e- &b" + rounded + "%"));
        }

        result.add(0, ChatColor.YELLOW + "= - - - 貢獻率排行榜 - - - =");
        return result.toArray(new String[0]);
    }


    private void startWave() {
        int currentWave = wave;
        this.broadcastNearby(Utilities.colorize("&6第 &a" + wave + " &6波即將開始！請待命作戰的玩家們做好準備."));
        final int[] countdown = {10};
        new BukkitRunnable() {

            @Override
            public void run() {
                getNearbyPlayers().forEach(player -> {
                    player.sendTitle(ChatColor.GOLD + Integer.toString(countdown[0]), Utilities.colorize("&e請在 &a" + maxWaveTime / 60 + " &e分鐘内擊殺名爲 " + bossName + " &e的頭目。"), 0, 20, 0);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 10, 2);
                });
                if (countdown[0] < 1) {
                    cancel();
                    getNearbyPlayers().forEach(player -> {
                        player.sendTitle(ChatColor.BLUE + "第 " + wave + " 波已开始", "", 0, 20, 10);
                        player.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 10f, 0.5f);
                        player.sendMessage(Utilities.colorize("&6頭目 " + bossName + " &6復活了。"));
                    });
                    boss = MythicBukkit.inst().getMobManager().spawnMob(bossType, bossSpawnPoint);
                    boss.setDespawnMode(DespawnMode.NEVER);

                }
                countdown[0]--;
            }
        }.runTaskTimer(plugin, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!active) {
                    cancel();
                    return;
                }
                if (currentWave != wave) {
                    cancel();
                    return;
                }

                if (boss.getLocation().distance(abstractBossSpawnPoint) > maxBroadcastDistance) {
                    boss.getEntity().teleport(abstractBossSpawnPoint);
                    Instance.this.broadcastParticipant(bossName + " 遠離出生點 " + maxBroadcastDistance + " 格以外，已被傳送至出生點");
                }

            }
        }.runTaskTimer(plugin, 600,200);

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!active) {
                    cancel();
                    return;
                }
                if (currentWave != wave) {
                    cancel();
                    return;
                }

                participantMap.keySet().forEach((player -> {
                    player.playSound(player, Sound.ITEM_LODESTONE_COMPASS_LOCK, 10, 1);
                    player.sendMessage(displayName + ChatColor.GREEN + " 你目前總共輸出了 " + Utilities.roundTwoDecimalPlaces(participantMap.get(player)) + " 攻擊傷害，攻擊貢獻率為 " + Utilities.roundTwoDecimalPlaces(getContributionRate(player)) * 100 + "%");
                    player.sendMessage(displayName + ChatColor.YELLOW + " 實時排行榜：");
                }));
                broadcastParticipant(printContributionRateRanking(5));

            }
        }.runTaskTimer(plugin, 600, 600);

        new BukkitRunnable() {

            int countdown = maxWaveTime - 10;

            @Override
            public void run() {

                if (!active) {
                    cancel();
                    return;
                }
                if (boss.isDead() || countdown <= 10) {
                    cancel();
                    return;
                }
                if (currentWave != wave) {
                    cancel();
                    return;
                }

                participantMap.keySet().forEach(player -> {
                    player.sendTitle("", "還剩 " + countdown + " 秒", 0, 15, 0);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 10, 2);
                });
                countdown -= 10;
            }

        }.runTaskTimer(plugin, 20 * 20, 20 * 10); // 10s

        new BukkitRunnable() {

            int countdown = 10;

            @Override
            public void run() {

                if (!active) {
                    cancel();
                    return;
                }

                if (boss.isDead() || countdown <= 0) {
                    cancel();
                    return;
                }
                if (currentWave != wave) {
                    cancel();
                    return;
                }

                participantMap.keySet().forEach(player -> {
                    player.sendTitle("", "還剩 " + countdown + " 秒", 0, 20, 0);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 10, 2);
                });
                countdown--;
            }
        }.runTaskTimer(plugin, (maxWaveTime - 10) * 20L, 20); // last 10 sec bef end

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!active) {
                return;
            }
            if (currentWave != wave)
                return;
            if (boss == null) {
                this.broadcastNearby(ChatColor.RED + "出現致命錯誤，請立即通知管理員");
                plugin.getLogger().severe("Boss in instance " + name + " is null.");
            }
            if (!boss.isDead()) {
                participantMap.keySet().forEach((player -> {
                    player.sendTitle(ChatColor.RED + "挑戰失敗！", "你們成功堅持到了第 " + wave + " 波。", 10, 20, 10);
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 1);
                }));

                nextAvailableTime = System.currentTimeMillis() + (cooldown * 1000L);
                boss.despawn();
                boss.remove();
                boss = null;
                active = false;
                Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.broadcast(displayName + ChatColor.GREEN + " 此副本的冷卻時間已結束，玩家們現可前來挑戰。"), cooldown * 20L);

                broadcastNearby(Utilities.colorize("&c時間到了，由於未在指定時間内擊敗頭目 " + bossName + " &c，該怪物已被自動銷毀。"));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    broadcastParticipant(printContributionRateRanking(0));
                    participantMap.forEach(((player, damage) -> {

                        double contributionRate = getContributionRate(player);
                        int penaltyAmount = (int) Math.round((double) penalty * contributionRate);
                        if (penaltyAmount >= plugin.getEconomy().getBalance(player))
                            penaltyAmount = (int) Math.floor(plugin.getEconomy().getBalance(player));
                        else
                            plugin.getEconomy().withdrawPlayer(player, penaltyAmount);


                        player.sendMessage(Utilities.colorize("&c&l在這場激戰中，你總共對 " + bossName + "&c&l 輸出了 &a" + Utilities.roundTwoDecimalPlaces(damage) + " &c&l點的傷害，傷害貢獻率為 &a" + contributionRate * 100 + "% &c&l。"));
                        player.sendMessage(Utilities.colorize("&c&l按照規則，你的銀行將會被扣除：惩罚池（總共 $" + penalty + "）的 &a" + contributionRate * 100 + "% &c&l的金錢。"));
                        player.sendMessage(Utilities.colorize("&c&l你被扣除了 &b$" + penaltyAmount + " &c&l 金錢！"));

                    }));
                    Bukkit.getOnlinePlayers().forEach((player) -> player.sendMessage(displayName + Utilities.colorize(" &c擊殺頭目任務失敗，此副本進入 &a" + (cooldown / 60) + " &c分鐘冷卻時間")));
                    participantMap.clear();
                }, 100);
            }
        }, maxWaveTime * 20L);
    }

    public void resetCooldown() {
        nextAvailableTime = System.currentTimeMillis();
    }

}
