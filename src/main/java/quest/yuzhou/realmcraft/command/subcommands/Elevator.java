package quest.yuzhou.realmcraft.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class Elevator implements SubCommand {

    @Override
    public String getName() {
        return "elevator";
    }

    @Override
    public String getDescription() {
        return "將你傳送至地下城上主城的電梯。";
    }

    @Override
    public String getSyntax() {
        return "/rc elevator";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令。");
            return;
        }

        if (player.getWorld() != Bukkit.getWorld("plots")) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "你只有在地下城中才能使用這個指令。");
            sender.sendMessage(plugin.prefix + ChatColor.RED + "如果你深處野外想回地皮，請走回主城。（坐標 X=64, Z=64）");
            return;
        }

        player.teleport(new Location(Bukkit.getWorld("plots"), 0, 11, -10));
        sender.sendMessage(plugin.prefix + ChatColor.GREEN + "已將你傳送至電梯。");
    }
}
