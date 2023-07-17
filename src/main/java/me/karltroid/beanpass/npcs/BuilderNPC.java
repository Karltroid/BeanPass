package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.Material;

import java.util.HashMap;

public class BuilderNPC extends MinerNPC
{
    public BuilderNPC()
    {
        super();

        this.questVerb = "Mine";

        this.greetings = new String[]{
                "Heyo, gonna keep staring at our work here or help us out"
        };
        this.farewells = new String[]{
                "Alright, I'll be here doing hard \"work\"",
                "I'm paid by the hour so take your time."
        };
        this.questAsks = new String[]{
                "I don't have enough workers to help get materials for this house, mind lending a hand?"
        };
        this.differentQuestAsksP1 = new String[]{
                "Did you not follow OSHA guidelines and hit your head or something? I told you "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or are ya having some troubles finding that, want to look for something else?"
        };
    }
}
