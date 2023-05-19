package me.karltroid.beanpass;

import me.karltroid.beanpass.command.AddXP;
import me.karltroid.beanpass.command.OpenBeanPassGUI;
import me.karltroid.beanpass.command.ViewQuests;
import me.karltroid.beanpass.data.DataManager;
import me.karltroid.beanpass.data.Hats;
import me.karltroid.beanpass.data.Seasons;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.quests.QuestDifficulties;
import me.karltroid.beanpass.quests.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class BeanPass extends JavaPlugin implements Listener
{
    public static BeanPass main;
    private FileConfiguration config;
    PluginManager pluginManager = getServer().getPluginManager();
    public int activeSeason = 1;

    Hats hats = new Hats();
    Seasons seasons = new Seasons();
    public QuestDifficulties questDifficulties;
    public QuestManager questManager;
    DataManager dataManager;




    public Seasons.Season getActiveSeason() { return seasons.getSeason(activeSeason); }

    public HashMap<Player, BeanPassGUI> activeGUIs = new HashMap<>();

    @Override
    public void onEnable()
    {
        main = this; // set singleton instance of the plugin

        // load config
        saveDefaultConfig();
        reloadConfig();
        config = getConfig();

        dataManager = new DataManager();
        questDifficulties = new QuestDifficulties();
        questManager = new QuestManager(config.getInt("QuestsPerPlayer", 5));

        // register event listeners to the plugin instance
        pluginManager.registerEvents(dataManager, this);
        pluginManager.registerEvents(questManager, this);

        // register the commands for the plugin instance

        main.getCommand("beanpass").setExecutor(new OpenBeanPassGUI());
        main.getCommand("beanpass-addxp").setExecutor(new AddXP());

        main.getCommand("quests").setExecutor(new ViewQuests());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers())
        {
            dataManager.savePlayerData(player.getUniqueId());
            if (activeGUIs.containsKey(player)) activeGUIs.get(player).close();
        }

        // save config settings
        config.set("QuestsPerPlayer", questManager.getQuestsPerPlayer());
        saveConfig();
    }

    public PluginManager getPluginManager(){ return pluginManager; }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (!activeGUIs.containsKey(player)) return;

        activeGUIs.get(player).close();
    }
}
