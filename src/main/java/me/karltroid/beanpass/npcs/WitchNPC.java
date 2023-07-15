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

    public WitchNPC(String name)
    {
        super(name);

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
    public void Interact(Player player)
    {
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        MessagePlayer(player, getRandomMessage(greetings));

        BrewingQuest previousQuest = (BrewingQuest) playerData.getQuests().stream().filter(quest -> quest.getQuestGiver().name.equals(this.name)).findFirst().orElse(null);

        if (previousQuest == null) MessagePlayer(player, getRandomMessage(questAsks));
        else MessagePlayer(player, getRandomMessage(differentQuestAsksP1) + previousQuest.getGoalDescription() + getRandomMessage(differentQuestAsksP2));

        playerData.responseFuture = new CompletableFuture<>();
        AskPlayer(player);

        playerData.responseFuture.thenAccept(wantNewQuest -> {
            if (wantNewQuest)
            {
                if (previousQuest != null) playerData.removeQuest(previousQuest, true);
                giveQuest(playerData, null, -1, 0, -1, true);
            }

            MessagePlayer(player, getRandomMessage(farewells));
        });
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
        playerData.giveQuest(new BrewingQuest(playerData.getUUID().toString(), this, goalPotionType, goalCount, playerCount, xpReward), alert);
    }
}
