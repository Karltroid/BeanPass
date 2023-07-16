package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class MinerNPC extends NPC
{
    HashMap<Material, String> questTypes;


    public MinerNPC(String name)
    {
        super(name);

        this.questVerb = "Mine";

        this.greetings = new String[]{
                "Whats good splunker!",
                "Ah, the mighty miner, greetings!"
        };
        this.farewells = new String[]{
                "See you in the mines!",
                "Be careful down there.",
                "Till next time, lucky mining!"
        };
        this.questAsks = new String[]{
                "Are you looking to help us find some minerals?"
        };
        this.differentQuestAsksP1 = new String[]{
                "The coal dust must be getting to your head. You need to "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or are ya having some troubles, want to look for something else?"
        };
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
    public String getQuestGoalType(Quests.Quest quest)
    {
        Quests.MiningQuest miningQuest = (Quests.MiningQuest) quest;
        return miningQuest.getGoalBlockType().name();
    }

    @Override
    public void giveQuest(PlayerData playerData, String goalType, int goalCount, int playerCount, double xpReward, boolean alert)
    {
        Material goalMaterial = null;
        if (goalType != null) goalMaterial = Material.valueOf(goalType);
        playerData.giveQuest(new Quests.MiningQuest(playerData.getUUID().toString(), this, goalMaterial, goalCount, playerCount, xpReward), alert);
    }
}
