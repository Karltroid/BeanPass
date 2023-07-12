package me.karltroid.beanpass;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import me.karltroid.beanpass.Rewards.*;
import me.karltroid.beanpass.command.*;
import me.karltroid.beanpass.data.*;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.mounts.MountManager;
import me.karltroid.beanpass.npcs.NPCManager;
import me.karltroid.beanpass.quests.QuestDifficulties;
import me.karltroid.beanpass.quests.QuestManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    private NPCManager npcManager;
    public QuestManager questManager;
    PlayerDataManager dataManager;
    public static BeanPass getInstance(){ return main; }
    public Season getSeason() { return season; }
    public HashMap<Player, BeanPassGUI> activeGUIs = new HashMap<>();
    private Economy econ = null;
    private Essentials ess;
    private ProtocolManager protocolManager;

    public SkinManager skinManager;
    public MountManager mountManager;

    @Override
    public void onEnable()
    {
        main = this; // set singleton instance of the plugin

        if (Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib not found. Disabling the plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize ProtocolManager
        protocolManager = ProtocolLibrary.getProtocolManager();

        ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (ess == null)
        {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Essentials dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // load config
        saveDefaultConfig();
        reloadConfig();
        config = getConfig();

        skinManager = new SkinManager();
        mountManager = new MountManager();

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

                // Get the XP and Reward sections for the current season
                ConfigurationSection seasonSection = seasonLevelsSection.getConfigurationSection(season);
                if (seasonSection != null)
                {
                    // Get the XP value
                    int xp = seasonSection.getInt("XP");

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
                                    freeReward = new SkinReward(skinManager.getSkinByName(skinName.toLowerCase()));
                                    break;
                                case "MOUNT":
                                    String mountName = freeSection.getString("Mount");
                                    freeReward = new MountReward(mountManager.getMountByName(mountName.toLowerCase()));
                                    break;
                            }
                        }

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
                                    premiumReward = new SkinReward(skinManager.getSkinByName(skinName.toLowerCase()));
                                    break;
                                case "MOUNT":
                                    String mountName = paidSection.getString("Mount");
                                    premiumReward = new MountReward(mountManager.getMountByName(mountName.toLowerCase()));
                                    break;
                                default:
                                    continue;
                            }
                        }

                        seasonLevels.put(level, new Level(xp, freeReward, premiumReward));
                    }
                }
            }
        }
        int seasonNumber = config.getInt("Season", 1);
        season = new Season(seasonNumber, seasonLevels);

        npcManager = new NPCManager();
        dataManager = new PlayerDataManager();
        questDifficulties = new QuestDifficulties();
        questManager = new QuestManager();

        // register event listeners to the plugin instance
        pluginManager.registerEvents(dataManager, this);
        pluginManager.registerEvents(questManager, this);
        pluginManager.registerEvents(skinManager, this);
        pluginManager.registerEvents(mountManager, this);

        // register the commands for the plugin instance
        main.getCommand("beanpass").setExecutor(new BeanPassCommand());
        main.getCommand("quests").setExecutor(new OpenBeanPassPage(GUIMenu.Quests));
        main.getCommand("rewards").setExecutor(new OpenBeanPassPage(GUIMenu.Rewards));
        main.getCommand("mounts").setExecutor(new OpenBeanPassPage(GUIMenu.Mounts));
        main.getCommand("hats").setExecutor(new OpenBeanPassPage(GUIMenu.Hats));
        main.getCommand("tools").setExecutor(new OpenBeanPassPage(GUIMenu.Tools));
        main.getCommand("sethome").setExecutor(new SetHome());
        main.getCommand("givequest").setExecutor(new GiveQuest());
    }

    public FileConfiguration getBeanPassConfig() { return config; }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers())
        {
            dataManager.savePlayerData(player.getUniqueId());
            if (activeGUIs.containsKey(player)) activeGUIs.get(player).closeEntireGUI();
            mountManager.destroyMountInstance(player);
        }

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

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy()
    {
        return econ;
    }

    public Essentials getEssentials()
    {
        return ess;
    }

    public static void sendMessage(OfflinePlayer p, String message)
    {
        if (!p.isOnline()) return;

        Player player = (Player) p;
        player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[BeanPass] " + ChatColor.RESET + message);
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public MountManager getMountManager() {
        return mountManager;
    }
    public NPCManager getNpcManager() { return npcManager; }
}
