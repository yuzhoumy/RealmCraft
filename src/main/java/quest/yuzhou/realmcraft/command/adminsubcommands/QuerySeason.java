package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;
import quest.yuzhou.realmcraft.misc.season.SeasonManager;

import java.time.format.DateTimeFormatter;

public class QuerySeason implements SubCommand {

    @Override
    public String getName() {
        return "query-season";
    }

    @Override
    public String getDescription() {
        return "查看賽季資訊";
    }

    @Override
    public String getSyntax() {
        return "/rca query-season";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        SeasonManager manager = plugin.getSeasonManager();
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        sender.sendMessage("§6§l          Season Information");
        sender.sendMessage("");
        sender.sendMessage("§eCurrent Season: §f" + manager.getCurrentSeason().name()
                + " §7(Season " + manager.getCurrentSeason().seasonNumber() + ")");
        sender.sendMessage("§eStarts On: §f" + manager.getThisSeasonStartDate().format(DATE_FORMATTER));
        sender.sendMessage("§eDay: §f" + manager.getDayString());
        sender.sendMessage("§eProgress: §f" + manager.getProgressPercentage());
        sender.sendMessage("§eDays Remaining: §f" + manager.getDaysRemaining());
        sender.sendMessage("");
        sender.sendMessage("§eNext Season: §f" + manager.getNextSeason().name());
        sender.sendMessage("§eStarts On: §f" + manager.getNextSeasonDate().format(DATE_FORMATTER));
        sender.sendMessage("");
        sender.sendMessage("§eFirst Season Start Date: §f" + manager.getStartDate().format(DATE_FORMATTER));
    }
}
