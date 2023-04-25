package me.karltroid.beanpass.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class Buttons
{
    static HashMap<Integer, Button> database = new HashMap<>();

    class Button
    {
        ItemStack itemStack;
        Button(Material material, int customModelData)
        {
            this.itemStack = new ItemStack(material);
            ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.setCustomModelData(customModelData);

            this.itemStack.setItemMeta(itemMeta);

        }
    }

    public Buttons()
    {
        database.put(10000, new Button(Material.DIAMOND, 10000));
    }

    public Button get(int customModelData)
    {
        return database.get(customModelData);
    }
}
