package quest.yuzhou.realmcraft.misc.biomeevent;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import quest.yuzhou.realmcraft.RealmCraft;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class MusicPlayer implements Listener {

    private final RealmCraft plugin;
    private final List<Player> muted;
    private final HashMap<Player, Long> nextMusicPlayTime;
    private final List<String> musicList;
    private final float volume;
    private final BukkitTask task;

    public MusicPlayer(RealmCraft plugin) {
        this.plugin = plugin;
        this.muted = new ArrayList<>();
        this.nextMusicPlayTime = new HashMap<>();

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "music.yml"));

        this.volume = (float) yamlConfiguration.getDouble("volume");
        this.musicList = yamlConfiguration.getStringList("musicList");
        long interval = yamlConfiguration.getLong("interval");

        Bukkit.getOnlinePlayers().forEach(player -> nextMusicPlayTime.put(player, 0L));

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Long> entry : nextMusicPlayTime.entrySet()) {
                    if (entry.getValue() > System.currentTimeMillis()) continue;
                    if (muted.contains(entry.getKey())) continue;

                    int hasPassedIntroStage = 0;

                    try {
                        hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(entry.getKey().getUniqueId());
                    } catch (SQLException e) {
                        plugin.getLogger().severe("Error while getting player's has passed intro stage: " + entry.getKey());
                        e.printStackTrace();
                    }

                    if (hasPassedIntroStage == 0) continue;

                    String[] song = musicList.get(new Random().nextInt(musicList.size())).split("-");
                    entry.getKey().playSound(entry.getKey(), song[0], volume, 1);
                    nextMusicPlayTime.replace(entry.getKey(), System.currentTimeMillis() + (Integer.parseInt(song[1]) * 1000L));

                }
            }
        }.runTaskTimer(plugin, 1200, interval * 20);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            if (plugin.getDatabase().getPlayerHasPassedIntroStage(player.getUniqueId()) == 0) {
                nextMusicPlayTime.put(player, System.currentTimeMillis() + (300 * 1000L));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error while getting player's has passed intro stage : " + player);
        }
        nextMusicPlayTime.put(player, System.currentTimeMillis() + (60 * 1000));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        nextMusicPlayTime.remove(event.getPlayer());
    }

    public boolean isMuted(Player player) {
        return muted.contains(player);
    }

    public void mute(Player player) {
        muted.add(player);
        nextMusicPlayTime.replace(player, 0L);
        player.stopAllSounds();
    }

    public void unmute(Player player) {
        muted.remove(player);
    }

    public void stop() {
        nextMusicPlayTime.forEach((player, nextAvailableTime) -> player.stopAllSounds());
        task.cancel();
    }
}
