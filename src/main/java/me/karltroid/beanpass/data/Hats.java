package me.karltroid.beanpass.data;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public final class Hats
{
    public HashMap<Integer, Hat> database = new HashMap<>();

    public Hats()
    {
        database.put(100000, new Hat(1, ChatColor.RESET + "Top Hat",
                new String[]{
                        ChatColor.GRAY + "Very dapper!"
                }
        ));
    }

    static class Hat
    {
        int id;
        String name;
        String[] lore;
        ItemStack itemStack;

        Hat(int id, String name, String[] lore)
        {
            this.id = id;
            this.name = name;
            this.lore = lore;

            itemStack = new ItemStack(Material.CARVED_PUMPKIN);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(id);
            itemMeta.setDisplayName(name);
            if (lore != null) itemMeta.setLore(Arrays.asList(lore));
            itemStack.setItemMeta(itemMeta);
        }
    }

    public Hat getHat(int id) { return database.get(id); }
}
