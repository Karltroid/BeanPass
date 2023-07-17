package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.Material;

import java.util.HashMap;

public class FarmerNPC extends MinerNPC
{
    HashMap<Material, String> questTypes;

    public FarmerNPC()
    {
        super();

        this.questVerb = "Harvest";

        this.greetings = new String[]{
                "Howdy!",
                "Hows it go!?"
        };
        this.farewells = new String[]{
                "I'll get back to my farmin' now."
        };
        this.questAsks = new String[]{
                "I'm just one man feeding this whole city their bread and greens! Mind helping me out a tad?"
        };
        this.differentQuestAsksP1 = new String[]{
                "This new fertilizer formula I'm using must be getting to your head. Like I said before, "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or are ya having some troubles, want to harvest something else?"
        };
    }
}
