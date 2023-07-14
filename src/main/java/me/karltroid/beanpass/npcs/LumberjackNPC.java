package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
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

    public LumberjackNPC(String name, String configSectionName)
    {
        super(name, configSectionName);
    }

    @Override
    public HashMap<Material, String> getQuestTypes()
    {
        return questTypes;
    }

    @Override
    public void loadQuests()
    {
        questTypes = loadMiningQuestTypes(this.configSectionName);
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

                Quests.MiningQuest miningQuest = new Quests.MiningQuest(player.getUniqueId().toString(), -1, null, -1, 0, this);
                playerData.giveQuest(miningQuest, true);
            }

            MessagePlayer(player, getRandomMessage(farewells));
        });
    }
}
