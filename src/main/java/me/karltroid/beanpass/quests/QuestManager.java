package me.karltroid.beanpass.quests;


import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.BrewerInventory;
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
        playerData.addXp(quest.getXPReward());
        playerData.getQuests().remove(quest);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        BeanPass.sendMessage(player, BOLD_GREEN + "QUEST COMPLETED: " + ChatColor.GREEN + quest.getGoalDescription() + " " + ITALIC_YELLOW + quest.getRewardDescription());
    }

    @EventHandler
    void onMiningQuestProgressed(BlockBreakEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(playerUUID);

        Material blockMinedType = event.getBlock().getType();

        MiningQuest miningQuest = null;
        for (Quest quest : playerData.getQuests())
        {
            if (!(quest instanceof MiningQuest)) continue;
            if (blockMinedType != ((MiningQuest) quest).getGoalBlockType()) continue;
            miningQuest = (MiningQuest) quest;
        }
        if (miningQuest == null) return;

        miningQuest.incrementPlayerCount(1);
        if (miningQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, miningQuest);
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

        FishingQuest fishingQuest = null;
        for (Quest quest : playerData.getQuests())
        {
            if (!(quest instanceof FishingQuest)) continue;
            if (caughtMaterial != ((FishingQuest) quest).getGoalItemType()) continue;
            fishingQuest = (FishingQuest) quest;
        }
        if (fishingQuest == null) return;

        fishingQuest.incrementPlayerCount(1);
        if (fishingQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, fishingQuest);
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

        List<BrewingQuest> brewingQuests = new ArrayList<>();
        for (Quest quest : playerData.getQuests())
        {
            if (quest instanceof BrewingQuest) brewingQuests.add((BrewingQuest) quest);
        }

        if (brewingQuests.size() == 0) return;

        for(BrewingQuest brewingQuest : brewingQuests)
        {
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

        KillingQuest killingQuest = null;
        for (Quest quest : playerData.getQuests())
        {
            if (!(quest instanceof KillingQuest)) continue;
            if (entityTypeKilled != ((KillingQuest) quest).getGoalEntityType()) continue;
            killingQuest = (KillingQuest) quest;
        }
        if (killingQuest == null) return;

        killingQuest.incrementPlayerCount(1);
        if (killingQuest.isCompleted()) completeQuest(player, playerData, killingQuest);
    }
}
