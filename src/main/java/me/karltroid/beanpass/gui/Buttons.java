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
        database.put(10000, new Button(Material.GLASS_BOTTLE, 10000)); // beanpass bg
        database.put(10001, new Button(Material.GLASS_BOTTLE, 10001)); // left arrow
        database.put(10002, new Button(Material.GLASS_BOTTLE, 10002)); // right arrow
        database.put(10003, new Button(Material.GLASS_BOTTLE, 10003)); // get premium button
        database.put(10004, new Button(Material.GLASS_BOTTLE, 10004)); // items button
        database.put(10005, new Button(Material.GLASS_BOTTLE, 10005)); // quests button
        database.put(10006, new Button(Material.GLASS_BOTTLE, 10006)); // beanpass title
    }

    public Button get(int customModelData)
    {
        return database.get(customModelData);
    }
}
