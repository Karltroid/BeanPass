package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkinManager implements Listener
{
    List<Skin> skins = new ArrayList<>();
    List<Material> skinTypes = new ArrayList<>();

    public SkinManager()
    {
        FileConfiguration config = BeanPass.getInstance().getCosmeticsConfig();

        ConfigurationSection skinsSection = config.getConfigurationSection("Skins");

        if (skinsSection != null)
        {
            // Loop through each key (skin name) in the "Skins" section
            for (String skinName : skinsSection.getKeys(false))
            {

                // Get the configuration section for the current skin
                ConfigurationSection skinSection = skinsSection.getConfigurationSection(skinName);
                if (skinSection != null)
                {
                    // Read the skin properties from the configuration
                    String materialName = skinSection.getString("Material");
                    int id = skinSection.getInt("ID");

                    // Create a new Skin object and add it to the list
                    if (materialName == null || Material.matchMaterial(materialName) == null)
                    {
                        BeanPass.getInstance().getLogger().warning("Material name does not exist, skipping skin #" + id);
                        continue;
                    }
                    Material material = Material.matchMaterial(materialName);
                    if (!skinTypes.contains(material)) skinTypes.add(material);
                    Skin skin = new Skin(skinName.toLowerCase(), id, material);
                    skins.add(skin);
                }
            }
        }
    }

    public void updateInventorySkins(Player player, Inventory inventory)
    {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++)
        {
            updateItemSlotWithSkin(player, inventory, i);
        }
    }

    void updateItemSlotWithSkin(Player player, Inventory inventory, int slot)
    {
        ItemStack item = inventory.getItem(slot);
        if (item == null || item.getType() == Material.AIR) return;
        if (!skinTypes.contains(item.getType())) return;

        List<Skin> playerEquippedSkins = new ArrayList<>();
        if (inventory instanceof PlayerInventory) playerEquippedSkins = BeanPass.getInstance().getPlayerData(player.getUniqueId()).equippedSkins;

        int skinId = -1;
        for (Skin skin : playerEquippedSkins)
        {
            if (skin.getSkinApplicant() == item.getType())
            {
                skinId = skin.getId();
                break;
            }
        }

        ItemStack updatedItem = applySkin(item.clone(), skinId); // Create a clone of the item with updated customModelData

        // Use inventory.setItem() instead of player.getInventory().setItem()
        inventory.setItem(slot, updatedItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory upperInventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        Inventory playerInventory = player.getInventory();
        if (clickedInventory == null) return;

        ItemStack cursorItem = event.getCursor();
        ItemStack clickedItem = event.getCurrentItem();

        if ((cursorItem != null && skinTypes.contains(cursorItem.getType())) || (clickedItem != null && skinTypes.contains(clickedItem.getType())))
        {
            Bukkit.getScheduler().runTaskLater(BeanPass.getInstance(), () -> {
                updateInventorySkins(player, playerInventory);
                if (!(upperInventory instanceof CraftingInventory)) updateInventorySkins(player, upperInventory);
            }, 1);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event)
    {
        if (!(event.getEntity() instanceof Player)) return;


        Player player = (Player) event.getEntity();
        ItemStack pickedUpItem = event.getItem().getItemStack();
        if (!skinTypes.contains(pickedUpItem.getType())) return;

        Bukkit.getScheduler().runTaskLater(BeanPass.getInstance(), () -> {
            updateInventorySkins(player, player.getInventory());
        }, 1);
    }

    private ItemStack applySkin(ItemStack item, int customModelData)
    {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            BeanPass.getInstance().getLogger().warning("Failed to apply skin #" + customModelData + " to " + item.getType().name());
            return item;
        }

        if (customModelData == -1) itemMeta.setCustomModelData(null);
        else itemMeta.setCustomModelData(customModelData);
        item.setItemMeta(itemMeta);

        return item;
    }

    private ItemStack removeSkin(ItemStack item)
    {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;

        itemMeta.setCustomModelData(null);
        item.setItemMeta(itemMeta);

        return item;
    }


    public Skin getSkinByName(String name)
    {
        Skin foundSkin = null;
        for(Skin skin : skins)
        {
            if (!skin.getName().equals(name.toLowerCase())) continue;
            foundSkin = skin;
            break;
        }
        return foundSkin;
    }

    public Skin getSkinById(int id)
    {
        Skin foundSkin = null;
        for(Skin skin : skins)
        {
            if (skin.getId() != id) continue;
            foundSkin = skin;
            break;
        }
        return foundSkin;
    }
}
