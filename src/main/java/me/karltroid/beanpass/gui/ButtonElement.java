package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.awt.*;

public class ButtonElement
{
    ItemDisplay itemDisplay;
    Location originalLocation;
    Transformation originalTransformation;
    float displayScale;

    public ButtonElement(Player player, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Buttons.Button button)
    {
        this.displayScale = displayScale;

        // Get the player's eye location
        Location playerLocation = player.getLocation();
        playerLocation.add(0, player.getEyeHeight(), 0);

        // Calculate the position of the sphere, its circumference, and yaw rotation based on the player head's rotation
        double angleYaw = Math.toRadians(angleOffsetX + playerLocation.getYaw() + Hologram.HOLOGRAM_CENTER_CORRECTION);
        double anglePitch = Math.toRadians(angleOffsetY);
        double x = playerLocation.getX() + ((Hologram.RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.cos(angleYaw));
        double y = playerLocation.getY() + ((Hologram.RADIUS + radiusOffset) * Math.sin(anglePitch));
        double z = playerLocation.getZ() + ((Hologram.RADIUS + radiusOffset) * Math.cos(anglePitch) * Math.sin(angleYaw));

        // Create the armor stand at the calculated position
        this.originalLocation = new Location(playerLocation.getWorld(), x, y, z, playerLocation.getYaw(), playerLocation.getPitch());
        this.itemDisplay = (ItemDisplay) playerLocation.getWorld().spawnEntity(this.originalLocation, EntityType.ITEM_DISPLAY);
        this.itemDisplay.setBillboard(Display.Billboard.FIXED);
        this.itemDisplay.setItemStack(button.itemStack);
        this.itemDisplay.setBrightness(new Display.Brightness(15, 15));
        Transformation transformation = this.itemDisplay.getTransformation();
        this.itemDisplay.setTransformation(new Transformation(transformation.getTranslation(), transformation.getLeftRotation(),new Vector3f(displayScale), transformation.getRightRotation()));
        this.originalTransformation = this.itemDisplay.getTransformation();

        // Set the entire armor stand to face the player
        Vector facingDirection = playerLocation.toVector().subtract(this.originalLocation.toVector()).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-facingDirection.getX(), facingDirection.getZ()));
        float pitch = (float) Math.toDegrees(Math.asin(facingDirection.getY()));
        itemDisplay.setRotation(yaw, -pitch);
    }

    public void select()
    {
        itemDisplay.setTransformation(new Transformation(originalTransformation.getTranslation(), originalTransformation.getLeftRotation(),new Vector3f(displayScale * 1.2f), originalTransformation.getRightRotation()));
    }

    public void unselect()
    {
        itemDisplay.setTransformation(originalTransformation);
    }
}
