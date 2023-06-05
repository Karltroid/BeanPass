package me.karltroid.beanpass;

import me.karltroid.beanpass.Rewards.MoneyReward;
import me.karltroid.beanpass.Rewards.Reward;
import me.karltroid.beanpass.Rewards.SetHomeReward;
import me.karltroid.beanpass.Rewards.SkinReward;
import me.karltroid.beanpass.command.AddXP;
import me.karltroid.beanpass.command.OpenBeanPassGUI;
import me.karltroid.beanpass.command.ViewQuests;
import me.karltroid.beanpass.data.*;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.quests.QuestDifficulties;
import me.karltroid.beanpass.quests.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class BeanPass extends JavaPlugin implements Listener
{
    static BeanPass main;
    private FileConfiguration config;
    PluginManager pluginManager = getServer().getPluginManager();

    HashMap<UUID, PlayerData> playerData = new HashMap<>();
    Season season;
    public QuestDifficulties questDifficulties;
    public QuestManager questManager;
    DataManager dataManager;

    public static BeanPass getInstance(){ return main; }

    public Season getSeason() { return season; }

    public HashMap<Player, BeanPassGUI> activeGUIs = new HashMap<>();

    @Override
    public void onEnable()
    {
        main = this; // set singleton instance of the plugin

        // load config
        saveDefaultConfig();
        reloadConfig();
        config = getConfig();

        // get season data
        HashMap<Integer, Level> seasonLevels = new HashMap<>();
        // Check if the SeasonLevels section exists
        if (config.contains("SeasonLevels"))
        {
            // Get the SeasonRewards section
            ConfigurationSection seasonLevelsSection = config.getConfigurationSection("SeasonLevels");

            // Iterate through each season
            for (String season : seasonLevelsSection.getKeys(false))
            {
                int level = Integer.parseInt(season);
                System.out.println("Loading Level " + level);

                // Get the XP and Reward sections for the current season
                ConfigurationSection seasonSection = seasonLevelsSection.getConfigurationSection(season);
                if (seasonSection != null)
                {
                    // Get the XP value
                    int xp = seasonSection.getInt("XP");
                    System.out.println(level + " XP: " + xp);

                    // Get the Reward section
                    ConfigurationSection rewardSection = seasonSection.getConfigurationSection("Reward");
                    if (rewardSection != null)
                    {
                        // Get the Free rewards for the current season
                        ConfigurationSection freeSection = rewardSection.getConfigurationSection("Free");
                        Reward freeReward = null;
                        if (freeSection != null)
                        {
                            // Get the Type and Amount values
                            String freeType = freeSection.getString("Type");

                            switch (freeType)
                            {
                                case "MONEY":
                                    double moneyAmount = freeSection.getDouble("Amount");
                                    freeReward = new MoneyReward(moneyAmount);
                                    break;
                                case "SET_HOME":
                                    int homeAmount = freeSection.getInt("Amount");
                                    freeReward = new SetHomeReward(homeAmount);
                                    break;
                                case "SKIN":
                                    String skinName = freeSection.getString("Skin");
                                    freeReward = new SkinReward(Skins.database.get(skinName));
                                    break;
                            }
                        }
                        System.out.println("Free Null? " + (freeReward == null));

                        // Get the Paid rewards for the current season
                        ConfigurationSection paidSection = rewardSection.getConfigurationSection("Paid");
                        Reward premiumReward = null;
                        if (paidSection != null) {
                            // Get the Type, Item, and Skin values
                            String paidType = paidSection.getString("Type");

                            switch (paidType)
                            {
                                case "MONEY":
                                    int moneyAmount = paidSection.getInt("Amount");
                                    premiumReward = new MoneyReward(moneyAmount);
                                    break;
                                case "SET_HOME":
                                    int homeAmount = paidSection.getInt("Amount");
                                    premiumReward = new SetHomeReward(homeAmount);
                                    break;
                                case "SKIN":
                                    String skinName = paidSection.getString("Skin");
                                    premiumReward = new SkinReward(Skins.database.get(skinName));
                                    break;
                            }
                        }
                        System.out.println("Premium Null? " + (premiumReward == null));

                        seasonLevels.put(level, new Level(xp, freeReward, premiumReward));
                    }
                }
            }
        }
        int seasonNumber = config.getInt("Season", 1);
        season = new Season(seasonNumber, seasonLevels);

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
            if (activeGUIs.containsKey(player)) activeGUIs.get(player).closeEntireGUI();
        }

        // save config settings
        config.set("QuestsPerPlayer", questManager.getQuestsPerPlayer());
        saveConfig();
    }

    public PluginManager getPluginManager(){ return pluginManager; }

    public void addPlayerData(UUID uuid, PlayerData playerData) {
        this.playerData.put(uuid, playerData);
    }
    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    public boolean playerDataExists(UUID playerUUID) { return playerData.containsKey(playerUUID); }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (!activeGUIs.containsKey(player)) return;

        activeGUIs.get(player).closeEntireGUI();
    }
}
