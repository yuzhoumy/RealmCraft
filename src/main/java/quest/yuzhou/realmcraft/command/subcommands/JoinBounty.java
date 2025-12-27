package quest.yuzhou.realmcraft.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;
import quest.yuzhou.realmcraft.misc.strategicevent.strategicevents.Bounty;

public class JoinBounty implements SubCommand {

    @Override
    public String getName() {
        return "join-bounty";
    }

    @Override
    public String getDescription() {
        return "加入懸賞，成爲跟隨懸賞的同行者。如果被懸賞玩家在懸賞時間内被發動懸賞者殺死，則只有發動懸賞者獲得獎勵$10000。被同行者殺死，則同行者與發動懸賞者各獲得$5000。";
    }

    @Override
    public String getSyntax() {
        return "/rc join-bounty <通緝犯名>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "只有玩家可以使用這個指令。");
            return;
        }

        if (args.length != 2) {
            player.sendMessage(plugin.prefix + ChatColor.RED + "錯誤格式，請依據：" + getSyntax());
            return;
        }

        Bounty bounty = (Bounty) plugin.getStrategicEventManager().getStrategicEvent("懸賞");
        bounty.joinBounty(player, args[1]);
    }
}
