package me.karltroid.beanpass.quests;

import me.karltroid.beanpass.BeanPass;

import me.karltroid.beanpass.npcs.NPC;
import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

import java.text.DecimalFormat;
import java.util.*;

public class Quests
{
    public static abstract class Quest
    {
        public String playerUUID;
        public double xpReward;
        public int goalCount;
        public int playerCount;
        String questVerb = "";
        String goalName = "";
        NPC questGiver;

        Quest(String playerUUID, double xpReward, int goalCount, int playerCount, String questVerb, NPC questGiver)
        {
            this.playerUUID = playerUUID;
            this.goalCount = goalCount;
            this.xpReward = xpReward;
            this.playerCount = playerCount;
            this.questVerb = questVerb;
            this.questGiver = questGiver;
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
            DecimalFormat tenths = new DecimalFormat("0.00");
            return "+" + tenths.format(xpReward) + "XP";
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
        public NPC getQuestGiver() { return questGiver; }
    }

    public static class MiningQuest extends Quest
    {
        private final Material goalBlockType;

        public MiningQuest(String playerUUID, double xpReward, Material goalBlockType, int goalBlockCount, int playerBlockCount, NPC questGiver)
        {
            super(playerUUID, xpReward, goalBlockCount, playerBlockCount, "Mine", questGiver);
            HashMap<Material, String> miningQuestDifficulties = (HashMap<Material, String>) questGiver.getQuestTypes();
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            while (!miningQuestDifficulties.containsValue(questDifficultyKey)) questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalBlockCount <= 0 ? questDifficulty.generateUnitAmount() : goalBlockCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);

            
            if (goalBlockType != null)
            {
                this.goalBlockType = goalBlockType;
            }
            else
            {
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

        public KillingQuest(String playerUUID, double xpReward, EntityType goalEntityType, int goalKillCount, int playerKillCount, NPC questGiver)
        {
            super(playerUUID, xpReward, goalKillCount, playerKillCount, "Kill", questGiver);
            HashMap<EntityType, String> killingQuestDifficulties = (HashMap<EntityType, String>) questGiver.getQuestTypes();
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            while (!killingQuestDifficulties.containsValue(questDifficultyKey)) questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
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

                for (Map.Entry<EntityType, String> entry : killingQuestDifficulties.entrySet()) {
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

    /*public static class ExplorationQuest extends Quest
    {
        final Structure GOAL_STRUCTURE_TYPE;

        public ExplorationQuest(String playerUUID, double xpReward, Structure goalStructureType, int goalChestCount, int playerChestCount, NPC questGiver)
        {
            super(playerUUID, xpReward, goalChestCount, playerChestCount, "Loot", questGiver);
            this.GOAL_STRUCTURE_TYPE = goalStructureType;
        }

        public String getGoalDescription()
        {
            return "Loot " + playerCount + "/" + goalCount + "x undiscovered chest" + (goalCount > 1 ? "s" : "") + " from a " + GOAL_STRUCTURE_TYPE.toString().replace('_', ' ').toLowerCase();
        }

        public Structure getGoalStructureType() { return GOAL_STRUCTURE_TYPE; }
    }*/

    // Breeding Quest

    // Fishing Quest

    // Farming Quest

    // Brewing Quest
}
