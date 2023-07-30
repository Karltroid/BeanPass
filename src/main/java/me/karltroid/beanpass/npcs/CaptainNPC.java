package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public class CaptainNPC extends NPC
{
    HashMap<EntityType, String> questTypes;

    public CaptainNPC()
    {
        super();

        this.questVerb = "Loot";

        this.greetings = new String[]{
                "Yarr, captain!",
                "Aye, aye!"
        };
        this.farewells = new String[]{
                "Farewell traveller",
                "Careful on the seas!"
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

        playerData.giveQuest(new Quests.KillingQuest(playerData.getUUID().toString(), this, goalEntityType, goalCount, playerCount, xpReward), alert);
    }
}
