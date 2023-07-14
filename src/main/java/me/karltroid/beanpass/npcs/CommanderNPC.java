package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.QuestDifficulties;
import me.karltroid.beanpass.quests.Quests;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;
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

                Quests.KillingQuest killingQuest = new Quests.KillingQuest(player.getUniqueId().toString(), -1, null, -1, 0, this);
                playerData.giveQuest(killingQuest, true);
            }

            MessagePlayer(player, getRandomMessage(farewells));
        });
    }
}
