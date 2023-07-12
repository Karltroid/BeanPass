package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MinerNPC extends NPC
{
    HashMap<Material, String> questTypes;

    public MinerNPC()
    {
        this.configSectionName = "MinerMiningQuests";
        this.name = "Miner";

        loadQuests();
    }

    @Override
    public HashMap<Material, String> getQuestTypes()
    {
        return questTypes;
    }

    @Override
    public void loadQuests()
    {
        // load quest types from config.yml
        questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getConfig();
        ConfigurationSection minerMiningQuests = config.getConfigurationSection(configSectionName);

        if (minerMiningQuests == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get the " + configSectionName + " section in your Config.yml");
            return;
        }

        for (String materialName : minerMiningQuests.getKeys(false))
        {
            ConfigurationSection difficultySection = minerMiningQuests.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + materialName + " mining difficulty in the " + configSectionName + " section");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            String difficulty = difficultySection.getString("difficulty");

            questTypes.put(material, difficulty);
        }
    }

    @Override
    public void PromptQuestDifficulty(Player player)
    {
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());
        //playerData.giveQuest(new Quests.MiningQuest(playerData.getUUID().toString(), xpReward, goalEntityType, goalKillCount, playerKillCount));
    }
}
