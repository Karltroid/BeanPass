package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ButcherNPC extends CommanderNPC
{
    public ButcherNPC()
    {
        super();

        this.greetings = new String[]{
                "Hello!",
                "Whats good?"
        };
        this.farewells = new String[]{
                "Bye!",
                "See you later."
        };
        this.questAsks = new String[]{
                "I need some fresh kill for the shop, can you hunt down some for me?"
        };
        this.differentQuestAsksP1 = new String[]{
                "I still need you to "
        };
        this.differentQuestAsksP2 = new String[]{
                ". Are you having trouble, you can get me a different kind of meat if you want?"
        };
    }
}
