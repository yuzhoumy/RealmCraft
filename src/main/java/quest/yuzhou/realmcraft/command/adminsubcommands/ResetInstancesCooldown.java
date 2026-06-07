package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

public class ResetInstancesCooldown implements SubCommand {

    @Override
    public String getName() {
        return "reset-instances-cooldown";
    }

    @Override
    public String getDescription() {
        return "重置所有副本冷卻。";
    }

    @Override
    public String getSyntax() {
        return "/rca reset-instances-cooldown [instance]";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (args.length == 1) {
            plugin.getInstanceManager().getInstances().forEach((name, instance) -> instance.resetCooldown());
        } else if (args.length == 2) {
            plugin.getInstanceManager().getInstances().forEach((name, instance) -> {
                if (args[1].equalsIgnoreCase(name)) {
                    instance.resetCooldown();
                }
            });
            sender.sendMessage(plugin.prefix + "找不到副本，請檢查名字拼寫。");
            return;
        }
        sender.sendMessage(plugin.prefix + "所有副本的冷卻時間已經重置。");
    }

}
