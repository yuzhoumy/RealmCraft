package quest.yuzhou.realmcraft.misc.newbie;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;

public class NewbieIntroduction {

    private final RealmCraft plugin;

    public NewbieIntroduction(RealmCraft plugin) {
        this.plugin = plugin;
    }

    public void run(Player player) {
        startDialogue(player, 0);
    }

    private void startDialogue(Player player, int step) {
        String[] messages = {
                "",
                // step 1
                "&e&l-> &a在那遙遠的過去，人類曾擁有征服星辰的夢想……",
                "&e&l-> &a科技迅速發展，星際航行成爲現實，許多人都開始往其他星球移民。",
                "&e&l-> &a那是一個榮耀璀璨的時代，物質極度豐盈，卻也悄然奪走了人心的溫度。",
                "&e&l-> &a那也是個墮落與迷失的時代，人們逐漸在沉淪中迷失了自我。",
                "&e&l-> &a與此同時，犯罪分子也因無盡的欲望越來越猖獗。",
                // step 6
                "&e&l-> &a眼看社會秩序走向崩壞，連聯合國也不得不推出那些極端的救市措施……",
                "&f聯合國秘書長-薩伊： 爲了挽救人類的生育率，聯合國通過8137號決議：降低性同意年齡，撤銷與之的有關所有罪行",
                "&e&l-> &a但極端只會引發更大的混亂，人類社會也從此變成了一盤散沙。",
                "&e&l-> &a各個星球的政府與勢力，因資源與權力而彼此對立。很快地，戰火燃遍了整個宇宙。",
                "&e&l-> &a其中有一顆星球，代號『ACP-7945』，擁有豐富的礦產資源。各個星球的人紛紛都來這裏開礦。",
                // step 11
                "&e&l-> &a經歷了數百年的混戰，曾經被視作兵家必爭之地的代號『ACP-7945』星球，礦物早已被挖完，如今已是廢墟。",
                "&e&l-> &a在很長的一段時間裏，這塊土地成了無人管轄的三不管之地。人們只管殺戮，尸橫遍野。",
                "&e&l-> &a許多來自其他星球的犯人被流放到這裏，因爲這裏實在是難以生存，寸草不生。",
                "&e&l-> &a他們靠著堅强的意志，在惡劣的環境中生存。",
                "&e&l-> &a因爲各種原因移民過來的人越來越多…… 其中一群人在一個平原建設了一座城堡，建立了一個政府，旨在幫助新移民。",
                // step 16
                "&e&l-> &a這些從其他星球流放過來的人，科學家、技術人才不在少數。不少都在政府内工作，让政府迅速發展爲這個星球上最有影響力的勢力。",
                "&e&l-> &a不僅如此，政府手下還擁有兩隻裝備精良、武藝高超的騎士團。",
                "&e&l-> &a除了政府以外，也有幾個不歸依于政府的勢力：",
                "&b“土著”&f:\n最早一批來到這個星球的人類，并且定居下來。生活區受到“政府”與“拾荒者”的擠壓，對外人抱有强烈敵意。",
                "&b“異界”&f:\n一群以殺戮和掠奪爲生的不明生物，沒有人知道他們從哪裏來，要到哪裏去。",
                // step 21
                "&b“拾荒者”&f:\n這是組成這個星球人口最大的群體，他們不受政府青睞，不受政府管轄，因此魚龍混雜。搜刮、搶劫、殺戮是他們唯一的活命路。",
                "&e&l-> &a這個星球的資源與其他星球的不一樣，更加難以開采，且需要特殊技術進行加工。",
                "&e&l-> &b“土著”&a發展出了自己的加工方法，能夠激發材料裏的能量。（裝備以魔法效果爲主。只能處理木材。",
                "&e&l-> &b“政府”&a保留了土著的一部分技術，能夠進行更進階的加工，發揮出更强力的效果。（裝備以强力buff爲主。能夠處理石材，煉鐵。）",
                "&e&l-> &b“拾荒者”&a則沒有任何技術。（裝備都是混合型，自己DIY。物資需要與政府購買，或者野外搜刮。）",
                // step 26
                "",
                "&e【政府人員】&a：我是政府的工作人員。政府不會包養你，在這個星球上也沒有人會包養你。但作爲政府對新移民的輔助，我將教你如何在這個星球上生存。"
        };
        Runnable[] actions = {
                () -> {
                    player.setGameMode(GameMode.SPECTATOR);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab scoreboard off " + player.getName());
                    player.teleport(new Location(plugin.fieldWorld, -417, -59, 887));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "speed " + player.getName() + " 0");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pweather sun " + player.getName());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 3, false, false, false));
                    player.playSound(player, Sound.BLOCK_PORTAL_TRIGGER, 10 ,1);
                    player.playSound(player, "realmcraft:intro", 10, 1);
                },
                // step 1
                () -> spinPlayer(player),
                () -> {
                },
                () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime 5:00am " + player.getName());
                    player.teleport(new Location(plugin.fieldWorld, -497, 19, 880, -90, 0));
                },
                () -> movePlayer(player),
                () -> {
                },
                // step 6
                () -> player.teleport(new Location(plugin.fieldWorld, -458, -16, 889, 90, 0)),
                () -> {
                },
                () -> player.teleport(new Location(plugin.fieldWorld, -511, 19, 896, 90, 0)),
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -570, -47, 902));
                    displayBoomParticle(player);
                },
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -440, 10, 960));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime day " + player.getName());
                },
                // step 11
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -524, 16, 915));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime 6:45pm " + player.getName());
                },
                () -> player.teleport(new Location(plugin.fieldWorld, -445, 18, 925, 45, 0)),
                () -> player.teleport(new Location(plugin.fieldWorld, -572, 20, 938, 90, 0)),
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -600, 20, 925, -40, 0));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime day " + player.getName());
                },
                () -> player.teleport(new Location(plugin.fieldWorld, -381, -52, 962, -90, 0)),
                // step 16
                () -> player.teleport(new Location(plugin.fieldWorld, -410,-59,901, 54, 0)),
                () -> player.teleport(new Location(plugin.fieldWorld, -417, -59, 887)),
                () -> {
                },
                () -> player.teleport(new Location(plugin.fieldWorld, -419, -15, 895, -90, 0)),
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -420, -8, 828, -90, 20));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime night " + player.getName());
                },
                // step 21
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -498, 20, 847, 120, 0));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime day " + player.getName());
                },
                () -> player.teleport(new Location(plugin.fieldWorld, -394, -38, 823, 50, 0)),
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -416, -15, 890, -90, 30));
                    displayMagicParticle(player);
                },
                () -> player.teleport(new Location(plugin.fieldWorld, -328, -53, 959, -45, 0)),
                () -> player.teleport(new Location(plugin.fieldWorld, -512, 20, 856, 110, 0)),
                // step 26
                () -> {
                    player.teleport(new Location(plugin.fieldWorld, -417, -59, 887));
                    player.sendTitle("", "你是一名拾荒者，被稱呼為 " + player.getName(), 20, 80, 20);
                },
                () -> end(player)
        };
        long[] delays = {
                60L,
                180L, // step 1
                180L,
                120L,
                120L,
                120L,
                120L, // step 6
                80L,
                180L,
                180L,
                180L,
                180L, // step 11
                180L,
                180L,
                180L,
                180L,
                180L, // step 16
                180L,
                180L,
                200L,
                200L,
                200L, // step 21
                200L,
                200L,
                200L,
                200L,
                150L, // step 26
                0L
        };

        if (step >= messages.length) return;

        //debug msg
        if (plugin.isDebugOn())
            plugin.getLogger().info("[DEBUG] newbie-intro-step: " + step);

        if (!messages[step].equalsIgnoreCase(""))
            player.sendMessage(Utilities.colorize(messages[step]));

        actions[step].run();

        if (player.isOnline()) {
            long delay = delays[step];
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> startDialogue(player, step + 1), delay);
        }
    }

    private void spinPlayer(Player player) {

        Location origin = new Location(plugin.fieldWorld, -501, -40, 872);

        new BukkitRunnable() {

            double degree = -90;

            @Override
            public void run() {
                Utilities.pinPlayer(player, origin, 20, degree);
                degree += 1;

                if (degree == 90) cancel();
            }
        }.runTaskTimer(plugin, 0L, 2L);

    }

    private void movePlayer(Player player) {
        Location origin = new Location(plugin.fieldWorld, -492, 39, 872, 0, 0);

        double xOffset = -486;

        new BukkitRunnable() {

            @Override
            public void run() {
                player.teleport(origin);
                origin.setX(origin.getX() + 0.025);
                if (Math.abs(origin.getX() - xOffset) < 0.1) cancel();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void displayBoomParticle(Player player) {

        Location[][] particleLocations = {
                {
                        new Location(plugin.fieldWorld, -573, -46, 924),
                        new Location(plugin.fieldWorld, -584, -46, 926),
                        new Location(plugin.fieldWorld, -531, -38, 927),
                        new Location(plugin.fieldWorld, -595, -44, 942),
                        new Location(plugin.fieldWorld, -579, -46, 939),
                },

                {
                        new Location(plugin.fieldWorld, -574, -45, 925),
                        new Location(plugin.fieldWorld, -592, -41, 930),
                        new Location(plugin.fieldWorld, -588, -40, 935),

                },

                {
                        new Location(plugin.fieldWorld, -576, -37, 926),
                        new Location(plugin.fieldWorld, -591, -41, 934),
                        new Location(plugin.fieldWorld, -589, -47, 926),
                        new Location(plugin.fieldWorld, -595, -44, 931)
                }
        };

        new BukkitRunnable() {

            int step = 0;

            @Override
            public void run() {
                if (step >= particleLocations.length) return;

                for (Location location : particleLocations[step]) {
                    player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 10, 1);
                    player.spawnParticle(Particle.EXPLOSION_HUGE, location, 10);
                }

                step++;
            }
        }.runTaskTimer(plugin, 40L, 20L);
    }

    private void displayMagicParticle(Player player) {
        new BukkitRunnable() {

            int step = 0;

            @Override
            public void run() {
                if (step == 10) cancel();
                player.spawnParticle(Particle.CLOUD, new Location(plugin.fieldWorld, -415, -15, 890), 30);
                step++;
            }
        }.runTaskTimer(plugin, 0, 15);
    }

    private void end(Player player) {
        player.teleport(new Location(plugin.fieldWorld, -463, -59, 786, 180, 0));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pweather reset " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ptime reset " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "speed " + player.getName() + " 1");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tab scoreboard on " + player.getName());
        player.setGameMode(GameMode.SURVIVAL);
    }

}
