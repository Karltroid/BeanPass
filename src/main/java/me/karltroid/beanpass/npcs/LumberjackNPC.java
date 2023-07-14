package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class LumberjackNPC extends NPC
{
    HashMap<Material, String> questTypes;
    String[] greetings = new String[]{
            "Don't you love the smell of freshly chopped wood? anyways-",
            "How goes it?"
    };
    String[] farewells = new String[]{
            "Don't let the trees fall on you, just kidding they float :l",
            "Till next time, get choppin!"
    };

    public LumberjackNPC(String name)
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

        Quests.MiningQuest previousQuest = (Quests.MiningQuest) playerData.getQuests().stream().filter(quest -> quest.getQuestGiver().name.equals(this.name)).findFirst().orElse(null);

        if (previousQuest == null) MessagePlayer(player, "My old man and I could use your help, are ya' willing?");
        else MessagePlayer(player, "Just like my old man, forgetting what you need to do. " + previousQuest.getGoalDescription() + ", remember. Or are you having trouble finding some, want to chop down some different lumber?");

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
