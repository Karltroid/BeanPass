package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;

public abstract class NPC implements INPC
{
    String npcTag = ChatColor.GRAY + "" + ChatColor.BOLD + "[NPC]";
    String configSectionName;
    String name;

    void MessagePlayer(Player player, String message)
    {
        player.sendMessage(npcTag + ChatColor.YELLOW + " " + ChatColor.BOLD + name + " " + ChatColor.RESET + message);
    }

    CompletableFuture<Boolean> AskPlayer(Player player)
    {
        System.out.println("!");
        PlayerData playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());
        System.out.println("@");
        BeanPassGUI beanPassGUI = new BeanPassGUI(player, GUIMenu.YesNoQuestion);

        System.out.println("#");
        CompletableFuture<Boolean> answer = new CompletableFuture<>();
        System.out.println("$");
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                System.out.println("%");
                if(playerData.lastQuestionAnswer != null)
                {
                    System.out.println("^");
                    answer.complete(playerData.lastQuestionAnswer);
                    beanPassGUI.closeEntireGUI();
                    cancel();
                }
            }
        }.runTaskTimer(BeanPass.getInstance(), 0L, 10L);

        return answer;
    }
}