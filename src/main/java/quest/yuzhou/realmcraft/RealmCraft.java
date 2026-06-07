package quest.yuzhou.realmcraft;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import quest.yuzhou.rcchest.RCChest;
import quest.yuzhou.realmcraft.command.AdminCommandManager;
import quest.yuzhou.realmcraft.command.AdminCommandTabCompleter;
import quest.yuzhou.realmcraft.command.CommandManager;
import quest.yuzhou.realmcraft.command.MainCommandTabCompleter;
import quest.yuzhou.realmcraft.database.Database;
import quest.yuzhou.realmcraft.misc.PreventPlayerBreakHanging;
import quest.yuzhou.realmcraft.misc.PreventPutBackPackInsideBackPack;
import quest.yuzhou.realmcraft.misc.afkarea.AFKArea;
import quest.yuzhou.realmcraft.misc.biomeevent.BiomePotionEffect;
import quest.yuzhou.realmcraft.misc.chestunlocker.ChestUnlocker;
import quest.yuzhou.realmcraft.misc.emergencypack.EmergencyPackChestListener;
import quest.yuzhou.realmcraft.misc.instance.InstanceManager;
import quest.yuzhou.realmcraft.misc.biomeevent.MusicPlayer;
import quest.yuzhou.realmcraft.misc.newbie.NewbieIntroduction;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuestManager;
import quest.yuzhou.realmcraft.misc.newbie.listener.ChoicePressurePlate;
import quest.yuzhou.realmcraft.misc.recyclestation.RecycleStation;
import quest.yuzhou.realmcraft.misc.restart.Restarter;
import quest.yuzhou.realmcraft.misc.kit.KitChestListener;
import quest.yuzhou.realmcraft.misc.newbie.listener.NewbieStartTutorial;
import quest.yuzhou.realmcraft.misc.newbie.listener.NewbieStartIntro;
import quest.yuzhou.realmcraft.misc.season.SeasonManager;
import quest.yuzhou.realmcraft.misc.strategicevent.StrategicEventManager;
import quest.yuzhou.realmcraft.placeholder.KillCounterPlaceholderExpansion;
import quest.yuzhou.realmcraft.placeholder.NextChestUnlockTimePlaceholderExpansion;
import quest.yuzhou.realmcraft.placeholder.RankingPlaceholderExpansion;
import quest.yuzhou.realmcraft.placeholder.SeasonPlaceholderExpansion;
import quest.yuzhou.realmcraft.player.PlayerJoinLeaveListener;
import quest.yuzhou.realmcraft.player.PlayerPreLoginListener;
import quest.yuzhou.realmcraft.player.Rewards;
import quest.yuzhou.realmcraft.player.killcounter.KillCounter;
import quest.yuzhou.realmcraft.player.menu.MenuManager;
import quest.yuzhou.realmcraft.player.menu.mainmenu.MainMenuHandler;
import quest.yuzhou.realmcraft.player.menu.manualmenu.ManualMenuHandler;
import quest.yuzhou.realmcraft.player.menu.playermenu.PlayerMenuHandler;
import quest.yuzhou.realmcraft.player.menu.seasonmenu.SeasonMenuHandler;
import quest.yuzhou.realmcraft.player.menu.strategiceventmenu.StrategicEventMenuHandler;
import quest.yuzhou.realmcraft.player.menu.tutorialmenu.TutorialMenuHandler;
import quest.yuzhou.realmcraft.player.rank.RankManager;
import quest.yuzhou.realmcraft.types.Season;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RealmCraft extends JavaPlugin {

    private boolean debug = false;
    private final PluginManager pluginManager = getServer().getPluginManager();
    public String prefix;
    public World mainWorld;
    public World fieldWorld;
    private Economy economy;
    private RCChest rcChest;
    private ConfigManager configManager;
    private Database database;
    private RankManager rankManager;
    private NewbieIntroduction newbieIntroduction;
    private NewbieQuestManager newbieQuestManager;
    private MenuManager menuManager;
    private StrategicEventManager strategicEventManager;
    private InstanceManager instanceManager;
    private SeasonManager seasonManager;
    private CommandManager commandManager;
    private AdminCommandManager adminCommandManager;
    private Restarter restarter;
    private MusicPlayer musicPlayer;
    private KillCounter killCounter;
    private AFKArea afkArea;
    private ChestUnlocker chestUnlocker;

    private List<PlaceholderExpansion> placeholderExpansionList;
    private List<Listener> listenerList;
    
    private final Map<Player, String> silent = new HashMap<>(); // To prevent a player in battle is disturbed.

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        try {
            database = new Database(this, getDataFolder().getAbsolutePath() + "/player.db");
        } catch (SQLException e) {
            getLogger().warning("Failed to connect to database!");
            e.printStackTrace();
            pluginManager.disablePlugin(this);
        }
        configManager = new ConfigManager(this);
        configManager.loadAllConfigs();

        if (!setupEconomy()) {
            getLogger().severe("Error while loading Vault dependency");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!setupRCChestPlugin()) {
            getLogger().severe("Error while loading RCChest dependency");
            getServer().getPluginManager().disablePlugin(this);
        }

        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
        mainWorld = Bukkit.getWorld(getConfig().getString("main-world-name"));
        if (mainWorld == null) {
            pluginManager.disablePlugin(this);
            getLogger().severe("MAIN WORLD'S NAME NOT FOUND IN CONFIG, PLEASE MAKE SURE YOU SET THE CONFIG PROPERLY");
        }
        fieldWorld = Bukkit.getWorld(getConfig().getString("field-world-name"));
        if (fieldWorld == null) {
            pluginManager.disablePlugin(this);
            getLogger().severe("FIELD WORLD'S NAME NOT FOUND IN CONFIG, PLEASE MAKE SURE YOU SET THE CONFIG PROPERLY");
        }

        rankManager = new RankManager(this);
        newbieIntroduction = new NewbieIntroduction(this);
        newbieQuestManager = new NewbieQuestManager(this);
        menuManager = new MenuManager(this);
        strategicEventManager = new StrategicEventManager(this);
        instanceManager = new InstanceManager(this);
        seasonManager = new SeasonManager(this);
        restarter = new Restarter(this);
        musicPlayer = new MusicPlayer(this);
        killCounter = new KillCounter(this);
        afkArea = new AFKArea(this);
        chestUnlocker = new ChestUnlocker(this);

        commandManager = new CommandManager(this);
        adminCommandManager = new AdminCommandManager(this);

        getCommand("rc").setExecutor(commandManager);
        getCommand("rca").setExecutor(adminCommandManager);

        getCommand("rc").setTabCompleter(new MainCommandTabCompleter(this));
        getCommand("rca").setTabCompleter(new AdminCommandTabCompleter(this));

        listenerList = new ArrayList<>();

        listenerList.add(new PlayerJoinLeaveListener(this));
        listenerList.add(new MainMenuHandler(this));
        listenerList.add(new PlayerMenuHandler(this));
        listenerList.add(new TutorialMenuHandler(this));
        listenerList.add(new StrategicEventMenuHandler(this));
        listenerList.add(new ManualMenuHandler(this));
        listenerList.add(new SeasonMenuHandler(this));
        listenerList.add(new NewbieStartIntro(this));
        listenerList.add(new NewbieStartTutorial(this));
        listenerList.add(new KitChestListener(this));
        listenerList.add(new EmergencyPackChestListener(this));
        listenerList.add(new PlayerPreLoginListener(this));
        listenerList.add(new Rewards(this));
        listenerList.add(musicPlayer);
        listenerList.add(killCounter);
        listenerList.add(newbieIntroduction);
        listenerList.add(new BiomePotionEffect(this));
        listenerList.add(new PreventPutBackPackInsideBackPack());
        listenerList.add(new RecycleStation(this));
        listenerList.add(new ChoicePressurePlate(this));
        listenerList.add(new PreventPlayerBreakHanging(this));
        
        listenerList.forEach((listener) -> pluginManager.registerEvents(listener, this));

        if (pluginManager.getPlugin("PlaceholderAPI") != null) {

            placeholderExpansionList = new ArrayList<>();
            placeholderExpansionList.add(new RankingPlaceholderExpansion(this));
            placeholderExpansionList.add(new KillCounterPlaceholderExpansion(this));
            placeholderExpansionList.add(new SeasonPlaceholderExpansion(this));
            placeholderExpansionList.add(new NextChestUnlockTimePlaceholderExpansion(this));

            placeholderExpansionList.forEach(PlaceholderExpansion::register);

            getLogger().info("PlaceholderAPI expansion registered.");
        } else {
            getLogger().info("PlaceholderAPI not found!");
            pluginManager.disablePlugin(this);
        }

        getLogger().info("Current season: " + seasonManager.getCurrentSeason().name()
                + " (Day " + seasonManager.getCurrentDay() + "/" + Season.getDaysInSeason() + ")");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "discord bcast :flags: 目前賽季： " + seasonManager.getCurrentSeason().name() + "\n 進度：" + seasonManager.getCurrentDay() + "/28 日 (" + seasonManager.getProgressPercentage() + ")");

        getLogger().info("RealmCraft plugin enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getLogger().info("Bye!");

        seasonManager.stop();
        strategicEventManager.stop();
        afkArea.stop();
        musicPlayer.stop();
        chestUnlocker.stop();
        listenerList.forEach(HandlerList::unregisterAll);
        placeholderExpansionList.forEach(PlaceholderExpansion::unregister);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupRCChestPlugin() {
        try {
            this.rcChest = getPlugin(RCChest.class);
        } catch (Exception e) {
            getLogger().severe("Error while importing RCChest plugin!");
            return false;
        }
        return true;
    }

    public void broadcast(String message) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.removeAll(newbieIntroduction.getRunning());
        players.forEach(player -> player.sendMessage(message));
    }

    public Economy getEconomy() {
        return economy;
    }

    public RCChest getRCChest() {
        return rcChest;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Database getDatabase() {
        return database;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public NewbieIntroduction getNewbieIntroduction() {
        return newbieIntroduction;
    }

    public NewbieQuestManager getNewbieQuestManager() {
        return newbieQuestManager;
    }

    public StrategicEventManager getStrategicEventManager() {
        return strategicEventManager;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    public Restarter getRestarter() {
        return restarter;
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public KillCounter getKillCounter() {
        return killCounter;
    }

    public ChestUnlocker getChestUnlocker() {
        return chestUnlocker;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AdminCommandManager getAdminCommandManager() {
        return adminCommandManager;
    }

    public boolean isDebugOn() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Map<Player, String> getSilent() {
        return silent;
    }
}
