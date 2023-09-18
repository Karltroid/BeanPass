package me.karltroid.beanpass.data;

import me.clip.placeholderapi.PlaceholderAPI;
import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.GUIManager;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.hooks.DiscordSRVHook;
import me.karltroid.beanpass.mounts.Mount;
import me.karltroid.beanpass.mounts.MountManager;
import me.karltroid.beanpass.npcs.NPC;
import me.karltroid.beanpass.quests.Quests.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getPlayer;


public class PlayerDataManager implements Listener
{
    private static final PlayerDataManager instance = new PlayerDataManager();
    private static HashMap<UUID, PlayerData> playerData;
    private static String DATABASE_NAME;
    private static String PLAYER_SEASON_DATA_TABLE_NAME;
    private static String PLAYER_REWARDS_TABLE_NAME;
    private static String PLAYER_QUESTS_TABLE_NAME;

    public PlayerDataManager()
    {
        playerData = new HashMap<>();
        DATABASE_NAME = "database.db";
        PLAYER_SEASON_DATA_TABLE_NAME = "player_season_" + BeanPass.getInstance().getSeason().getId() + "_data";
        PLAYER_REWARDS_TABLE_NAME = "player_rewards";
        PLAYER_QUESTS_TABLE_NAME = "player_quests";
        createTables();

        // save everyone's data every 5 minutes
        Bukkit.getServer().getScheduler().runTaskTimer(BeanPass.getInstance(), this::saveAllPlayerData, 6000, 6000);
    }

    public static PlayerDataManager getInstance() { return instance; }

    private static Connection getConnection(Plugin plugin) throws SQLException {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        String databasePath = dataFolder.getAbsolutePath() + File.separator + DATABASE_NAME;
        String jdbcUrl = "jdbc:sqlite:" + databasePath;
        return DriverManager.getConnection(jdbcUrl);
    }

