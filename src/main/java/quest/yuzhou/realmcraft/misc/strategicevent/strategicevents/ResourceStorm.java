package quest.yuzhou.realmcraft.misc.strategicevent.strategicevents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.types.ServerExecutable;
import quest.yuzhou.realmcraft.types.StrategicEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResourceStorm extends StrategicEvent implements Listener, ServerExecutable {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");
    private long cooldown;

    public ResourceStorm(RealmCraft plugin) {
        super(
                plugin,
                Material.RAW_GOLD,
                "50",
                "探勘家",
                new String[]{
                        "嫌資源不夠嗎？聘請探勘家幫你尋找資源點吧。",
                        "伺服器將生成數超大型資源點，",
                        "這些資源點内部的寶箱至多可達20多個！",
                        "探勘家會在一分鐘前提前通知所有玩家開始尋找資源點，",
                        "以便玩家準備混戰。一分鐘後，探勘家將會廣播資源點的坐標。",
                        "該資源點半徑50格内PvP積分獎勵+30"
                }
        );
    }

    @Override
    public void onMenuClick(Player player) {
        if (System.currentTimeMillis() < cooldown && !plugin.getNewbieQuestManager().getNewbieQuests().get(18).isQuestRunning(player)) { //quest 17

            String date = simpleDateFormat.format(new Date(cooldown));
            player.sendMessage(plugin.prefix + ChatColor.RED + "探勘家目前還在冷卻中，下次可用時間是 " + ChatColor.GOLD + ChatColor.UNDERLINE + date);
            return;
        }

        int costRequired = Integer.parseInt(cost);
        if (player.getLevel() > costRequired) {
            player.setLevel(player.getLevel() - costRequired);

        run(player);
        } else {
            player.sendMessage(plugin.prefix + ChatColor.RED + "你沒有足夠的等級，至少需要 " + costRequired);
        }
    }

    @Override
    public void serverExecute() {
        if (System.currentTimeMillis() < cooldown) return;
        run(null);
    }

    private void run(@Nullable Player player) {
        cooldown = System.currentTimeMillis() + YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "strategic-event.yml")).getInt("resource-storm.cooldown");
        String resourceStormCommand = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "strategic-event.yml")).getString("resource-storm.command");
        if (resourceStormCommand == null) {
            plugin.getLogger().severe("resource-storm's command not found in strategic-event.yml. Please config properly!");
            if (player != null)
                player.sendMessage(plugin.prefix + ChatColor.RED + "出現嚴重錯誤，請立即通知管理員。");
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resourceStormCommand);
    }
}
