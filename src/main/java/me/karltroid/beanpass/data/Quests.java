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
    public HashMap<ServerGamemode, List<Quest>> database = new HashMap<>();

    public void addQuest(Quest quest)
    {
        ServerGamemode gamemode = quest.getServerGamemode();
        List<Quest> questList = database.get(gamemode);
        questList.add(quest);
    }

    @EventHandler
    void onMiningQuestProgressed(BlockBreakEvent event)
    {
        UUID playerUUID = event.getPlayer().getUniqueId();
        SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(playerUUID);

        MiningQuest miningQuest = null;
        for (Quest quest : seasonPlayer.quests)
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
            seasonPlayer.quests.remove(miningQuest);

            event.getPlayer().sendMessage(ChatColor.GREEN + "COMPLETED: " + miningQuest.getGoalDescription());
            event.getPlayer().sendMessage(ChatColor.YELLOW + "+" + miningQuest.XP_REWARD + "XP");
            seasonPlayer.quests.add(new MiningQuest(ServerGamemode.SURVIVAL, 50, Material.STONE, 16, 0));
        }
    }

    static abstract class Quest
    {
        final ServerGamemode SERVER_GAMEMODE;
        final double XP_REWARD;
        final int GOAL_COUNT;
        int playerCount;

        Quest(ServerGamemode serverGamemode, double xpReward, int goalCount, int playerCount)
        {
            this.SERVER_GAMEMODE = serverGamemode;
            this.XP_REWARD = xpReward;
            this.GOAL_COUNT = goalCount;
            this.playerCount = playerCount;
        }

        boolean playerHasQuest(SeasonPlayer seasonPlayer)
        {
            return seasonPlayer.quests.contains(this);
        }

        public String getRewardDescription()
        {
            return "+" + XP_REWARD + "XP";
        }
        public ServerGamemode getServerGamemode() { return SERVER_GAMEMODE; }

        public boolean isCompleted() { return playerCount >= GOAL_COUNT; }
    }

    public class MiningQuest extends Quest
    {
        final Material GOAL_BLOCK_TYPE;

        public MiningQuest(ServerGamemode gamemode, int xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount)
        {
            super(gamemode, xpReward, goalBlockCount, playerBlockCount);
            this.GOAL_BLOCK_TYPE = goalBlockType;
        }

        public String getGoalDescription()
        {
            return "Mine " + GOAL_COUNT + "x natural " + GOAL_BLOCK_TYPE.name().toLowerCase() + (GOAL_COUNT > 1 ? "s" : "");
        }

        public Material getGoalBlockType() { return GOAL_BLOCK_TYPE; }
    }

    public class KillingQuest extends Quest
    {
        final EntityType GOAL_ENTITY_TYPE;

        public KillingQuest(ServerGamemode gamemode, int xpReward, EntityType goalEntityType, int goalKillCount, int playerKillCount)
        {
            super(gamemode, xpReward, goalKillCount, playerKillCount);
            this.GOAL_ENTITY_TYPE = goalEntityType;
        }

        public String getGoalDescription()
        {
            return "Kill " + GOAL_COUNT + "x " + GOAL_ENTITY_TYPE.name().toLowerCase() + (GOAL_COUNT > 1 ? "s" : "");
        }

        public EntityType getGoalEntityType() { return GOAL_ENTITY_TYPE; }
    }

    public class ExplorationQuest extends Quest
    {
        final Structure GOAL_STRUCTURE_TYPE;

        public ExplorationQuest(ServerGamemode gamemode, int xpReward, Structure goalStructureType, int goalChestCount, int playerChestCount)
        {
            super(gamemode, xpReward, goalChestCount, playerChestCount);
            this.GOAL_STRUCTURE_TYPE = goalStructureType;
        }

        public String getGoalDescription()
        {
            return "Loot " + GOAL_COUNT + "x undiscovered chest" + (GOAL_COUNT > 1 ? "s" : "") + " from a " + GOAL_STRUCTURE_TYPE.toString().toLowerCase();
        }

        public Structure getGoalStructureType() { return GOAL_STRUCTURE_TYPE; }
    }
}
