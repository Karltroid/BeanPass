package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class CommanderNPC extends NPC
{
    HashMap<EntityType, String> questTypes;
    String[] greetings = new String[]{
            "Greetings soldier.",
            "Greeting warrior."
    };
    String[] farewells = new String[]{
            "Till next time, see you on the battlefield.",
            "Don't get yourself killed."
    };

    public CommanderNPC(String name)
    {
        super(name);
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
    public void Interact(Player player)
    {
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        MessagePlayer(player, getRandomMessage(greetings));

        Quests.KillingQuest previousQuest = (Quests.KillingQuest) playerData.getQuests().stream().filter(quest -> quest.getQuestGiver().name.equals(this.name)).findFirst().orElse(null);

        if (previousQuest == null) MessagePlayer(player, "Are you looking to help us take down a few targets? We could use your help.");
        else MessagePlayer(player, "You've already got your orders. " + previousQuest.getGoalDescription() + ". Or is that too tough, do you want a new order?");

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
