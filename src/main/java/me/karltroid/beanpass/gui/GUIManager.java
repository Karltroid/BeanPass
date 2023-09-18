package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class GUIManager implements Listener
{
    private static final PlayerDataManager instance = new PlayerDataManager();
    private static final HashMap<Player, BeanPassGUI> activeGUIs = new HashMap<>();

    public static BeanPassGUI getGUI(Player player)
    {
        return activeGUIs.get(player);
    }

    public static void closeGUI(Player player)
    {
        if (!activeGUIs.containsKey(player)) return;
        activeGUIs.get(player).closeEntireGUI();
        activeGUIs.remove(player);
    }

    public static void openGUI(Player player, GUIMenu guiMenu)
    {
        BeanPassGUI alreadyOpenGUI = GUIManager.getGUI(player);
        if (alreadyOpenGUI != null) closeGUI(player);

        activeGUIs.put(player, new BeanPassGUI(player, guiMenu));
    }

    public static void closeAllGUIs() { for(Player player : Bukkit.getOnlinePlayers()) closeGUI(player); }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        GUIManager.closeGUI(player);
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity().getPlayer();
        closeGUI(player);
    }
}
