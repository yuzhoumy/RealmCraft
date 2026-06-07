package quest.yuzhou.realmcraft.misc.biomeevent;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import quest.yuzhou.realmcraft.RealmCraft;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomePotionEffect implements Listener {

    private final YamlConfiguration config;
    private final Map<String, Map<Biome, List<PotionEffectType>>> worldBiomeEffects = new HashMap<>();

    public BiomePotionEffect(RealmCraft plugin) {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "biome-potion-effect.yml"));

        ConfigurationSection worldsSection = config.getConfigurationSection("biome-effects");
        if (worldsSection != null) {
            for (String worldName : worldsSection.getKeys(false)) {
                ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
                Map<Biome, List<PotionEffectType>> biomeEffects = new HashMap<>();

                if (worldSection != null) {
                    for (String biomeKey : worldSection.getKeys(false)) {

                        Biome biome = null;
                        try {
                            biome = Biome.valueOf(biomeKey.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().info("Error while loading biome '" + biomeKey + "' in world '" + worldName + "'");
                            e.printStackTrace();
                        }

                        List<String> effectNames = worldSection.getStringList(biomeKey);
                        List<PotionEffectType> effects = effectNames.stream()
                                .map(PotionEffectType::getByName)
                                .toList();
                        biomeEffects.put(biome, effects);
                    }
                }
                worldBiomeEffects.put(worldName, biomeEffects);
            }
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        handle(e);
    }

    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent e) {
        handle(e);
    }

    private void handle(PlayerMoveEvent e) {
        Biome previousBiome = e.getFrom().getBlock().getBiome();
        Biome currentBiome = e.getTo().getBlock().getBiome();

        if (previousBiome.equals(currentBiome))
            return;

        Player player = e.getPlayer();
        World world = player.getWorld();

        Map<Biome, List<PotionEffectType>> biomeEffects = worldBiomeEffects.get(world.getName());
        if (biomeEffects == null)
            return;

        // remove effect
        if (biomeEffects.containsKey(previousBiome))
            biomeEffects.get(previousBiome).forEach(player::removePotionEffect);

        // add effect
        if (biomeEffects.containsKey(currentBiome)) {
            biomeEffects.get(currentBiome).forEach(
                    potionEffectType -> player.addPotionEffect(
                            new PotionEffect(potionEffectType, Integer.MAX_VALUE, 255, false, false)
                    )
            );
            String sound = config.getString("enter-biome-sound");
            assert sound != null;
            player.playSound(player.getLocation(), sound, 10F, 0.5F);
        }
    }

}
