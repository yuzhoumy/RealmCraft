package quest.yuzhou.realmcraft.player.menu.tutorialmenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.misc.newbie.NewbieQuest;
import quest.yuzhou.realmcraft.player.menu.AbstractMenu;

import java.sql.SQLException;
import java.util.*;

public class TutorialMenu extends AbstractMenu {

    public TutorialMenu(RealmCraft plugin) {
        super(plugin);

        String title = plugin.getConfig().getString("tutorial-menu-name");
        if (title == null) {
            plugin.getLogger().severe("Player menu name not found in config.yml");
            title = "&b新手教學";
        }
        menuName = title;
    }

    @Override
    public void open(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, Utilities.colorize(menuName));

        ItemStack returnItem = new ItemStack(Material.PAPER);
        ItemMeta returnMeta = returnItem.getItemMeta();
        returnMeta.setCustomModelData(10071);
        returnMeta.setDisplayName("返回");
        returnItem.setItemMeta(returnMeta);
        inventory.setItem(18, returnItem);

        int hasPassedIntroStage = 0;

        try {
            hasPassedIntroStage = plugin.getDatabase().getPlayerHasPassedIntroStage(player.getUniqueId());
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while getting player's has passed intro stage");
            e.printStackTrace();
        }

        if (hasPassedIntroStage >= 11 && hasPassedIntroStage < plugin.getNewbieQuestManager().getQuestsAmount() + 1) {
            ItemStack skipItem = new ItemStack(Material.PAPER);
            ItemMeta skipMeta = skipItem.getItemMeta();
            skipMeta.setCustomModelData(10064);
            skipMeta.setDisplayName("跳過當前教學");
            skipItem.setItemMeta(skipMeta);
            inventory.setItem(26, skipItem);
        }

        for (NewbieQuest quest : plugin.getNewbieQuestManager().getNewbieQuests()) {

            // 如果顯示屏障就表示有問題
            ItemStack questItem = new ItemStack(Material.BARRIER);
            ItemMeta questMeta = questItem.getItemMeta();
            questMeta.setLore(Arrays.stream(quest.getLore()).map((lore) -> Utilities.colorize("&7" + lore)).toList());

            if (quest.getNumber() < hasPassedIntroStage) {
                questItem.setType(Material.BOOK);
                questMeta.setDisplayName(Utilities.colorize("&5【已完成】&e " + quest.getNumber() + ". " + quest.getDisplayName()));
            } else if (quest.getNumber() > hasPassedIntroStage) {
                questItem.setType(Material.CHEST);
                questMeta.setDisplayName(Utilities.colorize("&c【未解鎖】&e " + quest.getNumber() + ". " + quest.getDisplayName()));
            } else {
                questItem.setType(Material.IRON_SWORD);
                questMeta.setDisplayName(Utilities.colorize("&a【進行中】&e " + quest.getNumber() + ". " + quest.getDisplayName()));
            }

            questItem.setItemMeta(questMeta);
            inventory.setItem(quest.getNumber() - 1, questItem);

        }

        player.openInventory(inventory);
    }
}
