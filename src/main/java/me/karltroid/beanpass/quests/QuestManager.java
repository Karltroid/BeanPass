package me.karltroid.beanpass.quests;


import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests.*;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Smoker;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class QuestManager implements Listener
{
    final String BOLD_GREEN = ChatColor.GREEN + "" + ChatColor.BOLD;
    final String BOLD_GRAY = ChatColor.GRAY + "" + ChatColor.BOLD;
    final String ITALIC_YELLOW = ChatColor.YELLOW + "" + ChatColor.ITALIC;


    void completeQuest(Player player, PlayerData playerData, Quest quest)
    {
        playerData.getQuests().remove(quest);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        BeanPass.sendMessage(player, BOLD_GREEN + "QUEST COMPLETED: " + ChatColor.GREEN + quest.getGoalDescription());
        playerData.addXp(quest.getXPReward());
    }

    @EventHandler
    void onMiningQuestProgressed(BlockBreakEvent event)
    {
        // if the block goal is something that grows, only count it if its fully grown
        BlockData blockData = event.getBlock().getState().getBlockData();
        if (blockData instanceof Ageable)
        {
            Ageable ageable = (Ageable) blockData;
            if (ageable.getAge() != ageable.getMaximumAge()) return;
        }

        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(playerUUID);

        Material blockMinedType = event.getBlock().getType();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof MiningQuest)) continue;
            if (!(blockMinedType.name().contains(((MiningQuest) quest).getGoalBlockType().name()))) continue;

            MiningQuest miningQuest = (MiningQuest) quest;
            miningQuest.incrementPlayerCount(1);
            if (miningQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, miningQuest);
        }
    }

    @EventHandler
    void onFishingQuestProgressed(PlayerFishEvent event)
    {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(playerUUID);

        Entity caught = event.getCaught();

        if (caught == null || caught.getType() != EntityType.DROPPED_ITEM) return;

        Material caughtMaterial = ((Item)event.getCaught()).getItemStack().getType();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof FishingQuest)) continue;
            if (caughtMaterial != ((FishingQuest) quest).getGoalItemType()) continue;

            FishingQuest fishingQuest = (FishingQuest) quest;
            fishingQuest.incrementPlayerCount(1);
            if (fishingQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, fishingQuest);
        }
    }

    @EventHandler
    void onCraftingQuestProgressed(CraftItemEvent event)
    {
        Player player = (Player)event.getWhoClicked();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        // Check if the click resulted in a potion being taken out
        ItemStack itemCrafted = event.getCurrentItem();
        Material itemCraftedType = itemCrafted.getType();
        int amount = itemCrafted.getAmount();

        if (event.isShiftClick())
        {
            ItemStack[] matrix = event.getInventory().getMatrix();
            ItemStack smallestItem = null;
            for(int i = 2; i <= 65; i++)
            {
                for (ItemStack item : matrix) {
                    if (item == null || item.getAmount() >= i) continue;

                    smallestItem = item;
                    break;
                }

                if (smallestItem == null) continue;

                amount *= smallestItem.getAmount();
                break;
            }

            // check to make sure the amount crafted fit in their inventory, if not adjust amount accordingly
            int itemCraftedMaxStackSize = itemCrafted.getMaxStackSize();
            int playerInvSlotsOpen = 0;
            int playerInvAmountOfCraftedTypeFillable = 0;
            for (ItemStack slot : player.getInventory().getStorageContents()) {
                if (slot == null || slot.getType() == Material.AIR) {
                    playerInvSlotsOpen++;
                }
                else if (slot.getType() == itemCraftedType && slot.getAmount() < itemCraftedMaxStackSize)
                {
                    playerInvAmountOfCraftedTypeFillable += itemCraftedMaxStackSize - slot.getAmount();
                }
            }
            if (playerInvSlotsOpen == 0 && playerInvAmountOfCraftedTypeFillable == 0) return;

            int maxCraftedItemThatFitsInv = playerInvSlotsOpen * itemCraftedMaxStackSize + playerInvAmountOfCraftedTypeFillable;
            if (maxCraftedItemThatFitsInv < amount) amount = maxCraftedItemThatFitsInv;
        }

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof CraftingQuest)) continue;
            CraftingQuest craftingQuest = (CraftingQuest) quest;

            if (!(itemCraftedType.equals(craftingQuest.getGoalItemType()))) continue;
            craftingQuest.incrementPlayerCount(amount);
            if (craftingQuest.isCompleted()) completeQuest(player, playerData, craftingQuest);
        }
    }

    @EventHandler
    public void onCraftingQuestProgressed_Furnace(FurnaceExtractEvent event)
    {
        Player player = event.getPlayer();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        // Check if the click resulted in a potion being taken out
        Material itemSmeltedType = event.getItemType();
        int amountSmelted = event.getItemAmount();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof CraftingQuest)) continue;
            CraftingQuest craftingQuest = (CraftingQuest) quest;

            if (!(itemSmeltedType.equals(craftingQuest.getGoalItemType()))) continue;
            craftingQuest.incrementPlayerCount(amountSmelted);
            if (craftingQuest.isCompleted()) completeQuest(player, playerData, craftingQuest);
        }
    }

    @EventHandler
    void onBrewingQuestProgressed(InventoryClickEvent event)
    {
        if (!(event.getClickedInventory() instanceof BrewerInventory)) return;

        Player player = (Player)event.getWhoClicked();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        // Check if the click resulted in a potion being taken out
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() != Material.POTION) return;

        PotionMeta potionMeta = (PotionMeta)currentItem.getItemMeta();
        NamespacedKey brewedKey = new NamespacedKey(BeanPass.getInstance(), "brewed");
        if (potionMeta.getPersistentDataContainer().has(brewedKey, PersistentDataType.BYTE)) return;
        else
        {
            potionMeta.getPersistentDataContainer().set(brewedKey, PersistentDataType.BYTE, (byte) 1);
            currentItem.setItemMeta(potionMeta);
        }

        PotionType potionType = (potionMeta).getBasePotionData().getType();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof BrewingQuest)) continue;
            BrewingQuest brewingQuest = (BrewingQuest) quest;

            if (!(potionType.equals(brewingQuest.getGoalItemType()))) continue;
            brewingQuest.incrementPlayerCount(1);
            if (brewingQuest.isCompleted()) completeQuest(player, playerData, brewingQuest);
        }
    }

    @EventHandler
    void onKillingQuestProgressed(EntityDeathEvent event)
    {
        EntityType entityTypeKilled = event.getEntityType();
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(playerUUID);

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof KillingQuest)) continue;
            KillingQuest killingQuest = (KillingQuest) quest;

            if (entityTypeKilled != ((KillingQuest) quest).getGoalEntityType()) continue;
            killingQuest.incrementPlayerCount(1);
            if (killingQuest.isCompleted()) completeQuest(player, playerData, killingQuest);
        }
    }
}
