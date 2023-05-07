package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.enums.ServerGamemode;

import me.karltroid.beanpass.quests.QuestDifficulties;
import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;

import java.util.*;

public class Quests
{
    private final static Class<?>[] classTypes = { MiningQuest.class };

    public static Class<?> getRandomQuestType()
    {
        Random random = new Random();
        return classTypes[random.nextInt(classTypes.length)];
    }

    public static abstract class Quest
    {
        final ServerGamemode SERVER_GAMEMODE;
        final String PLAYER_UUID;
        double xpReward;
        int goalCount;
        int playerCount;

        Quest(ServerGamemode serverGamemode, String playerUUID, double xpReward, int goalCount, int playerCount)
        {
            this.SERVER_GAMEMODE = serverGamemode;
            this.PLAYER_UUID = playerUUID;
            this.goalCount = goalCount;
            this.xpReward = xpReward;
            this.playerCount = playerCount;
        }

        public String getRewardDescription()
        {
            return "+" + xpReward + "XP";
        }
        public ServerGamemode getServerGamemode() { return SERVER_GAMEMODE; }
        public boolean isCompleted() { return playerCount >= goalCount; }
        public void incrementPlayerCount() { playerCount++; }
        public double getXPReward() { return xpReward; }
    }

    public static class MiningQuest extends Quest
    {
        Material goalBlockType = null;

        public MiningQuest(ServerGamemode gamemode, String playerUUID, double xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount)
        {
            super(gamemode, playerUUID, xpReward, goalBlockCount, playerBlockCount);
            String questDifficultyKey = BeanPass.main.questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.main.questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalBlockCount == -1 ? questDifficulty.generateUnitAmount() : goalBlockCount);
            this.xpReward = (xpReward == -1 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);

            
            if (goalBlockType != null)
            {
                this.goalBlockType = goalBlockType;
            }
            else
            {
                HashMap<Material, String> miningQuestDifficulties = BeanPass.main.questManager.getMiningQuestDifficulties();
                List<Map.Entry<Material, String>> matchingDifficultyMaterials = new ArrayList<>();

                for (Map.Entry<Material, String> entry : miningQuestDifficulties.entrySet()) {
                    if (entry.getValue().equals(questDifficultyKey)) {
                        matchingDifficultyMaterials.add(entry);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(matchingDifficultyMaterials.size());

                Map.Entry<Material, String> randomEntry = matchingDifficultyMaterials.get(randomIndex);

                this.goalBlockType = randomEntry.getKey();
            }
        }

        public String getGoalDescription()
        {
            String formattedGoalBlockTypeName = goalBlockType.name();
            formattedGoalBlockTypeName = formattedGoalBlockTypeName.replace('_', ' ');
            formattedGoalBlockTypeName = formattedGoalBlockTypeName.toLowerCase();
            formattedGoalBlockTypeName += (goalCount > 1 && !formattedGoalBlockTypeName.endsWith("s") ? "s" : "");
            return "Mine " + playerCount + "/" + goalCount + "x natural " + formattedGoalBlockTypeName;
        }

        public Material getGoalBlockType() { return goalBlockType; }
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
            return "Kill " + playerCount + "/" + goalCount + "x " + GOAL_ENTITY_TYPE.name().replace('_', ' ').toLowerCase() + (goalCount > 1 ? "s" : "");
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
            return "Loot " + playerCount + "/" + goalCount + "x undiscovered chest" + (goalCount > 1 ? "s" : "") + " from a " + GOAL_STRUCTURE_TYPE.toString().replace('_', ' ').toLowerCase();
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
            return "Breed " + playerCount + "/" + goalCount + "x baby " + GOAL_ENTITY_TYPE.name().replace('_', ' ').toLowerCase();
        }

        public EntityType getGoalEntityType() { return GOAL_ENTITY_TYPE; }
    }
}
