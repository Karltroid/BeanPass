package me.karltroid.beanpass.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BeanPassGUI implements InventoryHolder, GUI
{
    private final Inventory gui;

    public BeanPassGUI()
    {
        // Create the inventory with 54 slots (double chest)
        this.gui = Bukkit.createInventory(this, 54, "Beanpass GUI"); // You can customize the title as you like

        // Populate the inventory with items
        // Replace this with your actual GUI content
        ItemStack exampleItem = new ItemStack(Material.DIAMOND);
        this.gui.setItem(0, exampleItem);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.gui;
    }
}
