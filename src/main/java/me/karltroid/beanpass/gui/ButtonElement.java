package me.karltroid.beanpass.gui;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class ButtonElement extends VisualElement implements Button
{
    double selectionSensitivity;

    public ButtonElement(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, int legacySize, double selectionSensitivity, Material material, int customModelData)
    {
        super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, legacySize, material, customModelData);
        this.selectionSensitivity = selectionSensitivity;
    }

    public void select()
    {
        if (beanPassGUI.playerData.isBedrockAccount())
        {
            ArmorStand armorStand = (ArmorStand) entity;

            if (armorStand.getEquipment() == null) return;
            ItemStack displayItem;
            if (legacySize == 1 || legacySize == 2)
            {
                displayItem = armorStand.getEquipment().getItemInMainHand();
                ItemMeta displayItemMeta = displayItem.getItemMeta();
                displayItemMeta.addEnchant(Enchantment.MENDING, 1, true);
                displayItem.setItemMeta(displayItemMeta);
                armorStand.getEquipment().setItemInMainHand(displayItem);
            }
            else if (legacySize == 3 || legacySize == 4)
            {
                displayItem = armorStand.getEquipment().getHelmet();
                ItemMeta displayItemMeta = displayItem.getItemMeta();
                displayItemMeta.addEnchant(Enchantment.MENDING, 1, true);
                displayItem.setItemMeta(displayItemMeta);
                armorStand.getEquipment().setHelmet(displayItem);
            }
            else return;

            armorStand.setGlowing(true);
            armorStand.setFireTicks(0);
            Vector movingDirection = beanPassGUI.player.getEyeLocation().clone().toVector().subtract(armorStand.getEyeLocation().toVector()).normalize();
            Location newLocation = location.clone().add(movingDirection.multiply(new Vector(0.02, 0, 0.02)));
            armorStand.teleport(newLocation);
        }
        else
        {
            ItemDisplay itemDisplay = (ItemDisplay) entity;
            itemDisplay.setTransformation(new Transformation(originalTransformation.getTranslation(), originalTransformation.getLeftRotation(), new Vector3f(originalTransformation.getScale().x * 1.2f), originalTransformation.getRightRotation()));
            itemDisplay.setGlowing(true);
            itemDisplay.setGlowColorOverride(Color.WHITE);
        }
    }

    public void unselect()
    {
        if (beanPassGUI.playerData.isBedrockAccount())
        {
            ArmorStand armorStand = (ArmorStand) entity;

            if (armorStand.getEquipment() == null) return;
            ItemStack displayItem;
            if (legacySize == 1 || legacySize == 2)
            {
                displayItem = armorStand.getEquipment().getItemInMainHand();
                ItemMeta displayItemMeta = displayItem.getItemMeta();
                displayItemMeta.removeEnchant(Enchantment.MENDING);
                displayItem.setItemMeta(displayItemMeta);
                armorStand.getEquipment().setItemInMainHand(displayItem);
            }
            else if (legacySize == 3 || legacySize == 4)
            {
                displayItem = armorStand.getEquipment().getHelmet();
                ItemMeta displayItemMeta = displayItem.getItemMeta();
                displayItemMeta.removeEnchant(Enchantment.MENDING);
                displayItem.setItemMeta(displayItemMeta);
                armorStand.getEquipment().setHelmet(displayItem);
            }
            else return;

            armorStand.setFireTicks(72000);
            armorStand.setGlowing(false);
            armorStand.teleport(location);
        }
        else
        {
            ItemDisplay itemDisplay = (ItemDisplay) entity;
            itemDisplay.setTransformation(originalTransformation);
            itemDisplay.setGlowing(false);
        }
    }

    public boolean isPlayerLooking(Player player)
    {
        Location eye = player.getEyeLocation();

        double distance = Math.sqrt(Math.pow(location.getX() - eye.getX(), 2) + Math.pow(location.getY() - eye.getY(), 2) + Math.pow(location.getZ() - eye.getZ(), 2));
        if (distance > 10) return false;

        if (beanPassGUI.playerData.isBedrockAccount())
        {
            ArmorStand armorStand = (ArmorStand) entity;
            if (armorStand.isSmall()) eye.setPitch((eye.getPitch() + 12.0f));
            else eye.setPitch((eye.getPitch() + 22.0f));
        }

        Vector toEntity = location.toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot > selectionSensitivity;
    }

    @Override
    public void click()
    {
        // do nothing, click functionality will be determined by specific button element subclasses
    }
}
