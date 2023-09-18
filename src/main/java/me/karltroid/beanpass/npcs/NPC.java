package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.data.PlayerDataManager;
import me.karltroid.beanpass.gui.GUIManager;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.quests.Quests;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class NPC implements INPC
{
    HashMap<UUID, Integer> questsGivenThisInstance = new HashMap<UUID, Integer>();
    String typeName;
    String questVerb;
    String[] greetings = new String[]{
            "Hey"
    };
    String[] farewells = new String[]{
            "Bye"
    };
    String[] questAsks = new String[]{
            "Want a quest?"
    };
    String[] differentQuestAsksP1 = new String[]{
            "I already have a quest for you, "
    };
    String[] differentQuestAsksP2 = new String[]{
            ". Or are you having trouble, do you want a new quest?"
    };

    public NPC()
    {
        this.typeName = this.getClass().getSimpleName().replace("NPC", "");
        loadQuests();
    }

    void MessagePlayer(Player player, String message)
    {
        player.sendMessage(npcTag + ChatColor.YELLOW + "" + ChatColor.BOLD + getTypeName() + ChatColor.DARK_GRAY + " " + ChatColor.BOLD + "» " + ChatColor.RESET + message);
    }

    public String getQuestVerb(){ return questVerb; }

    @Override
    public void Interact(Player player)
    {
        PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

        MessagePlayer(player, getRandomMessage(greetings));

        Quest previousQuest = playerData.getQuests().stream().filter(quest -> quest.getQuestGiver().typeName.equals(this.typeName)).findFirst().orElse(null);

        if (previousQuest == null) MessagePlayer(player, getRandomMessage(questAsks));
        else MessagePlayer(player, getRandomMessage(differentQuestAsksP1) + previousQuest.getGoalDescription() + getRandomMessage(differentQuestAsksP2));

        int questsGivenAlready = questsGivenThisInstance.getOrDefault(player.getUniqueId(), 0);
        if (questsGivenAlready >= BeanPass.getInstance().getNpcManager().questsPerNPCPerDay)
        {
            MessagePlayer(player, "Sorry I have no work for you right now, come back tomorrow and I may have something for you!");
            return;
        }

        if (playerData.responseFuture != null) playerData.responseFuture.complete(false);
        playerData.responseFuture = new CompletableFuture<>();
        AskPlayer(player);

        playerData.responseFuture.thenAccept(wantNewQuest -> {
            GUIManager.closeGUI(player);

            if (wantNewQuest)
            {
                if (previousQuest != null) playerData.removeQuest(previousQuest, true);
                giveQuest(playerData, null, -1, 0, -1, true);

                questsGivenThisInstance.put(player.getUniqueId(), questsGivenAlready + 1);
            }

            MessagePlayer(player, getRandomMessage(farewells));
        });
    }

    HashMap<Material, String> loadMaterialQuestTypes()
    {
        String configSectionName = getConfigSectionName();

        // load quest types from config.yml
        HashMap<Material, String> questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getQuestsConfig();
        ConfigurationSection materialQuest = config.getConfigurationSection(configSectionName);

        if (materialQuest == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get the material quest types in the " + configSectionName + " section in your Config.yml");
            return null;
        }

        for (String materialName : materialQuest.getKeys(false))
        {
            ConfigurationSection difficultySection = materialQuest.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + materialName + " mining difficulty in the " + configSectionName + " section");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            if (material == null)
            {
                BeanPass.getInstance().getLogger().warning(materialName + " does not exist. Check the " + configSectionName + " section in the config");
                continue;
            }
            String difficulty = difficultySection.getString("difficulty");

            questTypes.put(material, difficulty);
        }

        return questTypes;
    }

    HashMap<PotionType, String> loadBrewingQuestTypes()
    {
        String configSectionName = getConfigSectionName();

        // load quest types from config.yml
        HashMap<PotionType, String> questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getQuestsConfig();
        ConfigurationSection brewingQuest = config.getConfigurationSection(configSectionName);

        if (brewingQuest == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get the " + configSectionName + " section in your Config.yml");
            return null;
        }

        for (String potionTypeName : brewingQuest.getKeys(false))
        {
            ConfigurationSection difficultySection = brewingQuest.getConfigurationSection(potionTypeName);

            if (difficultySection == null)
            {
                BeanPass.getInstance().getLogger().warning("Couldn't get the " + potionTypeName + " mining difficulty in the " + configSectionName + " section");
                continue;
            }

            PotionType potionType = PotionType.valueOf(potionTypeName);
            String difficulty = difficultySection.getString("difficulty");

            questTypes.put(potionType, difficulty);
        }

        return questTypes;
    }

    HashMap<EntityType, String> loadEntityQuestTypes()
    {
        String configSectionName = getConfigSectionName();

        // load quest types from config.yml
        HashMap<EntityType, String> questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getQuestsConfig();
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

    public String getTypeName()
    {
        return typeName;
    }

    String getConfigSectionName()
    {
        return typeName + "Quests";
    }
}