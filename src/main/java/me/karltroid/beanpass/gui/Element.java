package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

public abstract class Element
{
    BeanPassGUI beanPassGUI;
    Location location;

    public Element(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance,  double angleOffsetX, double angleOffsetY)
    {
        this.beanPassGUI = beanPassGUI;
        Location playerLocation = beanPassGUI.playerLocation;

        // calculate the position of the element around the player
        double angleYaw, anglePitch;
        double x,y,z;
        Vector facingDirection;
        float yawRotation, pitchRotation;
        if (spherePlacement)
        {
            angleYaw = Math.toRadians(angleOffsetX + playerLocation.getYaw() + BeanPassGUI.GUI_ROTATION_CORRECTION);
            anglePitch = Math.toRadians(angleOffsetY);

            x = playerLocation.getX() + (distance * Math.cos(anglePitch) * Math.cos(angleYaw));
            y = playerLocation.getY() + (distance * Math.sin(anglePitch));
            z = playerLocation.getZ() + (distance * Math.cos(anglePitch) * Math.sin(angleYaw));

            // calculate the yaw and pitch rotation of the element, so it faces towards the player head
            facingDirection = playerLocation.toVector().subtract(new Vector(x, y, z)).normalize();
            yawRotation = (float) Math.toDegrees(Math.atan2(-facingDirection.getX(), facingDirection.getZ()));
            pitchRotation = (float) -Math.toDegrees(Math.asin(facingDirection.getY()));
        }
        else
        {
            angleOffsetX /= 15;
            angleOffsetY /= 15;

            angleYaw = Math.toRadians(playerLocation.getYaw() + BeanPassGUI.GUI_ROTATION_CORRECTION);
            anglePitch = 0;

            x = playerLocation.getX() + (distance * Math.cos(anglePitch) * Math.cos(angleYaw));
            y = playerLocation.getY();
            z = playerLocation.getZ() + (distance * Math.cos(anglePitch) * Math.sin(angleYaw));

            facingDirection = playerLocation.toVector().subtract(new Vector(x, y, z)).normalize();
            yawRotation = (float) Math.toDegrees(Math.atan2(-facingDirection.getX(), facingDirection.getZ()));
            pitchRotation = (float) -Math.toDegrees(Math.asin(facingDirection.getY()));

            Location centerLocation = new Location(beanPassGUI.world, x,y,z);
            Vector centerRotation = facingDirection.clone();

            Vector up = new Vector(0, 1, 0);
            Vector right = centerRotation.crossProduct(up).multiply(-1);
            right.normalize();

            Location newPosition = centerLocation.clone().add(right.multiply(angleOffsetX));
            newPosition = newPosition.add(up.multiply(angleOffsetY));

            x = newPosition.getX();
            y = newPosition.getY();
            z = newPosition.getZ();

            // calculate the yaw and pitch rotation of the element, so it faces towards the player head
        }

        this.location = new Location(playerLocation.getWorld(), x, y, z, Float.isFinite(yawRotation) ? yawRotation : 0.0f, Float.isFinite(pitchRotation) ? pitchRotation : 0.0f);
    }
}
