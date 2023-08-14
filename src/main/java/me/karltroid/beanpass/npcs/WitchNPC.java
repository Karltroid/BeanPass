package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests.BrewingQuest;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class WitchNPC extends NPC
{
    HashMap<PotionType, String> questTypes;

    public WitchNPC()
    {
        super();

        this.questVerb = "Brew";

        this.greetings = new String[]{
                "HEHEHE-oh hey! uh.. nothing to see here!"
        };
        this.farewells = new String[]{
                "See you soon, happy brewing HEHEHE!",
                "Don't drop your potions! Your dog might lick it up... stupid boy."
        };
        this.questAsks = new String[]{
                "I make some great brews, but I'm just one witch! Would you help an old woman out?"
        };
        this.differentQuestAsksP1 = new String[]{
                "Let me guess, sniffing those fermented spider eyes. Like I told you, "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or do you want to brew something else for me?"
        };
    }

    @Override
    public HashMap<PotionType, String> getQuestTypes()
    {
        return questTypes;
    }

    @Override
    public void loadQuests()
    {
        questTypes = loadBrewingQuestTypes();
    }

    @Override
    public String getQuestGoalType(Quest quest)
    {
        BrewingQuest brewingQuest = (BrewingQuest) quest;
        return brewingQuest.getGoalItemType().name();
    }

    @Override
    public void giveQuest(PlayerData playerData, String goalType, int goalCount, int playerCount, double xpReward, boolean alert)
    {
        PotionType goalPotionType = null;
        if (goalType != null) goalPotionType = PotionType.valueOf(goalType);
        playerData.giveQuest(new BrewingQuest(playerData.getUUID(), this, goalPotionType, goalCount, playerCount, xpReward), alert);
    }
}
