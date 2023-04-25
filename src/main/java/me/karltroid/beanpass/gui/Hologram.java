package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Hologram
{
    static final double RADIUS = 3; // how far out the hologram will be from the player
    static final double ARMOR_STAND_HEIGHT = 1; // 1 = 1 block/meter
    static final double HOLOGRAM_CENTER_CORRECTION = 90; // 1 = 1 block/meter

    public static boolean isLookingAt(Player player, Location l)
    {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() - ARMOR_STAND_HEIGHT);

        double distance = Math.sqrt(Math.pow(l.getX() - eye.getX(), 2) + Math.pow(l.getY() - eye.getY(), 2) + Math.pow(l.getZ() - eye.getZ(), 2));
        if (distance > 10) return false;

        Vector toEntity = l.toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());
        return dot > 0.985D;
    }

}
