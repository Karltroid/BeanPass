package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;

import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

import java.util.*;

public class Quests
{
    public static Quest getRandomQuestType(String uuid)
    {
        Quest[] quests = {
                new MiningQuest(uuid, -1, null, -1, 0),
                new KillingQuest(uuid, -1, null, -1, 0)
        };

        Random random = new Random();
        return quests[random.nextInt(quests.length)];
    }

    public static abstract class Quest
    {
        String playerUUID;
        double xpReward;
        int goalCount;
        int playerCount;

        Quest(String playerUUID, double xpReward, int goalCount, int playerCount)
        {
            this.playerUUID = playerUUID;
            this.goalCount = goalCount;
            this.xpReward = xpReward;
            this.playerCount = playerCount;
        }

        public void setPlayerUUID(String playerUUID)
        {
            this.playerUUID = playerUUID;
        }

        public String getRewardDescription()
        {
            return "+" + xpReward + "XP";
        }
        public boolean isCompleted() { return playerCount >= goalCount; }
        public void incrementPlayerCount() { playerCount++; }
        public double getXPReward() { return xpReward; }
    }

    public static class MiningQuest extends Quest
    {
        Material goalBlockType;

        public MiningQuest(String playerUUID, double xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount)
        {
            super(playerUUID, xpReward, goalBlockCount, playerBlockCount);
            String questDifficultyKey = BeanPass.main.questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.main.questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalBlockCount <= 0 ? questDifficulty.generateUnitAmount() : goalBlockCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);

            
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
        EntityType goalEntityType;

        public KillingQuest(String playerUUID, double xpReward, EntityType goalEntityType, int goalKillCount, int playerKillCount)
        {
            super(playerUUID, xpReward, goalKillCount, playerKillCount);
            String questDifficultyKey = null;
            for (int i = 0; i < 100; i++)
            {
                questDifficultyKey = BeanPass.main.questDifficulties.getRandom();
                if (BeanPass.main.questManager.getKillingQuestDifficulties().containsValue(questDifficultyKey)) break;
            }
            QuestDifficulty questDifficulty = BeanPass.main.questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalKillCount <= 0 ? questDifficulty.generateUnitAmount() : goalKillCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);


            if (goalEntityType != null)
            {
                this.goalEntityType = goalEntityType;
            }
            else
            {
                List<Map.Entry<EntityType, String>> matchingDifficultyEntity = new ArrayList<>();

                for (Map.Entry<EntityType, String> entry : BeanPass.main.questManager.getKillingQuestDifficulties().entrySet()) {
                    if (entry.getValue().equals(questDifficultyKey)) {
                        matchingDifficultyEntity.add(entry);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(matchingDifficultyEntity.size());

                Map.Entry<EntityType, String> randomEntry = matchingDifficultyEntity.get(randomIndex);

                this.goalEntityType = randomEntry.getKey();
            }
        }

        public String getGoalDescription()
        {
            String formattedGoalEntityTypeName = goalEntityType.name();
            formattedGoalEntityTypeName = formattedGoalEntityTypeName.replace('_', ' ');
            formattedGoalEntityTypeName = formattedGoalEntityTypeName.toLowerCase();
            formattedGoalEntityTypeName += (goalCount > 1 && !formattedGoalEntityTypeName.endsWith("s") ? "s" : "");

            return "Kill " + playerCount + "/" + goalCount + "x " + formattedGoalEntityTypeName;
        }

        public EntityType getGoalEntityType() { return goalEntityType; }
    }

    public static class ExplorationQuest extends Quest
    {
        final Structure GOAL_STRUCTURE_TYPE;

        public ExplorationQuest(String playerUUID, double xpReward, Structure goalStructureType, int goalChestCount, int playerChestCount)
        {
            super(playerUUID, xpReward, goalChestCount, playerChestCount);
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

        public BreedingQuest(String playerUUID, double xpReward, EntityType goalEntityType, int goalBabyCount, int playerBabyCount)
        {
            super(playerUUID, xpReward, goalBabyCount, playerBabyCount);
            this.GOAL_ENTITY_TYPE = goalEntityType;
        }

        public String getGoalDescription()
        {
            return "Breed " + playerCount + "/" + goalCount + "x baby " + GOAL_ENTITY_TYPE.name().replace('_', ' ').toLowerCase();
        }

        public EntityType getGoalEntityType() { return GOAL_ENTITY_TYPE; }
    }

    // fishing quest

    // farming quest
}
