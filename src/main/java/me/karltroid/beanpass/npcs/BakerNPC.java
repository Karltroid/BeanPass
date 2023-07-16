package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import me.karltroid.beanpass.quests.Quests.CraftingQuest;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class BakerNPC extends FisherNPC
{
    HashMap<Material, String> questTypes;

    public BakerNPC(String name)
    {
        super(name);

        this.questVerb = "Bake";

        this.greetings = new String[]{
                "Hello! Don't you love the smell of fresh bread."
        };
        this.farewells = new String[]{
                "Don't eat too many cookies, or it'll go right to your thighs!"
        };
        this.questAsks = new String[]{
                "This town is growing a sweet tooth or something, could you help me bake some product?"
        };
        this.differentQuestAsksP1 = new String[]{
                "One too many loaves of bread put you in a food coma, huh. I need you to "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or is that too hard, want to bake something else?"
        };
    }

    @Override
    public String getQuestGoalType(Quest quest)
    {
        CraftingQuest craftingQuest = (CraftingQuest) quest;
        return craftingQuest.getGoalItemType().name();
    }

    @Override
    public void giveQuest(PlayerData playerData, String goalType, int goalCount, int playerCount, double xpReward, boolean alert)
    {
        Material goalMaterial = null;
        if (goalType != null) goalMaterial = Material.valueOf(goalType);
        playerData.giveQuest(new CraftingQuest(playerData.getUUID().toString(), this, goalMaterial, goalCount, playerCount, xpReward), alert);
    }
}
