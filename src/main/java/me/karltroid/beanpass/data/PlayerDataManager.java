package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.mounts.Mount;
import me.karltroid.beanpass.npcs.NPC;
import me.karltroid.beanpass.quests.Quests.*;
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
    private static final String PLAYER_REWARDS_TABLE_NAME = "player_rewards";
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

                                Skin skin = BeanPass.getInstance().skinManager.getSkinById(rewardID);
                                playerData.giveSkin(skin, false);
                                if (equipped) playerData.equipSkin(skin, false);
                                break;
                            case "MOUNT":
                                Mount mount = BeanPass.getInstance().mountManager.getMountById(rewardID);
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
                insertPlayerRewardsStatement.setBoolean(4, playerData.equippedSkins.contains(BeanPass.getInstance().skinManager.getSkinById(skinID)));
                insertPlayerRewardsStatement.addBatch();
            }
            insertPlayerRewardsStatement.executeBatch();

            // Insert new player_mounts for the player
            for (Integer mountID : playerData.ownedMounts)
            {
                insertPlayerRewardsStatement.setString(1, uuid.toString());
                insertPlayerRewardsStatement.setString(2, "MOUNT");
                insertPlayerRewardsStatement.setString(3, mountID.toString());
                insertPlayerRewardsStatement.setBoolean(4, playerData.equippedMounts.contains(BeanPass.getInstance().mountManager.getMountById(mountID)));
                insertPlayerRewardsStatement.addBatch();
            }
            insertPlayerRewardsStatement.executeBatch();

            deletePlayerQuestsStatement.setString(1, uuid.toString());
            deletePlayerQuestsStatement.executeUpdate();
            for (Quest quest : playerData.getQuests())
            {
                NPC questGiver = quest.getQuestGiver();
                insertPlayerQuestsStatement.setString(1, quest.playerUUID);
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
        loadPlayerData(player.getUniqueId());
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        savePlayerData(player.getUniqueId());
    }
}
