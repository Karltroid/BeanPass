package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class VisualElement extends Element
{
    Entity entity;
    Transformation originalTransformation;
    float displayScale;

    public VisualElement(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double angleOffsetX, double angleOffsetY, float displayScale, int legacySize, Material material, int customModelData)
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
            boolean smallArmorStand = false;
            boolean headDisplay = true;

            switch (legacySize)
            {
                case 1:
                    smallArmorStand = true;
                    headDisplay = false;
                    this.location.subtract(0, 0.697058, 0);
                    break;
                case 2:
                    headDisplay = false;
                    this.location.subtract(0, 1.394117, 0);
                    break;
                case 3:
                    smallArmorStand = true;
                    this.location.subtract(0, 0.697058, 0);
                    break;
                case 4:
                    this.location.subtract(0, 1.394117, 0);
                    break;
                default:
                    BeanPass.getInstance().getLogger().warning("Bedrock armor stand legacy size does not exist. (1-4 only)");
                    break;
            }

            ArmorStand armorStand = (ArmorStand) beanPassGUI.world.spawnEntity(this.location, EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setBasePlate(false);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);
            armorStand.setFireTicks(72000);
            armorStand.setSmall(smallArmorStand);

            if (headDisplay)
            {
                armorStand.getEquipment().setHelmet(itemStack);
                armorStand.setHeadPose(new EulerAngle(Math.toRadians(location.getPitch()), 0 ,0));
            }
            else
            {
                armorStand.setArms(true);
                armorStand.setLeftArmPose(EulerAngle.ZERO);
                armorStand.setRightArmPose(EulerAngle.ZERO);
                armorStand.getEquipment().setItemInMainHand(itemStack);
            }

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
