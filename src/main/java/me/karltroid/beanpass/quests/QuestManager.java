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
    HashMap<Material, String> miningQuestDifficulties = new HashMap<>();
    HashMap<Material, String> lumberQuestDifficulties = new HashMap<>();
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
        FileConfiguration config = BeanPass.getInstance().getConfig();
        ConfigurationSection miningQuestDifficultiesSection = config.getConfigurationSection("MiningQuestDifficulties");
        ConfigurationSection lumberQuestDifficultiesSection = config.getConfigurationSection("LumberQuestDifficulties");
        ConfigurationSection killingQuestDifficultiesSection = config.getConfigurationSection("KillingQuestDifficulties");

        if (miningQuestDifficultiesSection == null || killingQuestDifficultiesSection == null || lumberQuestDifficultiesSection == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get a QuestDifficulties section in your Config.yml");
            return;
        }

        for (String materialName : miningQuestDifficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = miningQuestDifficultiesSection.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + materialName + " mining difficulty in your Config.yml");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            String difficulty = difficultySection.getString("difficulty");

            miningQuestDifficulties.put(material, difficulty);
        }

        for (String materialName : lumberQuestDifficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = lumberQuestDifficultiesSection.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + materialName + " lumber difficulty in your Config.yml");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            String difficulty = difficultySection.getString("difficulty");

            System.out.println(material.name() + " " + difficulty);
            lumberQuestDifficulties.put(material, difficulty);
        }

        for (String entityTypeName : killingQuestDifficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = killingQuestDifficultiesSection.getConfigurationSection(entityTypeName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + entityTypeName + " killing difficulty in your Config.yml");
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
                BeanPass.getInstance().getLogger().warning("Entity type " + entityTypeName + " does not exist. Skipping.");
                continue;
            }
            String difficulty = difficultySection.getString("difficulty");

            killingQuestDifficulties.put(entityType, difficulty);
        }
    }

    void completeQuest(Player player, PlayerData playerData, Quest quest)
    {
        playerData.addXp(quest.getXPReward());
        playerData.getQuests().remove(quest);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.sendMessage(BOLD_GREEN + "COMPLETED: " + ChatColor.GREEN + quest.getGoalDescription() + " " + ITALIC_YELLOW + quest.getRewardDescription());

        Quest nextQuest = playerData.giveQuest(null);
        player.sendMessage(BOLD_GRAY + "NEW QUEST: " + ChatColor.GRAY + nextQuest.getGoalDescription() + " " + ITALIC_YELLOW + nextQuest.getRewardDescription());
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
    void onLumberQuestProgressed(BlockBreakEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerData playerData = BeanPass.getInstance().getPlayerData(playerUUID);

        Material blockMinedType = event.getBlock().getType();

        LumberQuest lumberQuest = null;
        for (Quest quest : playerData.getQuests())
        {
            if (!(quest instanceof LumberQuest)) continue;
            if (blockMinedType != ((LumberQuest) quest).getGoalBlockType()) continue;
            lumberQuest = (LumberQuest) quest;
        }
        if (lumberQuest == null) return;

        lumberQuest.incrementPlayerCount();
        if (lumberQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, lumberQuest);
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

    public HashMap<Material, String> getMiningQuestDifficulties()
    {
        return miningQuestDifficulties;
    }
    public HashMap<Material, String> getLumberQuestDifficulties() { return lumberQuestDifficulties; }
    public HashMap<EntityType, String> getKillingQuestDifficulties()
    {
        return killingQuestDifficulties;
    }
}
