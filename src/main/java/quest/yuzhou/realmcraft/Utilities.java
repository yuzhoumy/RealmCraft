package quest.yuzhou.realmcraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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

    public static boolean hasInventorySpace(Player player, List<ItemStack> items) {
        Inventory inventory = player.getInventory();

        // Create a copy of only the storage contents (slots 0-35)
        ItemStack[] inventoryCopy = new ItemStack[36];
        ItemStack[] storageContents = inventory.getStorageContents();

        // Deep copy the storage inventory
        for (int i = 0; i < storageContents.length && i < 36; i++) {
            if (storageContents[i] != null && !storageContents[i].getType().isAir()) {
                inventoryCopy[i] = storageContents[i].clone();
            }
        }

        // Try to fit each item into the copied inventory
        for (ItemStack item : items) {
            if (item == null || item.getType().isAir()) {
                continue;
            }

            int remainingAmount = item.getAmount();

            // First, try to stack with existing items
            for (ItemStack itemStack : inventoryCopy) {
                if (remainingAmount <= 0) break;

                if (itemStack != null && itemStack.isSimilar(item)) {
                    int maxStack = itemStack.getMaxStackSize();
                    int spaceInSlot = maxStack - itemStack.getAmount();

                    if (spaceInSlot > 0) {
                        int amountToAdd = Math.min(spaceInSlot, remainingAmount);
                        itemStack.setAmount(itemStack.getAmount() + amountToAdd);
                        remainingAmount -= amountToAdd;
                    }
                }
            }

            // Then, use empty slots for remaining items
            for (int i = 0; i < inventoryCopy.length; i++) {
                if (remainingAmount <= 0) break;

                if (inventoryCopy[i] == null) {
                    int maxStack = item.getMaxStackSize();
                    int amountToAdd = Math.min(maxStack, remainingAmount);

                    inventoryCopy[i] = item.clone();
                    inventoryCopy[i].setAmount(amountToAdd);
                    remainingAmount -= amountToAdd;
                }
            }

            // If there's still items left, inventory is full
            if (remainingAmount > 0) {
                return false;
            }
        }

        return true;
    }

}
