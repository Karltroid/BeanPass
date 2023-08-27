package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class CommanderNPC extends NPC
{
    HashMap<EntityType, String> questTypes;

    public CommanderNPC()
    {
        super();

        this.questVerb = "Kill";

        this.greetings = new String[]{
                "Greetings soldier.",
                "Greeting warrior."
        };
        this.farewells = new String[]{
                "Till next time, see you on the battlefield.",
                "Don't get yourself killed."
        };
        this.questAsks = new String[]{
                "Are you looking to help us take down a few targets? We could use your help."
        };
        this.differentQuestAsksP1 = new String[]{
                "You've already got your orders. "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or is that too tough, do you want a new order?"
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
        Quests.KillingQuest killingQuest = (Quests.KillingQuest) quest;
        return killingQuest.getGoalEntityType().name();
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

        playerData.giveQuest(new Quests.KillingQuest(playerData.getUUID(), this, goalEntityType, goalCount, playerCount, xpReward), alert);
    }
}
