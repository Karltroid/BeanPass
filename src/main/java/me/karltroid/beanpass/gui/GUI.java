package me.karltroid.beanpass.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface GUI
{
    Inventory gui = null;

    default void open(Player player)
    {
        player.openInventory(gui);
    }

    default void close(Player player)
    {
        player.closeInventory();
    }
}
