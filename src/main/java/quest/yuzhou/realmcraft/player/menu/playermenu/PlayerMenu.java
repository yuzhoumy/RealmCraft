package quest.yuzhou.realmcraft.player.menu.playermenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.player.menu.AbstractMenu;
import quest.yuzhou.realmcraft.types.Rank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerMenu extends AbstractMenu {

    public PlayerMenu(RealmCraft plugin) {
        super(plugin);

        String title = plugin.getConfig().getString("player-menu-name");
        if (title == null) {
            plugin.getLogger().severe("Player menu name not found in config.yml");
            title = "&b玩家數據";
        }
        menuName = title;
    }

    @Override
    public void open(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, Utilities.colorize(menuName));

        ItemStack returnItem = null;
        ItemStack profileItem = null;
        ItemStack moneyItem = null;
        ItemStack rankItem = null;

        try {
            returnItem = new ItemStack(Material.PAPER);
            ItemMeta returnMeta = returnItem.getItemMeta();
            returnMeta.setCustomModelData(10071);
            returnMeta.setDisplayName("返回");
            returnItem.setItemMeta(returnMeta);

            profileItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta profileMeta = (SkullMeta) profileItem.getItemMeta();
            profileMeta.setDisplayName(ChatColor.YELLOW + player.getName());
            profileMeta.setOwningPlayer(player);
            profileItem.setItemMeta(profileMeta);

            double balance = plugin.getEconomy().getBalance(player);
            moneyItem = new ItemStack(Material.STICK);
            ItemMeta moneyMeta = moneyItem.getItemMeta();
            moneyMeta.setDisplayName(Utilities.colorize("&e現有金錢：&6$" + balance));
            moneyMeta.setCustomModelData(10004);
            moneyItem.setItemMeta(moneyMeta);

            Rank rank = null;
            int point = 0;
            try {
                rank = plugin.getDatabase().getRankByPlayer(player);
                point = plugin.getDatabase().getScore(player);
            } catch (SQLException e) {
                plugin.getLogger().severe("Can't get player's rank from database!");
                e.printStackTrace();
            }
            rankItem = new ItemStack(Material.STICK);
            ItemMeta rankMeta = rankItem.getItemMeta();
            rankMeta.setDisplayName(ChatColor.AQUA + "段位：" + rank.name());
            List<String> rankLore = new ArrayList<>();
            rankLore.add(ChatColor.AQUA + "積分：" + point);
            rankMeta.setLore(rankLore);
            rankMeta.setCustomModelData(10066);
            rankItem.setItemMeta(rankMeta);

        } catch (NullPointerException e) {
            plugin.getLogger().severe("Error while opening player profile");
            e.printStackTrace();
        }

        inventory.setItem(18, returnItem);
        inventory.setItem(10, profileItem);
        inventory.setItem(12, moneyItem);
        inventory.setItem(13, rankItem);

        player.openInventory(inventory);

    }

}
