package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.QuestDifficulties;
import me.karltroid.beanpass.quests.Quests;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CommanderNPC extends NPC
{
    HashMap<EntityType, String> questTypes;

    public CommanderNPC()
    {
        this.configSectionName = "CommanderKillingQuests";
        this.name = "Commander";

        loadQuests();
    }

    @Override
    public HashMap<EntityType, String> getQuestTypes()
    {
        return questTypes;
    }

    @Override
    public void loadQuests()
    {
        // load quest types from config.yml
        questTypes = new HashMap<>();
        FileConfiguration config = BeanPass.getInstance().getConfig();
        ConfigurationSection commanderKillingQuests = config.getConfigurationSection(configSectionName);

        if (commanderKillingQuests == null)
        {
            BeanPass.getInstance().getLogger().warning("Couldn't get " + configSectionName + " section in your Config.yml");
            return;
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
    }

    @Override
    public void PromptQuestDifficulty(Player player)
    {
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        MessagePlayer(player, "Greetings soldier.");

        Quests.KillingQuest givenQuest = null;
        for (Quests.Quest quest : playerData.getQuests())
        {
            if (!quest.getQuestGiver().name.equals(this.name)) continue;

            givenQuest = (Quests.KillingQuest) quest;
            break;
        }

        if (givenQuest == null) MessagePlayer(player, "Are you looking to help us \"eliminate\" a few targets? We could use your help.");
        else MessagePlayer(player, "You've already got your orders. " + givenQuest.getGoalDescription() + ". Or is that too tough, do you want a new order?");

        System.out.println("a");
        CompletableFuture.runAsync(() -> AskPlayer(player).thenAccept(wantNewQuest ->
        {
            System.out.println("b");
            if (wantNewQuest)
            {
                // future me: ask if they want a easy, normal, or hard quest
                System.out.println("c");
                Quests.KillingQuest killingQuest = new Quests.KillingQuest(player.getUniqueId().toString(), -1, null, -1, 0, this);
                playerData.giveQuest(killingQuest, true);
            }

            MessagePlayer(player, "Till next time, see you on the battlefield.");
        }));
    }
}