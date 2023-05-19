package me.karltroid.beanpass.quests;


import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Quests.KillingQuest;
import me.karltroid.beanpass.data.Quests.MiningQuest;
import me.karltroid.beanpass.data.Quests.Quest;
import me.karltroid.beanpass.data.SeasonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class QuestManager implements Listener
{
    HashMap<Material, String> miningQuestDifficulties = new HashMap<>();
    HashMap<EntityType, String> killingQuestDifficulties = new HashMap<>();
    final String BOLD_GREEN = ChatColor.GREEN + " " + ChatColor.BOLD;
    final String BOLD_GRAY = ChatColor.GRAY + " " + ChatColor.BOLD;
    final String ITALIC_YELLOW = ChatColor.YELLOW + " " + ChatColor.ITALIC;
    int questsPerPlayer;

    public QuestManager(int questsPerPlayer)
    {
        loadQuestDifficulties();
        this.questsPerPlayer = questsPerPlayer;
    }

    public int getQuestsPerPlayer() { return questsPerPlayer; }

    void loadQuestDifficulties()
    {
        FileConfiguration config = BeanPass.main.getConfig();
        ConfigurationSection miningQuestDifficultiesSection = config.getConfigurationSection("MiningQuestDifficulties");
        ConfigurationSection killingQuestDifficultiesSection = config.getConfigurationSection("KillingQuestDifficulties");

        if (miningQuestDifficultiesSection == null || killingQuestDifficultiesSection == null)
        {
            BeanPass.main.getLogger().warning("Couldn't get a QuestDifficulties section in your Config.yml");
            return;
        }

        for (String materialName : miningQuestDifficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = miningQuestDifficultiesSection.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.main.getLogger().warning("Couldn't get the " + materialName + " mining difficulty in your Config.yml");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            String difficulty = difficultySection.getString("difficulty");

            miningQuestDifficulties.put(material, difficulty);
        }

        for (String entityTypeName : killingQuestDifficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = killingQuestDifficultiesSection.getConfigurationSection(entityTypeName);

            if (difficultySection == null)
            {
                BeanPass.main.getLogger().warning("Couldn't get the " + entityTypeName + " killing difficulty in your Config.yml");
                continue;
            }

            EntityType entityType = null;
            for (EntityType type : EntityType.values())
            {
                if (!type.name().equalsIgnoreCase(entityTypeName)) continue;
                entityType = type;
                break;
            }
            if (entityType == null)
            {
                BeanPass.main.getLogger().warning("Entity type " + entityTypeName + " does not exist. Skipping.");
                continue;
            }
            String difficulty = difficultySection.getString("difficulty");

            killingQuestDifficulties.put(entityType, difficulty);
        }
    }

    void completeQuest(Player player, SeasonPlayer seasonPlayer, Quest quest)
    {
        seasonPlayer.addXp(quest.getXPReward());
        seasonPlayer.getQuests().remove(quest);
        player.sendMessage(BOLD_GREEN + "COMPLETED: " + ChatColor.GREEN + quest.getGoalDescription() + " " + ITALIC_YELLOW + quest.getRewardDescription());

        Quest nextQuest = seasonPlayer.giveQuest(null);
        player.sendMessage(BOLD_GRAY + "NEW QUEST: " + ChatColor.GRAY + nextQuest.getGoalDescription() + " " + ITALIC_YELLOW + nextQuest.getRewardDescription());
    }

    @EventHandler
    void onMiningQuestProgressed(BlockBreakEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(playerUUID);

        Material blockMinedType = event.getBlock().getType();

        MiningQuest miningQuest = null;
        for (Quest quest : seasonPlayer.getQuests())
        {
            if (!(quest instanceof MiningQuest)) continue;
            if (blockMinedType != ((MiningQuest) quest).getGoalBlockType()) continue;
            miningQuest = (MiningQuest) quest;
        }
        if (miningQuest == null) return;

        miningQuest.incrementPlayerCount();
        if (miningQuest.isCompleted()) completeQuest(event.getPlayer(), seasonPlayer, miningQuest);
    }

    @EventHandler
    void onKillingQuestProgressed(EntityDeathEvent event)
    {
        EntityType entityTypeKilled = event.getEntityType();
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        UUID playerUUID = player.getUniqueId();
        SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(playerUUID);

        KillingQuest killingQuest = null;
        for (Quest quest : seasonPlayer.getQuests())
        {
            if (!(quest instanceof KillingQuest)) continue;
            if (entityTypeKilled != ((KillingQuest) quest).getGoalEntityType()) continue;
            killingQuest = (KillingQuest) quest;
        }
        if (killingQuest == null) return;

        killingQuest.incrementPlayerCount();
        if (killingQuest.isCompleted()) completeQuest(player, seasonPlayer, killingQuest);
    }

    public HashMap<Material, String> getMiningQuestDifficulties()
    {
        return miningQuestDifficulties;
    }
    public HashMap<EntityType, String> getKillingQuestDifficulties()
    {
        return killingQuestDifficulties;
    }
}
