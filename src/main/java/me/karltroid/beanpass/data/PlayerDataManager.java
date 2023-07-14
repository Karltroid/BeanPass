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
    private static final String PLAYER_MINING_QUESTS_TABLE_NAME = "player_mining_quests";
    private static final String PLAYER_KILLING_QUESTS_TABLE_NAME = "player_killing_quests";
    private static final String PLAYER_EXPLORATION_QUESTS_TABLE_NAME = "player_exploration_quests";

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
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_MINING_QUESTS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "xp_reward DOUBLE NOT NULL,"
                            + "goal_count VARCHAR(36) NOT NULL,"
                            + "player_count VARCHAR(36) NOT NULL,"
                            + "goal_block_type VARCHAR(36) NOT NULL,"
                            + "quest_giver VARCHAR(36) NOT NULL,"
                            + "PRIMARY KEY (uuid, goal_count, goal_block_type, quest_giver)"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_KILLING_QUESTS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "xp_reward DOUBLE NOT NULL,"
                            + "goal_count VARCHAR(36) NOT NULL,"
                            + "player_count VARCHAR(36) NOT NULL,"
                            + "goal_entity_type VARCHAR(36) NOT NULL,"
                            + "quest_giver VARCHAR(36) NOT NULL,"
                            + "PRIMARY KEY (uuid, goal_count, goal_entity_type, quest_giver)"
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

            try (PreparedStatement playerMiningQuestStatement = conn.prepareStatement("SELECT * FROM " + PLAYER_MINING_QUESTS_TABLE_NAME + " WHERE uuid = ?")) {
                playerMiningQuestStatement.setString(1, uuid.toString());

                try (ResultSet playerMiningQuestResult = playerMiningQuestStatement.executeQuery())
                {
                    while (playerMiningQuestResult.next())
                    {
                        double xpReward = playerMiningQuestResult.getDouble("xp_reward");
                        Material goalBlockType = Material.valueOf(playerMiningQuestResult.getString("goal_block_type"));
                        int goalBlockCount = Integer.parseInt(playerMiningQuestResult.getString("goal_count"));
                        int playerBlockCount = Integer.parseInt(playerMiningQuestResult.getString("player_count"));
                        NPC questGiver = BeanPass.getInstance().getNpcManager().getNPCByTypeName(playerMiningQuestResult.getString("quest_giver"));

                        playerData.giveQuest(new MiningQuest(uuid.toString(), xpReward, goalBlockType, goalBlockCount, playerBlockCount, questGiver), false);
                    }
                }
            }
            catch (SQLException e)
            {
                getLogger().severe(e.getMessage());
            }

            try (PreparedStatement playerKillingQuestStatement = conn.prepareStatement("SELECT * FROM " + PLAYER_KILLING_QUESTS_TABLE_NAME + " WHERE uuid = ?")) {
                playerKillingQuestStatement.setString(1, uuid.toString());

                try (ResultSet playerKillingQuestResult = playerKillingQuestStatement.executeQuery())
                {
                    while (playerKillingQuestResult.next())
                    {
                        double xpReward = playerKillingQuestResult.getDouble("xp_reward");
                        String goalEntityTypeName = playerKillingQuestResult.getString("goal_entity_type");
                        EntityType goalEntityType = null;
                        for (EntityType type : EntityType.values())
                        {
                            if (!type.name().equalsIgnoreCase(goalEntityTypeName)) continue;

                            goalEntityType = type;
                            break;
                        }
                        if (goalEntityType == null)
                        {
                            BeanPass.getInstance().getLogger().warning("Entity type " + goalEntityTypeName + " does not exist. Could not load quest.");
                            continue;
                        }
                        int goalKillCount = Integer.parseInt(playerKillingQuestResult.getString("goal_count"));
                        int playerKillCount = Integer.parseInt(playerKillingQuestResult.getString("player_count"));
                        NPC questGiver = BeanPass.getInstance().getNpcManager().getNPCByTypeName(playerKillingQuestResult.getString("quest_giver"));

                        playerData.giveQuest(new KillingQuest(uuid.toString(), xpReward, goalEntityType, goalKillCount, playerKillCount, questGiver), false);
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
             PreparedStatement insertPlayerMiningQuestsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_MINING_QUESTS_TABLE_NAME + " (uuid, xp_reward, goal_count, player_count, goal_block_type, quest_giver) VALUES (?, ?, ?, ?, ?, ?)" +
                             "ON CONFLICT(uuid, goal_count, goal_block_type, quest_giver) DO UPDATE SET xp_reward = excluded.xp_reward, goal_count = excluded.goal_count, player_count = excluded.player_count, goal_block_type = excluded.goal_block_type, quest_giver = excluded.quest_giver"
             );
             PreparedStatement insertPlayerKillingQuestsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_KILLING_QUESTS_TABLE_NAME + " (uuid, xp_reward, goal_count, player_count, goal_entity_type, quest_giver) VALUES (?, ?, ?, ?, ?, ?)" +
                             "ON CONFLICT(uuid, goal_count, goal_entity_type, quest_giver) DO UPDATE SET xp_reward = excluded.xp_reward, goal_count = excluded.goal_count, player_count = excluded.player_count, goal_entity_type = excluded.goal_entity_type, quest_giver = excluded.quest_giver"
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
                if (quest instanceof MiningQuest)
                {
                    MiningQuest miningQuest = (MiningQuest) quest;
                    insertPlayerMiningQuestsStatement.setString(1, miningQuest.playerUUID);
                    insertPlayerMiningQuestsStatement.setDouble(2, miningQuest.xpReward);
                    insertPlayerMiningQuestsStatement.setInt(3, miningQuest.goalCount);
                    insertPlayerMiningQuestsStatement.setInt(4, miningQuest.playerCount);
                    insertPlayerMiningQuestsStatement.setString(5, miningQuest.getGoalBlockType().name());
                    insertPlayerMiningQuestsStatement.setString(6, BeanPass.getInstance().getNpcManager().getNPCTypeNameFromObject(miningQuest.getQuestGiver()));
                    insertPlayerMiningQuestsStatement.addBatch();
                }
                else if (quest instanceof KillingQuest)
                {
                    KillingQuest killingQuest = (KillingQuest) quest;
                    insertPlayerKillingQuestsStatement.setString(1, killingQuest.playerUUID);
                    insertPlayerKillingQuestsStatement.setDouble(2, killingQuest.xpReward);
                    insertPlayerKillingQuestsStatement.setInt(3, killingQuest.goalCount);
                    insertPlayerKillingQuestsStatement.setInt(4, killingQuest.playerCount);
                    insertPlayerKillingQuestsStatement.setString(5, killingQuest.getGoalEntityType().name());
                    insertPlayerKillingQuestsStatement.setString(6, BeanPass.getInstance().getNpcManager().getNPCTypeNameFromObject(killingQuest.getQuestGiver()));
                    insertPlayerKillingQuestsStatement.addBatch();
                }
            }
            insertPlayerMiningQuestsStatement.executeBatch();
            insertPlayerKillingQuestsStatement.executeBatch();
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
