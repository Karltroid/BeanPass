package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.w3c.dom.Text;

public class TextElement
{
    TextDisplay textDisplay;
    Location originalLocation;

    public TextElement(Player player, double radiusOffset, double angleOffsetX, double angleOffsetY, String text)
    {
        // Get the player's eye location
        Location playerLocation = player.getLocation();
        playerLocation.add(0, player.getEyeHeight(), 0);

        // Calculate the position of the sphere, its circumference, and yaw rotation based on the player head's rotation
        double angleYaw = Math.toRadians(angleOffsetX + playerLocation.getYaw() + Hologram.HOLOGRAM_CENTER_CORRECTION);
        double anglePitch = Math.toRadians(angleOffsetY);
        double x = playerLocation.getX() + ((Hologram.RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.cos(angleYaw));
        double y = playerLocation.getY() + ((Hologram.RADIUS + radiusOffset) * Math.sin(anglePitch));
        double z = playerLocation.getZ() + ((Hologram.RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.sin(angleYaw));

        // Create the text display entity at the calculated position
        this.originalLocation = new Location(playerLocation.getWorld(), x, y, z, playerLocation.getYaw(), playerLocation.getPitch());
        this.textDisplay = (TextDisplay) playerLocation.getWorld().spawnEntity(this.originalLocation, EntityType.TEXT_DISPLAY);
        this.textDisplay.setText(text);
        this.textDisplay.setBillboard(Display.Billboard.FIXED);
        this.textDisplay.setBrightness(new Display.Brightness(15, 15));
        this.textDisplay.setShadowed(false);
        this.textDisplay.setGravity(false);

        Vector facingDirection = playerLocation.toVector().subtract(this.originalLocation.toVector()).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-facingDirection.getX(), facingDirection.getZ()));
        float pitch = (float) Math.toDegrees(Math.asin(facingDirection.getY()));
        this.textDisplay.setRotation(yaw, -pitch);
    }
}
