package quest.yuzhou.realmcraft.command;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;

public interface SubCommand {
    String getName();
    String getDescription();
    String getSyntax();
    void perform(CommandSender sender, String[] args, RealmCraft plugin);
}
