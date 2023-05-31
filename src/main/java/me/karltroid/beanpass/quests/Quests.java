package me.karltroid.beanpass.quests;

import me.karltroid.beanpass.BeanPass;

import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

public class Quests
{
    public static Quest getRandomQuestType(String uuid)
    {
        Quest[] quests = {
                new MiningQuest(uuid, -1, null, -1, 0),
                new LumberQuest(uuid, -1, null, -1, 0),
                new KillingQuest(uuid, -1, null, -1, 0)
        };

        Random random = new Random();
        return quests[random.nextInt(quests.length)];
    }

    public static abstract class Quest
    {
        public String playerUUID;
        public double xpReward;
        public int goalCount;
        public int playerCount;
        String questVerb = "";
        String goalName = "";

        Quest(String playerUUID, double xpReward, int goalCount, int playerCount, String questVerb)
        {
            this.playerUUID = playerUUID;
            this.goalCount = goalCount;
            this.xpReward = xpReward;
            this.playerCount = playerCount;
            this.questVerb = questVerb;
        }

        public void setGoalName(String goalName)
        {
            this.goalName = goalName;
        }
        public void setQuestVerb(String questVerb)
        {
            this.questVerb = questVerb;
        }

        public String getRewardDescription()
        {
            return "+" + xpReward + "XP";
        }
        public String getGoalDescription()
        {
            String formattedGoalName = goalName;
            formattedGoalName = formattedGoalName.toLowerCase();
            formattedGoalName = formattedGoalName.replace('_', ' ');
            formattedGoalName += (goalCount > 1 && !formattedGoalName.endsWith("sh") && !formattedGoalName.endsWith("s") ? "s" : "");
            return questVerb + " " + playerCount + "/" + goalCount + "x " + formattedGoalName;
        }

        public boolean isCompleted() { return playerCount >= goalCount; }
        public void incrementPlayerCount() { playerCount++; }
        public double getXPReward() { return xpReward; }
    }

    public static class MiningQuest extends Quest
    {
        private final Material goalBlockType;

        public MiningQuest(String playerUUID, double xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount)
        {
            super(playerUUID, xpReward, goalBlockCount, playerBlockCount, "Mine");
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalBlockCount <= 0 ? questDifficulty.generateUnitAmount() : goalBlockCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);

            
            if (goalBlockType != null)
            {
                this.goalBlockType = goalBlockType;
            }
            else
            {
                HashMap<Material, String> miningQuestDifficulties = BeanPass.getInstance().questManager.getMiningQuestDifficulties();
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

            setGoalName(this.goalBlockType.name());
        }

        public Material getGoalBlockType() { return goalBlockType; }
    }

    public static class KillingQuest extends Quest
    {
        EntityType goalEntityType;

        public KillingQuest(String playerUUID, double xpReward, EntityType goalEntityType, int goalKillCount, int playerKillCount)
        {
            super(playerUUID, xpReward, goalKillCount, playerKillCount, "Kill");
            String questDifficultyKey = null;
            for (int i = 0; i < 100; i++)
            {
                questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
                if (BeanPass.getInstance().questManager.getKillingQuestDifficulties().containsValue(questDifficultyKey)) break;
            }
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalKillCount <= 0 ? questDifficulty.generateUnitAmount() : goalKillCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);


            if (goalEntityType != null)
            {
                this.goalEntityType = goalEntityType;
            }
            else
            {
                List<Map.Entry<EntityType, String>> matchingDifficultyEntity = new ArrayList<>();

                for (Map.Entry<EntityType, String> entry : BeanPass.getInstance().questManager.getKillingQuestDifficulties().entrySet()) {
                    if (entry.getValue().equals(questDifficultyKey)) {
                        matchingDifficultyEntity.add(entry);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(matchingDifficultyEntity.size());

                Map.Entry<EntityType, String> randomEntry = matchingDifficultyEntity.get(randomIndex);

                this.goalEntityType = randomEntry.getKey();
            }

            setGoalName(this.goalEntityType.name());
        }

        public EntityType getGoalEntityType() { return goalEntityType; }
    }

    public static class ExplorationQuest extends Quest
    {
        final Structure GOAL_STRUCTURE_TYPE;

        public ExplorationQuest(String playerUUID, double xpReward, Structure goalStructureType, int goalChestCount, int playerChestCount)
        {
            super(playerUUID, xpReward, goalChestCount, playerChestCount, "Loot");
            this.GOAL_STRUCTURE_TYPE = goalStructureType;
        }

        public String getGoalDescription()
        {
            return "Loot " + playerCount + "/" + goalCount + "x undiscovered chest" + (goalCount > 1 ? "s" : "") + " from a " + GOAL_STRUCTURE_TYPE.toString().replace('_', ' ').toLowerCase();
        }

        public Structure getGoalStructureType() { return GOAL_STRUCTURE_TYPE; }
    }

    // Breeding Quest

    // Fishing Quest

    // Farming Quest

    // Brewing Quest

    public static class LumberQuest extends Quest
    {

        private final Material goalBlockType;

        public LumberQuest(String playerUUID, double xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount) {
            super(playerUUID, xpReward, goalBlockCount, playerBlockCount, "Chop down");
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalBlockCount <= 0 ? questDifficulty.generateUnitAmount() : goalBlockCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);


            if (goalBlockType != null)
            {
                this.goalBlockType = goalBlockType;
            }
            else
            {
                HashMap<Material, String> miningQuestDifficulties = BeanPass.getInstance().questManager.getMiningQuestDifficulties();
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

            setGoalName(this.goalBlockType.name());
        }

        public Material getGoalBlockType() { return goalBlockType; }
    }

    // Lumber Quest (same as mining quest but specifically for wood)
}
