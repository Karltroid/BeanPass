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

public class MinerNPC extends NPC
{
    HashMap<Material, String> questTypes;
    String[] greetings = new String[]{
            "Whats good splunker!",
            "Ah, the mighty miner, greetings!"
    };
    String[] farewells = new String[]{
            "See you in the mines!",
            "Be careful down there.",
            "Till next time, lucky mining!"
    };

    public MinerNPC(String name)
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

        if (previousQuest == null) MessagePlayer(player, "Are you looking to help us find some minerals?");
        else MessagePlayer(player, "The coal dust must be getting to your head. You need to " + previousQuest.getGoalDescription() + ". Or are ya having some troubles, want to look for something else?");

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
