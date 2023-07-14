package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FisherNPC extends NPC
{
    HashMap<Material, String> questTypes;
    String[] greetings = new String[]{
            "Such a great day for fishing!"
    };
    String[] farewells = new String[]{
            "Watch out for drowned!",
            "Till next time, lucky fishing!"
    };

    public FisherNPC(String name)
    {
        super(name);
    }

    @Override
    public HashMap<Material, String> getQuestTypes()
    {
        return questTypes;
    }

    @Override
    public void loadQuests()
    {
        questTypes = loadMaterialQuestTypes();
    }

    @Override
    public void Interact(Player player)
    {
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        MessagePlayer(player, getRandomMessage(greetings));

        Quests.FishingQuest previousQuest = (Quests.FishingQuest) playerData.getQuests().stream().filter(quest -> quest.getQuestGiver().name.equals(this.name)).findFirst().orElse(null);

        if (previousQuest == null) MessagePlayer(player, "Haven't been having good luck with the water, think you can help catch something?");
        else MessagePlayer(player, "Did a puffer fish poison you or something? Don't you remember, " + previousQuest.getGoalDescription() + ". Or are ya not having good luck, want to catch something else?");

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
        Quests.FishingQuest fishingQuest = (Quests.FishingQuest) quest;
        return fishingQuest.getGoalItemType().name();
    }

    @Override
    public void giveQuest(PlayerData playerData, String goalType, int goalCount, int playerCount, double xpReward, boolean alert)
    {
        Material goalMaterial = null;
        if (goalType != null) goalMaterial = Material.valueOf(goalType);
        playerData.giveQuest(new Quests.FishingQuest(playerData.getUUID().toString(), this, goalMaterial, goalCount, playerCount, xpReward), alert);
    }
}
