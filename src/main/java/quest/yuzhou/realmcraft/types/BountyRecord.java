package quest.yuzhou.realmcraft.types;

import org.bukkit.entity.Player;

import java.util.List;

public record BountyRecord(Player initiator, Player recipient, List<Player> followers, int minute) {
}
