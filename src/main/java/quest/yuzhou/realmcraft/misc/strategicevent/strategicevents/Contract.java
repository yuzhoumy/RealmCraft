package quest.yuzhou.realmcraft.misc.strategicevent.strategicevents;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import quest.yuzhou.realmcraft.RealmCraft;
import quest.yuzhou.realmcraft.Utilities;
import quest.yuzhou.realmcraft.types.StrategicEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Contract extends StrategicEvent implements Listener {

    private final Set<UUID> waitingResponses = ConcurrentHashMap.newKeySet();
    private final Map<UUID, UUID> pendingInvites = new HashMap<>();
    private final Map<UUID, Set<UUID>> activeContracts = new HashMap<>();
    private final int duration;
    private final int costNeeded = Integer.parseInt(cost);

    public Contract(RealmCraft plugin) {
        super(
                plugin,
                Material.FLOWER_BANNER_PATTERN,
                "5",
                "契約",
                new String[]{
                        "aka臨時組隊系統，你可以請求特定玩家",
                        "成為你的隊友，長達 " + plugin.getConfig().getInt("contract-duration") + " 分鐘。",
                        "在這段時間内，你們是無法攻擊對方的。"
                }
        );
        this.duration = plugin.getConfig().getInt("contract-duration");
    }

    @Override
    public void onMenuClick(Player player) {
        player.sendMessage(plugin.prefix + "請輸入該玩家的名字。");
        if (waitingResponses.add(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> waitingResponses.remove(player.getUniqueId()), 200);
            plugin.getLogger().info(Arrays.toString(waitingResponses.toArray()));
        }
    }

//    @EventHandler
//    public void onPlayerChat(AsyncPlayerChatEvent event) {
//        Player player = event.getPlayer();
//        String message = event.getMessage();
//
//        // Handle contract invitation
//        if (waitingResponses.contains(player.getUniqueId())) {
//            plugin.getLogger().info("waiting response include player");
//            event.setCancelled(true); // 先取消事件
//
//            // 在主线程中处理邀请逻辑
//            Bukkit.getScheduler().runTask(plugin, () -> {
//                // 检查服务器重启状态
//                if (plugin.getRestarter().isPreparingRestart()) {
//                    player.sendMessage(plugin.prefix + "伺服器即將重啟，暫時無法使用此功能。");
//                    waitingResponses.remove(player.getUniqueId());
//                    return;
//                }
//
//                waitingResponses.remove(player.getUniqueId());
//                Player target = Bukkit.getPlayerExact(message);
//
//                if (target != null && !target.equals(player)) {
//                    processContractInvitation(player, target);
//                } else {
//                    player.sendMessage(plugin.prefix + ChatColor.RED + "你不能請求自己。或者該玩家不存在。");
//                }
//            });
//        }
//
//        // Handle responses - 移到同步任务中处理
//        if (message.equalsIgnoreCase("y") || message.equalsIgnoreCase("n")) {
//            UUID responderId = player.getUniqueId();
//            UUID inviterId = null;
//
//            for (Map.Entry<UUID, UUID> entry : pendingInvites.entrySet()) {
//                if (entry.getValue().equals(responderId)) {
//                    inviterId = entry.getKey();
//                    break;
//                }
//            }
//
//            if (inviterId != null) {
//                event.setCancelled(true); // 先取消事件
//                UUID finalInviterId = inviterId;
//
//                // 在主线程中处理响应
//                Bukkit.getScheduler().runTask(plugin, () -> {
//                    Player inviter = Bukkit.getPlayer(finalInviterId);
//                    if (inviter != null) {
//                        if (message.equalsIgnoreCase("y")) {
//                            acceptContract(inviter, player);
//                        } else {
//                            declineContract(inviter, player);
//                        }
//                    }
//                    pendingInvites.remove(finalInviterId);
//                });
//            }
//        }
//    }

    private void processContractInvitation(Player player, Player target) {

        // 异步检查和扣除积分
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (target.getLevel() < costNeeded) {
                Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(plugin.prefix + ChatColor.RED + "你沒有足夠的等級，你至少需要 " + cost + " 等級。"));
                return;
            }

            // 回到主线程发送邀请
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.sendMessage(plugin.prefix + ChatColor.YELLOW + "已扣除 " + cost + "等級");
                sendContractInvitation(player, target);
            });
        });
    }

    private void sendContractInvitation(Player player, Player target) {
        pendingInvites.put(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(plugin.prefix + "邀請已發送給 " + target.getName() + "。");

        target.sendMessage(plugin.prefix + player.getName() + " 要和你簽訂契約，持續 " + duration + " 分鐘。");
        target.sendMessage(plugin.prefix + "你有 30 秒鐘時間決定：");
        target.sendMessage(plugin.prefix + Utilities.colorize("&e輸入 \"&by&e\" 同意，或 \"&bn&e\" 拒絕。"));
        TextComponent acceptComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.GREEN + "【接受】"));
        acceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rc accept-contract"));
        TextComponent denyComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + "【拒絕】"));
        denyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rc deny-contract"));
        target.spigot().sendMessage(new TextComponent(TextComponent.fromLegacyText("請點擊： ")), acceptComponent, new TextComponent(TextComponent.fromLegacyText(" 或 ")), denyComponent);

        // 30秒后自动移除邀请
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID targetId = pendingInvites.get(player.getUniqueId());
                if (targetId != null && targetId.equals(target.getUniqueId())) {
                    pendingInvites.remove(player.getUniqueId());
                    // 可选：通知玩家邀请已过期
                    if (player.isOnline()) {
                        player.sendMessage(plugin.prefix + ChatColor.GRAY + "对 " + target.getName() + " 的契约邀请已过期。");
                    }
                    if (target.isOnline()) {
                        target.sendMessage(plugin.prefix + ChatColor.GRAY + "来自 " + player.getName() + " 的契约邀请已过期。");
                    }
                }
            }
        }.runTaskLater(plugin, 600); // 30 seconds
    }

    public void acceptContract(Player acceptor) {

        Player inviter = null;
        for (Map.Entry<UUID, UUID> invites : pendingInvites.entrySet()) {
            if (invites.getValue().equals(acceptor.getUniqueId())) {
                inviter = Bukkit.getPlayer(invites.getKey());
            }
        }
        if (inviter == null) {
            acceptor.sendMessage(plugin.prefix = ChatColor.RED + "你沒有收到任何契約邀請！");
            return;
        }

        inviter.sendMessage(plugin.prefix + ChatColor.YELLOW + acceptor.getName() + " 已同意簽訂契約。");
        acceptor.sendMessage(plugin.prefix + ChatColor.YELLOW + "你已與 " + inviter.getName() + " 簽訂契約。");

        inviter.playSound(inviter.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_4, 1f, 1f);
        acceptor.playSound(acceptor.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_4, 1f, 1f);

        activeContracts.computeIfAbsent(inviter.getUniqueId(), k -> new HashSet<>()).add(acceptor.getUniqueId());
        activeContracts.computeIfAbsent(acceptor.getUniqueId(), k -> new HashSet<>()).add(inviter.getUniqueId());

        inviter.setLevel(inviter.getLevel() - costNeeded);

        Player finalInviter = inviter;
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<UUID> inviterSet = activeContracts.get(finalInviter.getUniqueId());
                if (inviterSet != null) {
                    inviterSet.remove(acceptor.getUniqueId());
                    if (inviterSet.isEmpty()) {
                        activeContracts.remove(finalInviter.getUniqueId());
                    }
                }
                Set<UUID> accepterSet = activeContracts.get(acceptor.getUniqueId());
                if (accepterSet != null) {
                    accepterSet.remove(finalInviter.getUniqueId());
                    if (accepterSet.isEmpty()) {
                        activeContracts.remove(acceptor.getUniqueId());
                    }
                }
                finalInviter.sendMessage(plugin.prefix + ChatColor.RED + "你與 " + acceptor.getName() + " 的契約已結束，現在你們可以互相攻擊了。");
                acceptor.sendMessage(plugin.prefix + ChatColor.RED + "你與 " + acceptor.getName() + " 的契約已結束，現在你們可以互相攻擊了。");
                finalInviter.playSound(finalInviter, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 10, 1);
                acceptor.playSound(acceptor, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 10, 1);
            }
        }.runTaskLater(plugin, (long) duration * 60 * 20); // duration minutes
    }

    private void declineContract(Player decliner) {
        Player inviter = null;
        for (Map.Entry<UUID, UUID> invites : pendingInvites.entrySet()) {
            if (invites.getValue().equals(decliner.getUniqueId())) {
                inviter = Bukkit.getPlayer(invites.getKey());
            }
        }
        if (inviter == null) {
            decliner.sendMessage(plugin.prefix = ChatColor.RED + "你沒有收到任何契約邀請！");
            return;
        }
        inviter.sendMessage(plugin.prefix + ChatColor.RED + decliner.getName() + " 拒絕了你的契約邀請。");
        decliner.sendMessage(plugin.prefix + ChatColor.RED + "你拒絕了與 " + inviter.getName() + " 的契約。");
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim) || !(event.getDamager() instanceof Player attacker)) {
            return;
        }

        if (areContracted(attacker, victim)) {
            event.setCancelled(true);
            attacker.sendMessage(plugin.prefix + ChatColor.RED + "你無法攻擊你的契約夥伴 " + victim.getName() + "！");
        }
    }

    private boolean areContracted(Player p1, Player p2) {
        Set<UUID> contracts = activeContracts.get(p1.getUniqueId());
        return contracts != null && contracts.contains(p2.getUniqueId());
    }
}
