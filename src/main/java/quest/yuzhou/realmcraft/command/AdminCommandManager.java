package quest.yuzhou.realmcraft.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.adminsubcommands.*;

import java.util.ArrayList;
import java.util.List;

public class AdminCommandManager implements CommandExecutor {

    private final RealmCraft plugin;
    private final List<SubCommand> subCommands;

    public AdminCommandManager(RealmCraft plugin) {
        this.plugin = plugin;
        subCommands = new ArrayList<>();
        subCommands.add(new SetHasPassedIntroStage());
        subCommands.add(new Reload());
        subCommands.add(new AddPoint());
        subCommands.add(new NewbieIntro());
        subCommands.add(new Debug());
        subCommands.add(new ForceQuest());
        subCommands.add(new Query());
        subCommands.add(new DoneRefreshResourcePoint());
        subCommands.add(new PrepareRestart());
        subCommands.add(new CancelRestart());
        subCommands.add(new ResetInstancesCooldown());
        subCommands.add(new QuerySeason());
        subCommands.add(new SetSeasonStartDate());
        subCommands.add(new SetKitClaimed());
        subCommands.add(new SetFinalKitClaimed());
        subCommands.add(new GetSeasonSouvenirItem());
        subCommands.add(new SeasonRewardItems());
        subCommands.add(new UnlockChests());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!command.getName().equalsIgnoreCase("rca")) return true;

        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    subCommand.perform(sender, args, plugin);
                    return true;
                }
            }
        }
        sender.sendMessage("=--- RealmCraft plugin admin commands ---=");
        for (SubCommand subCommand : subCommands) {
            sender.sendMessage(ChatColor.YELLOW + subCommand.getSyntax() + " " + ChatColor.AQUA + subCommand.getDescription());
        }
        return true;

    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
