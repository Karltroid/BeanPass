package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Quests.KillingQuest;
import me.karltroid.beanpass.data.Quests.MiningQuest;
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
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;


public class DataManager implements Listener
{
    private static final String DATABASE_NAME = "database.db";
    private static final String PLAYER_SEASON_DATA_TABLE_NAME = "player_season_" + BeanPass.main.getActiveSeason().getId() + "_data";
    private static final String PLAYER_HATS_TABLE_NAME = "player_hats";
    private static final String PLAYER_MINING_QUESTS_TABLE_NAME = "player_mining_quests";
    private static final String PLAYER_KILLING_QUESTS_TABLE_NAME = "player_killing_quests";
    private static final String PLAYER_EXPLORATION_QUESTS_TABLE_NAME = "player_exploration_quests";

    public DataManager()
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
        try (Connection conn = getConnection(BeanPass.main);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_SEASON_DATA_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL PRIMARY KEY,"
                            + "premium BOOLEAN NOT NULL,"
                            + "xp DOUBLE NOT NULL"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_HATS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "hat_id VARCHAR(16) NOT NULL,"
                            + "PRIMARY KEY (uuid, hat_id)"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_MINING_QUESTS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "xp_reward DOUBLE NOT NULL,"
                            + "goal_count VARCHAR(36) NOT NULL,"
                            + "player_count VARCHAR(36) NOT NULL,"
                            + "goal_block_type VARCHAR(36) NOT NULL,"
                            + "PRIMARY KEY (uuid, goal_count, goal_block_type)"
                            + ")"
            );
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + PLAYER_KILLING_QUESTS_TABLE_NAME + " ("
                            + "uuid VARCHAR(36) NOT NULL,"
                            + "xp_reward DOUBLE NOT NULL,"
                            + "goal_count VARCHAR(36) NOT NULL,"
                            + "player_count VARCHAR(36) NOT NULL,"
                            + "goal_entity_type VARCHAR(36) NOT NULL,"
                            + "PRIMARY KEY (uuid, goal_count, goal_entity_type)"
                            + ")"
            );
        } catch (SQLException e) {
            getLogger().severe("Failed to create database table: " + e.getMessage());
        }
    }

    public void loadPlayerData(UUID uuid)
    {
        try (Connection conn = getConnection(BeanPass.main))
        {
            boolean premium = false;
            double xp = 0.0;

            try (PreparedStatement playerSeasonDataStatement = conn.prepareStatement("SELECT * FROM " + PLAYER_SEASON_DATA_TABLE_NAME + " WHERE uuid = ?"))
            {
                playerSeasonDataStatement.setString(1, uuid.toString());

                try (ResultSet playerSeasonDataResult = playerSeasonDataStatement.executeQuery()) {
                    if (playerSeasonDataResult.next()) {
                        premium = playerSeasonDataResult.getBoolean("premium");
                        xp = playerSeasonDataResult.getDouble("xp");
                    }
                }
            }

            SeasonPlayer seasonPlayer = new SeasonPlayer(uuid.toString(), xp, premium);

            try (PreparedStatement playerHatsStatement = conn.prepareStatement("SELECT hat_id FROM player_hats WHERE uuid = ?"))
            {
                playerHatsStatement.setString(1, uuid.toString());

                try (ResultSet playerHatsResult = playerHatsStatement.executeQuery()) {
                    while (playerHatsResult.next()) {
                        int hatID = Integer.parseInt(playerHatsResult.getString("hat_id"));
                        seasonPlayer.giveHat(hatID);
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

                        seasonPlayer.giveQuest(new MiningQuest(uuid.toString(), xpReward, goalBlockType, goalBlockCount, playerBlockCount));
                    }

                    //if (!hasQuest) { seasonPlayer.giveQuest(new MiningQuest(ServerGamemode.SURVIVAL, uuid.toString(), -1, null, -1, 0)); }
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
                            if (type.name().equalsIgnoreCase(goalEntityTypeName)) goalEntityType = type;
                            break;
                        }
                        if (goalEntityType == null)
                        {
                            BeanPass.main.getLogger().warning("Entity type " + goalEntityTypeName + " does not exist. Skipping.");
                            continue;
                        }
                        int goalKillCount = Integer.parseInt(playerKillingQuestResult.getString("goal_count"));
                        int playerKillCount = Integer.parseInt(playerKillingQuestResult.getString("player_count"));

                        seasonPlayer.giveQuest(new KillingQuest(uuid.toString(), xpReward, goalEntityType, goalKillCount, playerKillCount));
                    }
                }

                while (seasonPlayer.getQuests().size() < BeanPass.main.questManager.getQuestsPerPlayer())
                {
                    seasonPlayer.giveQuest(null);
                }
            }
            catch (SQLException e)
            {
                getLogger().severe(e.getMessage());
            }

            while (seasonPlayer.getQuests().size() < BeanPass.main.questManager.getQuestsPerPlayer())
                seasonPlayer.giveQuest(null);

            BeanPass.main.getActiveSeason().playerData.put(uuid, seasonPlayer);
        } catch (SQLException e) {
            getLogger().severe("Failed to load data from database: " + e.getMessage());
        }
    }

    public void savePlayerData(UUID uuid)
    {
        if (!BeanPass.main.getActiveSeason().playerData.containsKey(uuid)) return;
        SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(uuid);

        try (Connection conn = getConnection(BeanPass.main);
             PreparedStatement playerSeasonDataStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_SEASON_DATA_TABLE_NAME + " (uuid, xp, premium) VALUES (?, ?, ?) " +
                             "ON CONFLICT(uuid) DO UPDATE SET xp = excluded.xp, premium = excluded.premium"
             );
             PreparedStatement deletePlayerHatsStatement = conn.prepareStatement(
                     "DELETE FROM player_hats WHERE uuid = ?"
             );
             PreparedStatement insertPlayerHatStatement = conn.prepareStatement(
                     "INSERT INTO player_hats (uuid, hat_id) VALUES (?, ?)"
             );
             PreparedStatement insertPlayerMiningQuestsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_MINING_QUESTS_TABLE_NAME + " (uuid, xp_reward, goal_count, player_count, goal_block_type) VALUES (?, ?, ?, ?, ?)" +
                             "ON CONFLICT(uuid, goal_count, goal_block_type) DO UPDATE SET xp_reward = excluded.xp_reward, goal_count = excluded.goal_count, player_count = excluded.player_count, goal_block_type = excluded.goal_block_type"
             );
             PreparedStatement insertPlayerKillingQuestsStatement = conn.prepareStatement(
                     "INSERT INTO " + PLAYER_KILLING_QUESTS_TABLE_NAME + " (uuid, xp_reward, goal_count, player_count, goal_entity_type) VALUES (?, ?, ?, ?, ?)" +
                             "ON CONFLICT(uuid, goal_count, goal_entity_type) DO UPDATE SET xp_reward = excluded.xp_reward, goal_count = excluded.goal_count, player_count = excluded.player_count, goal_entity_type = excluded.goal_entity_type"
             )
        ) {
            // Insert or update player_season_data table
            playerSeasonDataStatement.setString(1, uuid.toString());
            playerSeasonDataStatement.setDouble(2, seasonPlayer.xp);
            playerSeasonDataStatement.setBoolean(3, seasonPlayer.premium);
            playerSeasonDataStatement.executeUpdate();

            // Delete all player_hats for the player
            deletePlayerHatsStatement.setString(1, uuid.toString());
            deletePlayerHatsStatement.executeUpdate();

            // Insert new player_hats for the player
            for (Integer hatId : seasonPlayer.hats) {
                insertPlayerHatStatement.setString(1, uuid.toString());
                insertPlayerHatStatement.setString(2, hatId.toString());
                insertPlayerHatStatement.addBatch();
            }
            insertPlayerHatStatement.executeBatch();

            for (Quests.Quest quest : seasonPlayer.getQuests())
            {
                if (quest instanceof MiningQuest)
                {
                    MiningQuest miningQuest = (MiningQuest) quest;
                    insertPlayerMiningQuestsStatement.setString(1, miningQuest.playerUUID);
                    insertPlayerMiningQuestsStatement.setDouble(2, miningQuest.xpReward);
                    insertPlayerMiningQuestsStatement.setInt(3, miningQuest.goalCount);
                    insertPlayerMiningQuestsStatement.setInt(4, miningQuest.playerCount);
                    insertPlayerMiningQuestsStatement.setString(5, miningQuest.getGoalBlockType().name());
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
