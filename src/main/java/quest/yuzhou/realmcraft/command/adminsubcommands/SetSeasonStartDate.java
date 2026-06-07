package quest.yuzhou.realmcraft.command.adminsubcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.command.SubCommand;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SetSeasonStartDate implements SubCommand {
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getName() {
        return "set-season-start-date";
    }

    @Override
    public String getDescription() {
        return "設定第一個賽季的起始日期";
    }

    @Override
    public String getSyntax() {
        return "/rca set-season-start-date <dd/MM/yyyy>";
    }

    @Override
    public void perform(CommandSender sender, String[] args, RealmCraft plugin) {
        if (args.length != 2) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "格式錯誤，請參照：" + this.getSyntax());
            return;
        }

        try {
            LocalDate newStartDate = LocalDate.parse(args[1], DATE_FORMATTER);
            plugin.getSeasonManager().setStartDate(newStartDate);

            sender.sendMessage("§a起始日期更新爲： " + newStartDate.format(DATE_FORMATTER));
            sender.sendMessage("§a目前賽季： " + plugin.getSeasonManager().getCurrentSeason().name()
                    + " (" + plugin.getSeasonManager().getCurrentDay() + "/28)");

        } catch (Exception e) {
            sender.sendMessage(plugin.prefix + ChatColor.RED + "日期格式不對，請參照 " + this.getSyntax());
        }
    }
}
