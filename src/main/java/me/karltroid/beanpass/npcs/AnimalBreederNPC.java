package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class AnimalBreederNPC extends NPC
{
    HashMap<EntityType, String> questTypes;

    public AnimalBreederNPC()
    {
        super();

        this.questVerb = "Breed";

        this.greetings = new String[]{
                "Howdy!",
                "How ya doin' fella?"
        };
        this.farewells = new String[]{
                "Keep those animals fed!",
                "See ya on the range!",
                "Till next time, partner!"
        };
        this.questAsks = new String[]{
                "One too many people are helping out that dang Butcher. Could ya' help balance that out and breed some animals?",
                "Can't just be one person breeding these animals, mind helping me out?"
        };
        this.differentQuestAsksP1 = new String[]{
                "You silly, I already told you to ",
                "That cow manure gettin' to ya head, huh? I told you to "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or are there none of those on your range, do you want to breed something else?"
        };
    }

    @Override
    public HashMap<EntityType, String> getQuestTypes()
    {
        return questTypes;
    }

    @Override
    public void loadQuests()
    {
        questTypes = loadEntityQuestTypes();
    }

    @Override
    public String getQuestGoalType(Quests.Quest quest)
    {
        Quests.BreedingQuest breedingQuest = (Quests.BreedingQuest) quest;
        return breedingQuest.getGoalEntityType().name();
    }

    @Override
    public void giveQuest(PlayerData playerData, String goalType, int goalCount, int playerCount, double xpReward, boolean alert)
    {
        EntityType goalEntityType = null;
        if (goalType != null)
        {
            for (EntityType type : EntityType.values())
            {
                if (!type.name().equalsIgnoreCase(goalType)) continue;

                goalEntityType = type;
                break;
            }
        }

        playerData.giveQuest(new Quests.BreedingQuest(playerData.getUUID(), this, goalEntityType, goalCount, playerCount, xpReward), alert);
    }
}
