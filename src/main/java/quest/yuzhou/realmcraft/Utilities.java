package quest.yuzhou.realmcraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Utilities {

    public static String colorize(String s) {
        if (s.equalsIgnoreCase("")) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void pinPlayer(Player player, Location origin, double radius, double degree) {

        double x = (radius * Math.cos(Math.toRadians(degree))) + origin.getX();
        double z = (radius * Math.sin(Math.toRadians(degree))) + origin.getZ();

        player.teleport(new Location(origin.getWorld(), x, origin.getBlockY(), z, (float) degree - 90, 0));
    }

    public static int degreeToClock(Location origin, Location target) {
        double dx = target.getX() - origin.getX();
        double dz = target.getZ() - origin.getZ();

        double angleToTarget = Math.toDegrees(Math.atan2(-dx, dz));
        if (angleToTarget < 0) angleToTarget += 360;

        float playerYaw = origin.getYaw();
        if (playerYaw < 0) playerYaw += 360;

        double relativeAngle = (angleToTarget - playerYaw + 360) % 360;

        return (int) Math.round(relativeAngle / 30) % 12;
    }

    public static double roundTwoDecimalPlaces(double input) {
        return ((double) Math.round(input * 100)) / 100;
    }


}
