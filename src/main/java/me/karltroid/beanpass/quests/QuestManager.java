package me.karltroid.beanpass.quests;


import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
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

        miningQuest.incrementPlayerCount();
        if (miningQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, miningQuest);
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

        killingQuest.incrementPlayerCount();
        if (killingQuest.isCompleted()) completeQuest(player, playerData, killingQuest);
    }
}
