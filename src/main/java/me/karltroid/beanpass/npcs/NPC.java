package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class NPC implements INPC
{
    String configSectionName;
    String name;

    public NPC(String name, String configSectionName)
    {
        this.name = name;
        this.configSectionName = configSectionName;
        loadQuests();
    }

    void MessagePlayer(Player player, String message)
    {
        player.sendMessage(npcTag + ChatColor.YELLOW + " " + ChatColor.BOLD + name + " " + ChatColor.RESET + message);
    }

    public String getName()
    {
        return name;
    }

    HashMap<Material, String> loadMiningQuestTypes(String configSectionName)
    {
        // load quest types from config.yml
        HashMap<Material, String> questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getConfig();
        ConfigurationSection lumberjackMiningQuests = config.getConfigurationSection(configSectionName);

        if (lumberjackMiningQuests == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get the " + configSectionName + " section in your Config.yml");
            return null;
        }

        for (String materialName : lumberjackMiningQuests.getKeys(false))
        {
            ConfigurationSection difficultySection = lumberjackMiningQuests.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + materialName + " mining difficulty in the " + configSectionName + " section");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            String difficulty = difficultySection.getString("difficulty");

            questTypes.put(material, difficulty);
        }

        return questTypes;
    }

    HashMap<EntityType, String> loadKillingQuestTypes(String configSectionName)
    {
        // load quest types from config.yml
        HashMap<EntityType, String> questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getConfig();
        ConfigurationSection commanderKillingQuests = config.getConfigurationSection(configSectionName);

        if (commanderKillingQuests == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get " + configSectionName + " section in your Config.yml");
            return null;
        }

        for (String entityTypeName : commanderKillingQuests.getKeys(false))
        {
            ConfigurationSection difficultySection = commanderKillingQuests.getConfigurationSection(entityTypeName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + entityTypeName + " killing difficulty in the " + configSectionName + " section");
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

            questTypes.put(entityType, difficulty);
        }

        return questTypes;
    }
}