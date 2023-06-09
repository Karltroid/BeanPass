package me.karltroid.beanpass.gui;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.w3c.dom.Text;

public class TextElement extends Element
{
    TextDisplay textDisplay;
    Transformation originalTransformation;

    public TextElement(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double angleOffsetX, double angleOffsetY, float displayScale, String text)
    {
        super(beanPassGUI, spherePlacement, distance, angleOffsetX, angleOffsetY);

        this.textDisplay = (TextDisplay) beanPassGUI.world.spawnEntity(this.location, EntityType.TEXT_DISPLAY);

        this.textDisplay.setBillboard(Display.Billboard.FIXED);
        this.textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        this.textDisplay.setShadowed(true);
        this.textDisplay.setBrightness(new Display.Brightness(10, 10));
        Transformation transformation = this.textDisplay.getTransformation();
        this.textDisplay.setTransformation(new Transformation(transformation.getTranslation(), transformation.getLeftRotation(),new Vector3f(displayScale), transformation.getRightRotation()));
        this.originalTransformation = this.textDisplay.getTransformation();
        this.textDisplay.setRotation(location.getYaw(), location.getPitch());

        this.textDisplay.setText(text);
    }
}
