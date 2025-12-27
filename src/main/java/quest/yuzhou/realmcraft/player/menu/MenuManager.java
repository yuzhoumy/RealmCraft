package quest.yuzhou.realmcraft.player.menu;

import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.player.menu.mainmenu.MainMenu;
import quest.yuzhou.realmcraft.player.menu.manualmenu.ManualMenu;
import quest.yuzhou.realmcraft.player.menu.playermenu.PlayerMenu;
import quest.yuzhou.realmcraft.player.menu.seasonmenu.SeasonMenu;
import quest.yuzhou.realmcraft.player.menu.strategiceventmenu.StrategicEventMenu;
import quest.yuzhou.realmcraft.player.menu.tutorialmenu.TutorialMenu;

import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    public enum MenuType {
        MAIN_MENU,
        PLAYER_MENU,
        TUTORIAL_MENU,
        STRATEGIC_EVENT_MENU,
        MANUAL_MENU,
        SEASON_MENU
    }

    private final Map<MenuType, AbstractMenu> menus = new HashMap<>();

    public MenuManager(RealmCraft plugin) {
        addMenu(MenuType.MAIN_MENU, new MainMenu(plugin));
        addMenu(MenuType.PLAYER_MENU, new PlayerMenu(plugin));
        addMenu(MenuType.TUTORIAL_MENU, new TutorialMenu(plugin));
        addMenu(MenuType.STRATEGIC_EVENT_MENU, new StrategicEventMenu(plugin));
        addMenu(MenuType.MANUAL_MENU, new ManualMenu(plugin));
        addMenu(MenuType.SEASON_MENU, new SeasonMenu(plugin));
    }

    public void addMenu(MenuType type, AbstractMenu menu) {
        menus.put(type, menu);
    }

    public AbstractMenu getMenu(MenuType type) {
        return menus.get(type);
    }
}
