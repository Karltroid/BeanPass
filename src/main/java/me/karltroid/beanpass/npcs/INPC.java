package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

public interface INPC
{
    String npcTag = ChatColor.GRAY + "" + ChatColor.BOLD + "[NPC]";

    void loadQuests();
    void Interact(Player player);
    default String getRandomMessage(String[] messages)
    {
        Random random = new Random();
        return messages[random.nextInt(messages.length)];
    }
    HashMap<?, String> getQuestTypes();
    default void AskPlayer(Player player)
    {
        new BeanPassGUI(player, GUIMenu.YesNoQuestion);
    }
}
