package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;
import quest.yuzhou.realmcraft.types.Rank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetSeasonSouvenirItem implements SubCommand {

    @Override
    public String getName() {
        return "get-season-souvenir-item";
    }

    @Override
    public String getDescription() {
        return "獲得賽季紀念品物品";
    }

    @Override
    public String getSyntax() {
        return "/rca get-season-souvenir-item";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令！");
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "你手上沒有任何物品！");
            return;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand().clone();

        if (!itemStack.hasItemMeta()) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "該物品沒有ItemMeta！");
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!itemMeta.hasLore()) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "該物品沒有Lore!");
            return;
        }
        List<String> lore = itemMeta.getLore();
        lore.remove(lore.size() - 1);
        List<String> newLore = new ArrayList<>();

        int lastSeasonPoint = 0;
        Rank lastSeasonRank = plugin.getRankManager().getRankById("unset");
        try {
            lastSeasonPoint = plugin.getDatabase().getLastSeasonPoint(player.getUniqueId());
            lastSeasonRank = plugin.getDatabase().getLastSeasonRank(player.getUniqueId());
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error while getting last season point or last season rank of " + player);
            player.sendMessage(plugin.prefix + ChatColor.RED + "查詢資料庫時發生錯誤，請查看後臺。");
        }
        for (String line : lore) {
            String newLine;
            newLine = line.replace("你的名字", player.getName());
            newLine = newLine.replace("XX段位", lastSeasonRank.name() + " 段位");
            newLine = newLine.replace("XX積分", lastSeasonPoint + " 積分");
            newLore.add(newLine);
        }

        ItemStack newItem = new ItemStack(itemStack.getType());
        ItemMeta newMeta = newItem.getItemMeta();
        newMeta.setDisplayName(itemMeta.getDisplayName());
        newMeta.setCustomModelData(itemMeta.getCustomModelData());
        newMeta.setLore(newLore);
        newItem.setItemMeta(newMeta);

        player.getInventory().setItemInMainHand(newItem);
        player.sendMessage(plugin.prefix + "你的專屬賽季徽章完成啦！");
    }
}
