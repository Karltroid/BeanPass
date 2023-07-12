package me.karltroid.beanpass.npcs;

import org.bukkit.entity.Player;

import java.util.HashMap;

public interface INPC
{
    void loadQuests();
    void PromptQuestDifficulty(Player player);
    HashMap<?, String> getQuestTypes();
}
