package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;


public class DataManager implements Listener
{
    private static final String DATABASE_NAME = "database.db";
    private static final String PLAYER_SEASON_DATA_TABLE_NAME = "player_season_" + BeanPass.main.getActiveSeason().getId() + "_data";
    private static final String PLAYER_HATS_TABLE_NAME = "player_hats";

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
        } catch (SQLException e) {
            getLogger().severe("Failed to create database table: " + e.getMessage());
        }
    }

    public void loadPlayerData(UUID uuid) {
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

            SeasonPlayer seasonPlayer = new SeasonPlayer(xp, premium);

            try (PreparedStatement playerHatsStatement = conn.prepareStatement("SELECT hat_id FROM player_hats WHERE uuid = ?")) {
                playerHatsStatement.setString(1, uuid.toString());

                try (ResultSet playerHatsResult = playerHatsStatement.executeQuery()) {
                    while (playerHatsResult.next()) {
                        int hatID = Integer.parseInt(playerHatsResult.getString("hat_id"));
                        seasonPlayer.giveHat(hatID);
                    }
                }
            }

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
        } catch (SQLException e) {
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
