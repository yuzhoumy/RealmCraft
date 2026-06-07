package quest.yuzhou.realmcraft.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quest.yuzhou.realmcraft.RealmCraft;

import java.util.List;

public class MainCommandTabCompleter implements org.bukkit.command.TabCompleter {

    private final RealmCraft plugin;

    public MainCommandTabCompleter(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("rc")) {
            if (strings.length == 1) {
                return plugin.getCommandManager().getSubCommands().stream().map(SubCommand::getName).toList();
            } else if (strings.length == 2) {
                if (strings[1].equalsIgnoreCase("join-bounty")) {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                }
            }
        }
        return null;
    }
}
