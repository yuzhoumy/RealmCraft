package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class UnlockChests implements SubCommand {

    @Override
    public String getName() {
        return "unlock-chests";
    }

    @Override
    public String getDescription() {
        return "解鎖所有資源箱";
    }

    @Override
    public String getSyntax() {
        return "/rca unlock-chests";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        int affected = plugin.getRCChest().getChestAndCommandManager().clearBlockAbove();
        sender.sendMessage(plugin.prefix + "解鎖了 " + affected + " 個資源箱。");
    }
}
