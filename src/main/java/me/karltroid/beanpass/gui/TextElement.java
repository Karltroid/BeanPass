package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class TextElement
{
    ArmorStand armorStand;
    Location originalLocation;

    public TextElement(Player player, double radiusOffset, double angleOffsetX, double angleOffsetY, String text)
    {
        // Get the player's eye location
        Location playerLocation = player.getLocation();
        playerLocation.add(0, player.getEyeHeight() - Hologram.ARMOR_STAND_HEIGHT, 0);

        // Calculate the position of the sphere, its circumference, and yaw rotation based on the player head's rotation
        double angleYaw = Math.toRadians(angleOffsetX + playerLocation.getYaw() + Hologram.HOLOGRAM_CENTER_CORRECTION);
        double anglePitch = Math.toRadians(angleOffsetY);
        double x = playerLocation.getX() + ((Hologram.RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.cos(angleYaw));
        double y = playerLocation.getY() + ((Hologram.RADIUS + radiusOffset) * Math.sin(anglePitch));
        double z = playerLocation.getZ() + ((Hologram.RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.sin(angleYaw));

        // Create the armor stand at the calculated position
        Location armorStandLocation = new Location(playerLocation.getWorld(), x, y, z, playerLocation.getYaw(), playerLocation.getPitch());
        ArmorStand hologram = (ArmorStand) playerLocation.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
        hologram.setGravity(false);
        hologram.setVisible(false);
        hologram.setBasePlate(false);
        hologram.setInvulnerable(true);
        hologram.setSmall(true);
        hologram.setMarker(true);
        hologram.setFireTicks(72000);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(text);

        // Set the entire armor stand to face the player
        Vector facingDirection = playerLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-facingDirection.getX(), facingDirection.getZ()));
        float pitch = (float) Math.toDegrees(Math.asin(facingDirection.getY()));
        hologram.setRotation(yaw, pitch);

        // Set the head pose to tilt up/down to look at the player
        EulerAngle headPose = new EulerAngle(-Math.toRadians(pitch), 0, 0);
        hologram.setHeadPose(headPose);

        this.armorStand = hologram;
        this.originalLocation = hologram.getLocation();
    }
}
