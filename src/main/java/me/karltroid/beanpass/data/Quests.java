package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.enums.ServerGamemode;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;

import java.util.*;

public class Quests implements Listener
{
    @EventHandler
    void onMiningQuestProgressed(BlockBreakEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(playerUUID);

        MiningQuest miningQuest = null;
        for (Quest quest : seasonPlayer.getQuests(BeanPass.main.getServerGamemode()))
        {
            if (!(quest instanceof MiningQuest)) continue;
            miningQuest = (MiningQuest) quest;
        }

        if (miningQuest == null) return;

        Material blockMinedType = event.getBlock().getType();
        if (blockMinedType != miningQuest.getGoalBlockType()) return;

        miningQuest.playerCount++;
        if (miningQuest.isCompleted())
        {
            seasonPlayer.addXp(miningQuest.XP_REWARD);
            seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).remove(miningQuest);

            event.getPlayer().sendMessage(ChatColor.GREEN + "COMPLETED: " + miningQuest.getGoalDescription());
            event.getPlayer().sendMessage(ChatColor.YELLOW + "+" + miningQuest.XP_REWARD + "XP");
            seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).add(new MiningQuest(ServerGamemode.SURVIVAL, playerUUID.toString(), 50, Material.STONE, 16, 0));
        }
    }

    public static abstract class Quest
    {
        final ServerGamemode SERVER_GAMEMODE;
        final String PLAYER_UUID;
        final double XP_REWARD;
        final int GOAL_COUNT;
        int playerCount;

        Quest(ServerGamemode serverGamemode, String playerUUID, double xpReward, int goalCount, int playerCount)
        {
            this.SERVER_GAMEMODE = serverGamemode;
            this.PLAYER_UUID = playerUUID;
            this.XP_REWARD = xpReward;
            this.GOAL_COUNT = goalCount;
            this.playerCount = playerCount;
        }

        public String getRewardDescription()
        {
            return "+" + XP_REWARD + "XP";
        }
        public ServerGamemode getServerGamemode() { return SERVER_GAMEMODE; }
        public boolean isCompleted() { return playerCount >= GOAL_COUNT; }
    }

    public static class MiningQuest extends Quest
    {
        final Material GOAL_BLOCK_TYPE;

        public MiningQuest(ServerGamemode gamemode, String playerUUID, double xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount)
        {
            super(gamemode, playerUUID, xpReward, goalBlockCount, playerBlockCount);
            this.GOAL_BLOCK_TYPE = goalBlockType;
        }

        public String getGoalDescription()
        {
            return "Mine " + playerCount + "/" + GOAL_COUNT + "x natural " + GOAL_BLOCK_TYPE.name().replace('_', ' ').toLowerCase() + (GOAL_COUNT > 1 ? "s" : "");
        }

        public Material getGoalBlockType() { return GOAL_BLOCK_TYPE; }
    }

    public static class KillingQuest extends Quest
    {
        final EntityType GOAL_ENTITY_TYPE;

        public KillingQuest(ServerGamemode gamemode, String playerUUID, double xpReward, EntityType goalEntityType, int goalKillCount, int playerKillCount)
        {
            super(gamemode, playerUUID, xpReward, goalKillCount, playerKillCount);
            this.GOAL_ENTITY_TYPE = goalEntityType;
        }

        public String getGoalDescription()
        {
            return "Kill " + playerCount + "/" + GOAL_COUNT + "x " + GOAL_ENTITY_TYPE.name().replace('_', ' ').toLowerCase() + (GOAL_COUNT > 1 ? "s" : "");
        }

        public EntityType getGoalEntityType() { return GOAL_ENTITY_TYPE; }
    }

    public static class ExplorationQuest extends Quest
    {
        final Structure GOAL_STRUCTURE_TYPE;

        public ExplorationQuest(ServerGamemode gamemode, String playerUUID, double xpReward, Structure goalStructureType, int goalChestCount, int playerChestCount)
        {
            super(gamemode, playerUUID, xpReward, goalChestCount, playerChestCount);
            this.GOAL_STRUCTURE_TYPE = goalStructureType;
        }

        public String getGoalDescription()
        {
            return "Loot " + playerCount + "/" + GOAL_COUNT + "x undiscovered chest" + (GOAL_COUNT > 1 ? "s" : "") + " from a " + GOAL_STRUCTURE_TYPE.toString().replace('_', ' ').toLowerCase();
        }

        public Structure getGoalStructureType() { return GOAL_STRUCTURE_TYPE; }
    }

    public static class BreedingQuest extends Quest
    {
        final EntityType GOAL_ENTITY_TYPE;

        public BreedingQuest(ServerGamemode gamemode, String playerUUID, double xpReward, EntityType goalEntityType, int goalBabyCount, int playerBabyCount)
        {
            super(gamemode, playerUUID, xpReward, goalBabyCount, playerBabyCount);
            this.GOAL_ENTITY_TYPE = goalEntityType;
        }

        public String getGoalDescription()
        {
            return "Breed " + playerCount + "/" + GOAL_COUNT + "x baby " + GOAL_ENTITY_TYPE.name().replace('_', ' ').toLowerCase();
        }

        public EntityType getGoalEntityType() { return GOAL_ENTITY_TYPE; }
    }
}
