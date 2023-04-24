package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class ButtonElement
{
    ArmorStand armorStand;
    Location originalLocation;

    public ButtonElement(ArmorStand armorStand)
    {
        this.armorStand = armorStand;
        this.originalLocation = armorStand.getLocation();
    }
}
