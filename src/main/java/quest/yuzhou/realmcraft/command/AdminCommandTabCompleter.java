package quest.yuzhou.realmcraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quest.yuzhou.realmcraft.RealmCraft;

import java.util.List;

public class AdminCommandTabCompleter implements org.bukkit.command.TabCompleter {

    private final RealmCraft plugin;

    public AdminCommandTabCompleter(RealmCraft plugin) {
        this.plugin = plugin;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("rca")) {
            if (strings.length == 1) {
                return plugin.getAdminCommandManager().getSubCommands().stream().map(SubCommand::getName).toList();
            }
        }
        return null;
    }
}
