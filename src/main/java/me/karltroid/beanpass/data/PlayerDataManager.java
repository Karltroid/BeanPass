package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.mounts.Mount;
import me.karltroid.beanpass.npcs.NPC;
import me.karltroid.beanpass.quests.Quests.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;


public class PlayerDataManager implements Listener
{
    private static final String DATABASE_NAME = "database.db";
    private static final String PLAYER_SEASON_DATA_TABLE_NAME = "player_season_" + BeanPass.getInstance().getSeason().getId() + "_data";
    private static final String PLAYER_SKINS_TABLE_NAME = "player_skins";
    private static final String PLAYER_MOUNTS_TABLE_NAME = "player_mounts";
    private static final String PLAYER_QUESTS_TABLE_NAME = "player_quests";

    public PlayerDataManager()
    {
        createTables();
    }

    private Connection getConnection(Plugin plugin) throws SQLException {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        String databasePath = dataFolder.getAbsolutePath() + File.separator + DATABASE_NAME;
        String jdbcUrl = "jdbc:sqlite:" + databasePath;
        return DriverManager.getConnection(jdbcUrl);
    }

    public void createTables() {
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
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_SKINS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "skin_id VARCHAR(16) NOT NULL,"
                            + "equipped BOOLEAN NOT NULL DEFAULT FALSE,"
                            + "PRIMARY KEY (uuid, skin_id)"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_MOUNTS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "mount_id VARCHAR(16) NOT NULL,"
                            + "equipped BOOLEAN NOT NULL DEFAULT FALSE,"
                            + "PRIMARY KEY (uuid, mount_id)"
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
        } catch (SQLException e) {
            getLogger().severe("Failed to create database table: " + e.getMessage());
        }
    }

    public void loadPlayerData(UUID uuid)
    {
        try (Connection conn = getConnection(BeanPass.getInstance()))
        {
            boolean premium = false;
            double xp = 0.0;
            int lastKnownLevel = 1;
            int maxHomes = 0;

            try (PreparedStatement playerSeasonDataStatement = conn.prepareStatement("SELECT * FROM " + PLAYER_SEASON_DATA_TABLE_NAME + " WHERE uuid = ?"))
            {
                playerSeasonDataStatement.setString(1, uuid.toString());

                try (ResultSet playerSeasonDataResult = playerSeasonDataStatement.executeQuery()) {
                    if (playerSeasonDataResult.next()) {
                        premium = playerSeasonDataResult.getBoolean("premium");
                        xp = playerSeasonDataResult.getDouble("xp");
                        lastKnownLevel = playerSeasonDataResult.getInt("last_known_level");
                        maxHomes = playerSeasonDataResult.getInt("max_homes");
                    }
                }
            }

            PlayerData playerData = new PlayerData(uuid, premium, new ArrayList<>(), new ArrayList<>(), xp, lastKnownLevel, maxHomes);
            BeanPass.getInstance().addPlayerData(uuid, playerData);
            if (lastKnownLevel < playerData.getLevel()) playerData.leveledUp(); // level up player if they got xp while offline

            try (PreparedStatement playerSkinsStatement = conn.prepareStatement("SELECT * FROM player_skins WHERE uuid = ?"))
            {
                playerSkinsStatement.setString(1, uuid.toString());

                try (ResultSet playerSkinsResult = playerSkinsStatement.executeQuery())
                {
                    while (playerSkinsResult.next())
                    {
                        int skinID = Integer.parseInt(playerSkinsResult.getString("skin_id"));
                        Skin skin = BeanPass.getInstance().skinManager.getSkinById(skinID);
                        if (playerSkinsResult.getBoolean("equipped")) playerData.equipSkin(skin, false);
                        playerData.giveSkin(skin, false);
                    }
                }
            }

            try (PreparedStatement playerMountsStatement = conn.prepareStatement("SELECT * FROM player_mounts WHERE uuid = ?"))
            {
                playerMountsStatement.setString(1, uuid.toString());

                try (ResultSet playerMountsResult = playerMountsStatement.executeQuery())
                {
                    while (playerMountsResult.next())
                    {
                        int mountID = Integer.parseInt(playerMountsResult.getString("mount_id"));
                        Mount mount = BeanPass.getInstance().mountManager.getMountById(mountID);
                        if (playerMountsResult.getBoolean("equipped")) playerData.equipMount(mount, false);
                        playerData.giveMount(mount, false);
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
        }
        catch (SQLException e)
        {
            getLogger().severe("Failed to load data from database: " + e.getMessage());
        }
    }

    public void savePlayerData(UUID uuid)
    {
        if (!BeanPass.getInstance().playerDataExists(uuid)) return;
        PlayerData playerData = BeanPass.getInstance().getPlayerData(uuid);

        try (Connection conn = getConnection(BeanPass.getInstance());
             PreparedStatement playerSeasonDataStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_SEASON_DATA_TABLE_NAME + " (uuid, xp, premium, last_known_level, max_homes) VALUES (?, ?, ?, ?, ?) " +
                             "ON CONFLICT(uuid) DO UPDATE SET xp = excluded.xp, premium = excluded.premium, last_known_level = excluded.last_known_level, max_homes = excluded.max_homes"
             );
             PreparedStatement deletePlayerSkinsStatement = conn.prepareStatement(
                     "DELETE FROM player_skins WHERE uuid = ?"
             );
             PreparedStatement insertPlayerSkinStatement = conn.prepareStatement(
                     "INSERT INTO player_skins (uuid, skin_id, equipped) VALUES (?, ?, ?)"
             );
             PreparedStatement deletePlayerMountsStatement = conn.prepareStatement(
                     "DELETE FROM player_mounts WHERE uuid = ?"
             );
             PreparedStatement insertPlayerMountStatement = conn.prepareStatement(
                     "INSERT INTO player_mounts (uuid, mount_id, equipped) VALUES (?, ?, ?)"
             );
             PreparedStatement insertPlayerMaterialQuestsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_QUESTS_TABLE_NAME + " (uuid, quest_giver, goal_type, goal_count, player_count, xp_reward) VALUES (?, ?, ?, ?, ?, ?)" +
                             "ON CONFLICT(uuid, quest_giver, goal_type, goal_count) DO UPDATE SET player_count = excluded.player_count"
             );

        ) {
            // Insert or update player_season_data table
            playerSeasonDataStatement.setString(1, uuid.toString());
            playerSeasonDataStatement.setDouble(2, playerData.xp);
            playerSeasonDataStatement.setBoolean(3, playerData.premium);
            playerSeasonDataStatement.setInt(4, playerData.lastKnownLevel);
            playerSeasonDataStatement.setInt(5, playerData.maxHomes);
            playerSeasonDataStatement.executeUpdate();

            // Delete all player_skins and player_mounts for the player
            deletePlayerSkinsStatement.setString(1, uuid.toString());
            deletePlayerSkinsStatement.executeUpdate();
            deletePlayerMountsStatement.setString(1, uuid.toString());
            deletePlayerMountsStatement.executeUpdate();

            // Insert new player_skins for the player
            for (Integer skinID : playerData.ownedSkins)
            {
                insertPlayerSkinStatement.setString(1, uuid.toString());
                insertPlayerSkinStatement.setString(2, skinID.toString());
                insertPlayerSkinStatement.setBoolean(3, playerData.equippedSkins.contains(BeanPass.getInstance().skinManager.getSkinById(skinID)));
                insertPlayerSkinStatement.addBatch();
            }
            insertPlayerSkinStatement.executeBatch();

            // Insert new player_mounts for the player
            for (Integer mountID : playerData.ownedMounts)
            {
                insertPlayerMountStatement.setString(1, uuid.toString());
                insertPlayerMountStatement.setString(2, mountID.toString());
                insertPlayerMountStatement.setBoolean(3, playerData.equippedMounts.contains(BeanPass.getInstance().mountManager.getMountById(mountID)));
                insertPlayerMountStatement.addBatch();
            }
            insertPlayerMountStatement.executeBatch();

            for (Quest quest : playerData.getQuests())
            {
                NPC questGiver = quest.getQuestGiver();
                insertPlayerMaterialQuestsStatement.setString(1, quest.playerUUID);
                insertPlayerMaterialQuestsStatement.setString(2, BeanPass.getInstance().getNpcManager().getNPCTypeNameFromObject(questGiver));
                insertPlayerMaterialQuestsStatement.setString(3, questGiver.getQuestGoalType(quest));
                insertPlayerMaterialQuestsStatement.setInt(4, quest.goalCount);
                insertPlayerMaterialQuestsStatement.setInt(5, quest.playerCount);
                insertPlayerMaterialQuestsStatement.setDouble(6, quest.xpReward);
                insertPlayerMaterialQuestsStatement.addBatch();
            }
            insertPlayerMaterialQuestsStatement.executeBatch();
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
        loadPlayerData(player.getUniqueId());
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        savePlayerData(player.getUniqueId());
    }
}
