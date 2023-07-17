package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests.CraftingQuest;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.Material;

import java.util.HashMap;

public class BlacksmithNPC extends BakerNPC
{
    public BlacksmithNPC()
    {
        super();

        this.questVerb = "Forge";

        this.greetings = new String[]{
                "Greetings, looking to dabble in the art of forgery I see."
        };
        this.farewells = new String[]{
                "Watch where you swing your weapons, a guard may think you're up to no good."
        };
        this.questAsks = new String[]{
                "The commander is requesting some forged weapons and materials. If you know your way around a forge, could you help?"
        };
        this.differentQuestAsksP1 = new String[]{
                "The heat from the forge must be getting to your head. I told you before, "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or is that too hard, want to forge something else?"
        };
    }
}