    private static void createTables() {
        try (Connection conn = getConnection(BeanPass.getInstance());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_SEASON_DATA_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL PRIMARY KEY,"
                            + "premium BOOLEAN NOT NULL,"
                            + "xp DOUBLE NOT NULL,"
                            + "last_known_level INT NOT NULL,"
                            + "max_homes INT NOT NULL"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_REWARDS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "reward_type VARCHAR(36) NOT NULL,"
                            + "reward_id VARCHAR(16) NOT NULL,"
                            + "equipped BOOLEAN NOT NULL DEFAULT FALSE"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_QUESTS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "quest_giver VARCHAR(36) NOT NULL,"
                            + "goal_type VARCHAR(36) NOT NULL,"
                            + "goal_count VARCHAR(36) NOT NULL,"
                            + "player_count VARCHAR(36) NOT NULL,"
                            + "xp_reward DOUBLE NOT NULL,"
                            + "PRIMARY KEY (uuid, quest_giver, goal_type, goal_count)"
                            + ")"
            );

            //checkTableColumn(stmt, PLAYER_SEASON_DATA_TABLE_NAME, "max_warps", "INT", "0");
        } catch (SQLException e) {
            getLogger().severe("Failed to create database table: " + e.getMessage());
        }
    }

    private void checkTableColumn(Statement stmt, String tableName, String columnName, String dataType, String defaultValue) throws SQLException {
        ResultSetMetaData playerSeasonDataMetaData = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 0").getMetaData();
        if (!hasColumn((ResultSet) playerSeasonDataMetaData, columnName)) {
            stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType + " NOT NULL DEFAULT " + defaultValue);
        }
    }

    private static boolean hasColumn(ResultSet rs, String columnName)
    {
        try
        {
            rs.findColumn(columnName);
            return true;
        }
        catch (SQLException e)
        {
            return false;
        }
    }

    public static PlayerData getPlayerData(UUID uuid)
    {
        if (playerDataLoaded(uuid)) return playerData.get(uuid);

        try (Connection conn = getConnection(BeanPass.getInstance()))
        {
            boolean premium = false;
            double xp = 0.0;
            int lastKnownLevel = 1;
            int maxHomes = 0;

            try (PreparedStatement playerSeasonDataStatement = conn.prepareStatement("SELECT * FROM " + PLAYER_SEASON_DATA_TABLE_NAME + " WHERE uuid = ?"))
            {
                playerSeasonDataStatement.setString(1, uuid.toString());

                try (ResultSet playerSeasonDataResult = playerSeasonDataStatement.executeQuery())
                {
                    if (playerSeasonDataResult.next())
                    {
                        if (hasColumn(playerSeasonDataResult, "premium"))
                            premium = playerSeasonDataResult.getBoolean("premium");

                        if (hasColumn(playerSeasonDataResult, "xp"))
                            xp = playerSeasonDataResult.getDouble("xp");

                        if (hasColumn(playerSeasonDataResult, "last_known_level"))
                            lastKnownLevel = playerSeasonDataResult.getInt("last_known_level");

                        if (hasColumn(playerSeasonDataResult, "max_homes"))
                            maxHomes = playerSeasonDataResult.getInt("max_homes");
                    }
                }
            }

            PlayerData playerData = new PlayerData(uuid, premium, new ArrayList<>(), new ArrayList<>(), xp, lastKnownLevel, maxHomes);
            addPlayerData(uuid, playerData);

            try (PreparedStatement playerRewardsStatement = conn.prepareStatement("SELECT * FROM player_rewards WHERE uuid = ?"))
            {
                playerRewardsStatement.setString(1, uuid.toString());

                try (ResultSet playerRewardsResult = playerRewardsStatement.executeQuery())
                {
                    while (playerRewardsResult.next())
                    {
                        String rewardType = playerRewardsResult.getString("reward_type");
                        int rewardID = Integer.parseInt(playerRewardsResult.getString("reward_id"));
                        boolean equipped = playerRewardsResult.getBoolean("equipped");

                        switch (rewardType)
                        {
                            case "SKIN":

                                Skin skin = SkinManager.getSkinById(rewardID);
                                playerData.giveSkin(skin, false);
                                if (equipped) playerData.equipSkin(skin, false);
                                break;
                            case "MOUNT":
                                Mount mount = MountManager.getMountById(rewardID);
                                playerData.giveMount(mount, false);
                                if (equipped) playerData.equipMount(mount, false);
                                break;
                            default:
                                BeanPass.getInstance().getLogger().warning("Problem loading " + playerData.player.getName() + "'s reward data: Reward type does not exist");
                                break;
                        }
                    }
                }
            }

            try (PreparedStatement playerQuestStatement = conn.prepareStatement("SELECT * FROM " + PLAYER_QUESTS_TABLE_NAME + " WHERE uuid = ?")) {
                playerQuestStatement.setString(1, uuid.toString());

                try (ResultSet playerQuestResult = playerQuestStatement.executeQuery())
                {
                    while (playerQuestResult.next())
                    {
                        double xpReward = playerQuestResult.getDouble("xp_reward");
                        String goalType = playerQuestResult.getString("goal_type");
                        int goalCount = Integer.parseInt(playerQuestResult.getString("goal_count"));
                        int playerCount = Integer.parseInt(playerQuestResult.getString("player_count"));
                        NPC questGiver = BeanPass.getInstance().getNpcManager().getNPCByTypeName(playerQuestResult.getString("quest_giver"));

                        questGiver.giveQuest(playerData, goalType, goalCount, playerCount, xpReward, false);
                    }
                }
            }
            catch (SQLException e)
            {
                getLogger().severe(e.getMessage());
            }

            return playerData;
        }
        catch (SQLException e)
        {
            getLogger().severe("Failed to load data from database: " + e.getMessage());
        }

        return null;
    }

    public static void unloadPlayerData(UUID uuid)
    {
        savePlayerData(uuid);
        playerData.remove(uuid);
    }

    public static void unloadAllPlayerData()
    {
        for(Map.Entry<UUID, PlayerData> player : playerData.entrySet()) unloadPlayerData(player.getKey());
    }

    public static void savePlayerData(UUID uuid)
    {
        if (!playerDataLoaded(uuid)) return;
        PlayerData playerData = getPlayerData(uuid);

        try (Connection conn = getConnection(BeanPass.getInstance());
             PreparedStatement playerSeasonDataStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_SEASON_DATA_TABLE_NAME + " (uuid, xp, premium, last_known_level, max_homes) VALUES (?, ?, ?, ?, ?) " +
                             "ON CONFLICT(uuid) DO UPDATE SET xp = excluded.xp, premium = excluded.premium, last_known_level = excluded.last_known_level, max_homes = excluded.max_homes"
             );
             PreparedStatement deletePlayerRewardsStatement = conn.prepareStatement(
                     "DELETE FROM " + PLAYER_REWARDS_TABLE_NAME + " WHERE uuid = ?"
             );
             PreparedStatement insertPlayerRewardsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_REWARDS_TABLE_NAME + " (uuid, reward_type, reward_id, equipped) VALUES (?, ?, ?, ?)"
             );
             PreparedStatement deletePlayerQuestsStatement = conn.prepareStatement(
                     "DELETE FROM " + PLAYER_QUESTS_TABLE_NAME + " WHERE uuid = ?"
             );
             PreparedStatement insertPlayerQuestsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_QUESTS_TABLE_NAME + " (uuid, quest_giver, goal_type, goal_count, player_count, xp_reward) VALUES (?, ?, ?, ?, ?, ?)" +
                             "ON CONFLICT(uuid, quest_giver, goal_type, goal_count) DO UPDATE SET player_count = excluded.player_count"
             )

        ) {
            // Insert or update player_season_data table
            playerSeasonDataStatement.setString(1, uuid.toString());
            playerSeasonDataStatement.setDouble(2, playerData.xp);
            playerSeasonDataStatement.setBoolean(3, playerData.premium);
            playerSeasonDataStatement.setInt(4, playerData.lastKnownLevel);
            playerSeasonDataStatement.setInt(5, playerData.maxHomes);
            playerSeasonDataStatement.executeUpdate();

            // Delete all player_skins and player_mounts for the player
            deletePlayerRewardsStatement.setString(1, uuid.toString());
            deletePlayerRewardsStatement.executeUpdate();

            // Insert new player_skins for the player
            for (Integer skinID : playerData.ownedSkins)
            {
                insertPlayerRewardsStatement.setString(1, uuid.toString());
                insertPlayerRewardsStatement.setString(2, "SKIN");
                insertPlayerRewardsStatement.setString(3, skinID.toString());
                insertPlayerRewardsStatement.setBoolean(4, playerData.equippedSkins.contains(SkinManager.getSkinById(skinID)));
                insertPlayerRewardsStatement.addBatch();
            }
            insertPlayerRewardsStatement.executeBatch();

            // Insert new player_mounts for the player
            for (Integer mountID : playerData.ownedMounts)
            {
                insertPlayerRewardsStatement.setString(1, uuid.toString());
                insertPlayerRewardsStatement.setString(2, "MOUNT");
                insertPlayerRewardsStatement.setString(3, mountID.toString());
                insertPlayerRewardsStatement.setBoolean(4, playerData.equippedMounts.contains(MountManager.getMountById(mountID)));
                insertPlayerRewardsStatement.addBatch();
            }
            insertPlayerRewardsStatement.executeBatch();

            deletePlayerQuestsStatement.setString(1, uuid.toString());
            deletePlayerQuestsStatement.executeUpdate();
            for (Quest quest : playerData.getQuests())
            {
                NPC questGiver = quest.getQuestGiver();
                insertPlayerQuestsStatement.setString(1, quest.playerUUID.toString());
                insertPlayerQuestsStatement.setString(2, BeanPass.getInstance().getNpcManager().getNPCTypeNameFromObject(questGiver));
                insertPlayerQuestsStatement.setString(3, questGiver.getQuestGoalType(quest));
                insertPlayerQuestsStatement.setInt(4, quest.goalCount);
                insertPlayerQuestsStatement.setInt(5, quest.playerCount);
                insertPlayerQuestsStatement.setDouble(6, quest.xpReward);
                insertPlayerQuestsStatement.addBatch();
            }
            insertPlayerQuestsStatement.executeBatch();
        }
        catch (SQLException e)
        {
            getLogger().severe("Failed to save data to database: " + e.getMessage());
        }
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        PlayerData playerData = getPlayerData(player.getUniqueId());
        if (playerData.lastKnownLevel < playerData.getLevel()) playerData.leveledUp(); // level up player if they got xp while offline
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        unloadPlayerData(playerUUID);
    }

    void saveAllPlayerData()
    {
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayerData(player.getUniqueId());
        }
    }

    public static boolean playerDataLoaded(UUID playerUUID) { return playerData.containsKey(playerUUID); }
    private static void addPlayerData(UUID uuid, PlayerData playerData) {
        PlayerDataManager.playerData.put(uuid, playerData);
    }
}
