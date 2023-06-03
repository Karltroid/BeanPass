package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class ButtonElement extends VisualElement implements Button
{
    public ButtonElement(BeanPassGUI beanPassGUI, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
    {
        super(beanPassGUI, radiusOffset, angleOffsetX, angleOffsetY, displayScale, material, customModelData);
    }

    public void select()
    {
        itemDisplay.setTransformation(new Transformation(originalTransformation.getTranslation(), originalTransformation.getLeftRotation(), new Vector3f(originalTransformation.getScale().x * 1.2f), originalTransformation.getRightRotation()));
    }

    public void unselect()
    {
        itemDisplay.setTransformation(originalTransformation);
    }

    public boolean isPlayerLooking(Player player)
    {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY());

        double distance = Math.sqrt(Math.pow(location.getX() - eye.getX(), 2) + Math.pow(location.getY() - eye.getY(), 2) + Math.pow(location.getZ() - eye.getZ(), 2));
        if (distance > 10) return false;

        Vector toEntity = location.toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());
        return dot > 0.985D;
    }

    @Override
    public void click()
    {
        // do nothing, click functionality will be determined by specific button element subclasses
    }
}
