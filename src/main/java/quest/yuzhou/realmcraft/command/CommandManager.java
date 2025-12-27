package quest.yuzhou.realmcraft.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.subcommands.*;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final RealmCraft plugin;
    private final List<SubCommand> subCommands;

    public CommandManager(RealmCraft plugin) {
        this.plugin = plugin;
        subCommands = new ArrayList<>();
        subCommands.add(new Menu());
        subCommands.add(new Elevator());
        subCommands.add(new Tutorial());
        subCommands.add(new SE());
        subCommands.add(new JoinBounty());
        subCommands.add(new Handbook());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("rc")) return true;

        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    subCommand.perform(sender, args, plugin);
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.GREEN + "||| 領域世界核心指令 |||");
        for (SubCommand subCommand : subCommands) {
            sender.sendMessage(ChatColor.YELLOW + subCommand.getSyntax() + " " + ChatColor.AQUA + subCommand.getDescription());
        }
        return true;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
