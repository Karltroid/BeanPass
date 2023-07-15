package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class LumberjackNPC extends MinerNPC
{
    public LumberjackNPC(String name)
    {
        super(name);

        this.greetings = new String[]{
                "Don't you love the smell of freshly chopped wood? anyways-",
                "How goes it?"
        };
        this.farewells = new String[]{
                "Don't let the trees fall on you, just kidding they float :l",
                "Till next time, get choppin!"
        };
        this.questAsks = new String[]{
                "My old man and I could use your help, are ya' willing?"
        };
        this.differentQuestAsksP1 = new String[]{
                "Just like my old man, forgetting what you need to do. "
        };
        this.differentQuestAsksP2 = new String[]{
                ", remember. Or are you having trouble finding some, want to chop down some different lumber?"
        };
    }
}
