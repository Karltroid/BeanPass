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
    Entity entity;
    Transformation originalTransformation;
    float displayScale;

    public VisualElement(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
    {
        super(beanPassGUI, spherePlacement, distance, angleOffsetX, angleOffsetY);

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (customModelData != -1)
        {
            itemMeta.setCustomModelData(customModelData);
            itemStack.setItemMeta(itemMeta);
        }

        this.displayScale = displayScale;

        if (beanPassGUI.playerData.isBedrockAccount())
        {
            ArmorStand armorStand = (ArmorStand) beanPassGUI.world.spawnEntity(this.location, EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setBasePlate(false);
            armorStand.setInvulnerable(true);
            armorStand.setSmall(true);
            armorStand.setMarker(true);
            armorStand.setFireTicks(72000);
            armorStand.getEquipment().setHelmet(itemStack);

            this.entity = armorStand;
        }
        else
        {
            ItemDisplay itemDisplay = (ItemDisplay) beanPassGUI.world.spawnEntity(this.location, EntityType.ITEM_DISPLAY);
            itemDisplay.setItemStack(itemStack);

            itemDisplay.setBillboard(Display.Billboard.FIXED);
            itemDisplay.setBrightness(new Display.Brightness(10, 10));
            Transformation transformation = itemDisplay.getTransformation();
            itemDisplay.setTransformation(new Transformation(transformation.getTranslation(), transformation.getLeftRotation(),new Vector3f(displayScale), transformation.getRightRotation()));
            this.originalTransformation = itemDisplay.getTransformation();
            itemDisplay.setRotation(location.getYaw(), location.getPitch());

            this.entity = itemDisplay;
        }
    }
}
