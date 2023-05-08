package me.karltroid.beanpass.quests;


import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Quests;
import me.karltroid.beanpass.data.Quests.KillingQuest;
import me.karltroid.beanpass.data.Quests.MiningQuest;
import me.karltroid.beanpass.data.Quests.Quest;
import me.karltroid.beanpass.data.SeasonPlayer;
import me.karltroid.beanpass.enums.ServerGamemode;
import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.UUID;

public class QuestManager implements Listener
{
    HashMap<Material, String> miningQuestDifficulties = new HashMap<>();
    HashMap<EntityType, String> killingQuestDifficulties = new HashMap<>();
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
                if (type.name().equalsIgnoreCase(entityTypeName)) entityType = type;
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

    public HashMap<Material, String> getMiningQuestDifficulties()
    {
        return miningQuestDifficulties;
    }
    public HashMap<EntityType, String> getKillingQuestDifficulties()
    {
        return killingQuestDifficulties;
    }

    @EventHandler
    void onMiningQuestProgressed(BlockBreakEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(playerUUID);

        MiningQuest miningQuest = null;
        for (Quest quest : seasonPlayer.getQuests(BeanPass.main.getServerGamemode()))
        {
            if (!(quest instanceof MiningQuest)) continue;
            miningQuest = (MiningQuest) quest;
        }

        if (miningQuest == null) return;

        Material blockMinedType = event.getBlock().getType();
        if (blockMinedType != miningQuest.getGoalBlockType()) return;

        miningQuest.incrementPlayerCount();
        if (miningQuest.isCompleted())
        {
            seasonPlayer.addXp(miningQuest.getXPReward());
            seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).remove(miningQuest);

            event.getPlayer().sendMessage(ChatColor.GREEN + "COMPLETED: " + miningQuest.getGoalDescription());
            event.getPlayer().sendMessage(ChatColor.YELLOW + miningQuest.getRewardDescription());
            //seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).add(new MiningQuest(ServerGamemode.SURVIVAL, playerUUID.toString(), -1, null, -1, 0));
            seasonPlayer.giveQuest(null);
        }
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
        for (Quest quest : seasonPlayer.getQuests(BeanPass.main.getServerGamemode()))
        {
            if (!(quest instanceof KillingQuest)) continue;
            killingQuest = (KillingQuest) quest;
        }

        if (killingQuest == null) return;

        if (entityTypeKilled != killingQuest.getGoalEntityType()) return;

        killingQuest.incrementPlayerCount();
        if (killingQuest.isCompleted())
        {
            seasonPlayer.addXp(killingQuest.getXPReward());
            seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).remove(killingQuest);

            player.sendMessage(ChatColor.GREEN + "COMPLETED: " + killingQuest.getGoalDescription());
            player.sendMessage(ChatColor.YELLOW + killingQuest.getRewardDescription());
            //seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).add(new MiningQuest(ServerGamemode.SURVIVAL, playerUUID.toString(), -1, null, -1, 0));
            seasonPlayer.giveQuest(null);
        }
    }
}
