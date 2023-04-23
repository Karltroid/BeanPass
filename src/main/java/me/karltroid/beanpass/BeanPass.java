package me.karltroid.beanpass;

import me.karltroid.beanpass.command.AddXP;
import me.karltroid.beanpass.command.OpenBeanPassGUI;
import me.karltroid.beanpass.data.Hats;
import me.karltroid.beanpass.data.Seasons;
import me.karltroid.beanpass.gui.BeanPassGUI;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BeanPass extends JavaPlugin implements Listener
{
    public static BeanPass main;
    public int activeSeason = 1;

    Hats hats = new Hats();
    Seasons seasons = new Seasons();
    public BeanPassGUI beanPassGUI = new BeanPassGUI();

    public Seasons.Season getActiveSeason() { return seasons.getSeason(activeSeason); }

    @Override
    public void onEnable()
    {
        main = this; // set singleton instance of the plugin

        // register event listeners to the plugin instance
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(beanPassGUI, this);

        // register the commands for the plugin instance

        main.getCommand("beanpass").setExecutor(new OpenBeanPassGUI());
        main.getCommand("beanpass-addxp").setExecutor(new AddXP());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
}
