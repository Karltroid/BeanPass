package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class VisualElement extends Element
{
    ItemDisplay itemDisplay;
    Transformation originalTransformation;

    public VisualElement(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
    {
        super(beanPassGUI, spherePlacement, distance, angleOffsetX, angleOffsetY);

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        this.itemDisplay = (ItemDisplay) beanPassGUI.world.spawnEntity(this.location, EntityType.ITEM_DISPLAY);
        this.itemDisplay.setItemStack(itemStack);

        this.itemDisplay.setBillboard(Display.Billboard.FIXED);
        this.itemDisplay.setBrightness(new Display.Brightness(10, 10));
        Transformation transformation = this.itemDisplay.getTransformation();
        this.itemDisplay.setTransformation(new Transformation(transformation.getTranslation(), transformation.getLeftRotation(),new Vector3f(displayScale), transformation.getRightRotation()));
        this.originalTransformation = this.itemDisplay.getTransformation();
        this.itemDisplay.setRotation(location.getYaw(), location.getPitch());
    }
}
