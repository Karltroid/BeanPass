package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Hologram
{
    static final double RADIUS = 3; // how far out the hologram will be from the player
    static final double HOLOGRAM_CENTER_CORRECTION = 90; // 1 = 1 block/meter

    public static boolean isLookingAt(Player player, Location l)
    {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY());

        double distance = Math.sqrt(Math.pow(l.getX() - eye.getX(), 2) + Math.pow(l.getY() - eye.getY(), 2) + Math.pow(l.getZ() - eye.getZ(), 2));
        if (distance > 10) return false;

        Vector toEntity = l.toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());
        return dot > 0.985D;
    }

}
