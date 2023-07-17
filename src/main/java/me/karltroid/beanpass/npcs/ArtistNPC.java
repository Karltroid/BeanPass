package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests.CraftingQuest;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.Material;

import java.util.HashMap;

public class ArtistNPC extends BakerNPC
{
    public ArtistNPC()
    {
        super();

        this.questVerb = "Craft";

        this.greetings = new String[]{
                "Cant. Stop. Painting.",
                "Isn't this view just wonderful, it paints itself!"
        };
        this.farewells = new String[]{
                "Bye, if you need me i'll be here painting!"
        };
        this.questAsks = new String[]{
                "With everyone buying my paintings I need to keep painting FAST, but I need more dye! Could you help me get the dye I need?"
        };
        this.differentQuestAsksP1 = new String[]{
                "You weren't sniffing the paint were you? Like I asked, please "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Or is that too hard, want to fetch me a different color dye?"
        };
    }
}
