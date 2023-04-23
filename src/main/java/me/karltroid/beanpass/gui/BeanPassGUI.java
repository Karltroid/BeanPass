package me.karltroid.beanpass.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class BeanPassGUI implements GUI, InventoryHolder, Listener
{
    private final Inventory GUI_INVENTORY;

    public BeanPassGUI()
    {
        GUI_INVENTORY = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Beanpass GUI"); // You can customize the title as you like
        GUI_INVENTORY.setItem(0, createGuiItem(Material.DIAMOND, "BEANPASS GUI ITEM TEST"));
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event)
    {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        if(event.getWhoClicked().getOpenInventory().getTopInventory().equals(this.getInventory())) event.setCancelled(true);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.GUI_INVENTORY;
    }
}
