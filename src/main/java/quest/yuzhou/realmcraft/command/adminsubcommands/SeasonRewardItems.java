package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class SeasonRewardItems implements SubCommand {

    @Override
    public String getName() {
        return "season-reward-items";
    }

    @Override
    public String getDescription() {
        return "列出所有賽季的所有獎勵品。";
    }

    @Override
    public String getSyntax() {
        return "/rca season-reward-items";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        sender.sendMessage(plugin.getSeasonManager().getRewardItemMapToString());
    }
}
