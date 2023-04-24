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
    static double RADIUS = 3; // how far out the hologram will be from the player

    public static ArmorStand createHolographicText(BeanPassGUI gui, Player player, double radiusOffset, double angleOffsetX, double angleOffsetY, String text)
    {
        // Get the player's eye location
        Location playerLocation = player.getLocation();
        Location eyeLocation = playerLocation.clone().add(0, player.getEyeHeight(), 0);

        // Calculate the position on the sphere's circumference
        double angleYaw = Math.toRadians(eyeLocation.getYaw() + angleOffsetX);
        double anglePitch = Math.toRadians(eyeLocation.getPitch() + angleOffsetY);
        double x = playerLocation.getX() + ((RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.cos(angleYaw));
        double y = playerLocation.getY() + ((RADIUS + radiusOffset) * Math.sin(anglePitch));
        double z = playerLocation.getZ() + ((RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.sin(angleYaw));

        // Create the armor stand at the calculated position
        Location armorStandLocation = new Location(playerLocation.getWorld(), x, y, z, eyeLocation.getYaw(), eyeLocation.getPitch());
        ArmorStand hologram = (ArmorStand) playerLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
        hologram.setGravity(false);
        hologram.setVisible(false);
        hologram.setBasePlate(false);
        hologram.setInvulnerable(true);
        hologram.setSmall(true);
        hologram.setMarker(true);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(text);
        hologram.getEquipment().setHelmet(new ItemStack(Material.STONE));

        // Set the entire armor stand to face the player
        Vector facingDirection = playerLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-facingDirection.getX(), facingDirection.getZ()));
        float pitch = (float) Math.toDegrees(Math.asin(facingDirection.getY()));
        hologram.setRotation(yaw, pitch);

        // Set the head pose to tilt up/down to look at the player
        EulerAngle headPose = new EulerAngle(-Math.toRadians(pitch), 0, 0);
        hologram.setHeadPose(headPose);

        return hologram;
    }

    public static boolean isLookingAt(Player player, Location l)
    {
        Location eye = player.getEyeLocation();
        double distance = Math.sqrt(Math.pow(l.getX() - eye.getX(), 2) + Math.pow(l.getY() - eye.getY(), 2) + Math.pow(l.getZ() - eye.getZ(), 2));
        if (distance > 10) return false;

        Vector toEntity = l.toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());
        return dot > 0.96D;
    }

}
