package quest.yuzhou.realmcraft.command.subcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class DenyContract implements SubCommand {

    @Override
    public String getName() {
        return "deny-contract";
    }

    @Override
    public String getDescription() {
        return "接受某個玩家發出的契約請求";
    }

    @Override
    public String getSyntax() {
        return "/rc deny-contract";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {

    }
}
