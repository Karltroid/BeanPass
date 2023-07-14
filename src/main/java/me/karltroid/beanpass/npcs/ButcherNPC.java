package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ButcherNPC extends NPC
{
    HashMap<EntityType, String> questTypes;
    String[] greetings = new String[]{
            "Hello!",
            "Whats good?"
    };
    String[] farewells = new String[]{
            "Bye!",
            "See you later."
    };

    public ButcherNPC(String name)
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

        if (previousQuest == null) MessagePlayer(player, "I need some fresh kill for the shop, can you hunt down some for me?");
        else MessagePlayer(player, "I still need you to " + previousQuest.getGoalDescription() + ". Are you having trouble, you can get me a different kind of meat if you want?");

        playerData.responseFuture = new CompletableFuture<>();
        AskPlayer(player);

        playerData.responseFuture.thenAccept(wantNewQuest -> {
            if (wantNewQuest)
            {
                if (previousQuest != null) playerData.removeQuest(previousQuest, true);

                Quests.KillingQuest killingQuest = new Quests.KillingQuest(player.getUniqueId().toString(), -1, null, -1, 0, this);
                playerData.giveQuest(killingQuest, true);
            }

            MessagePlayer(player, getRandomMessage(farewells));
        });
    }
}
