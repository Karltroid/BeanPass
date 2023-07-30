package me.karltroid.beanpass.quests;

import me.karltroid.beanpass.BeanPass;

import me.karltroid.beanpass.npcs.NPC;
import me.karltroid.beanpass.other.Utils;
import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.*;

public class Quests
{
    public static abstract class Quest
    {
        public String playerUUID;
        NPC questGiver;
        String goalName = "";
        public int goalCount;
        public int playerCount;
        public double xpReward;

        Quest(String playerUUID, NPC questGiver, int goalCount, int playerCount, double xpReward)
        {
            this.playerUUID = playerUUID;
            this.questGiver = questGiver;
            this.goalCount = goalCount;
            this.playerCount = playerCount;
            this.xpReward = xpReward;
        }

        public void setGoalName(String goalName)
        {
            this.goalName = goalName;
        }

        public String getRewardDescription()
        {
            return "+" + Utils.formatDouble(xpReward) + "XP";
        }
        public String getGoalDescription()
        {
            String formattedGoalName = goalName;
            formattedGoalName = formattedGoalName.toLowerCase();
            formattedGoalName = formattedGoalName.replace('_', ' ');
            formattedGoalName += (goalCount > 1 && !formattedGoalName.endsWith("sh") && !formattedGoalName.endsWith("s") ? "s" : "");
            return questGiver.getQuestVerb() + " " + playerCount + "/" + goalCount + "x " + formattedGoalName;
        }

        public boolean isCompleted() { return playerCount >= goalCount; }
        public void incrementPlayerCount(int amount) { playerCount += amount; }
        public double getXPReward() { return xpReward; }
        public NPC getQuestGiver() { return questGiver; }
    }

    public static class MiningQuest extends Quest
    {
        private final Material goalBlockType;

        public MiningQuest(String playerUUID, NPC questGiver, Material goalBlockType, int goalBlockCount, int playerBlockCount, double xpReward)
        {
            super(playerUUID, questGiver, goalBlockCount, playerBlockCount, xpReward);
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

        public KillingQuest(String playerUUID, NPC questGiver, EntityType goalEntityType, int goalKillCount, int playerKillCount, double xpReward)
        {
            super(playerUUID, questGiver, goalKillCount, playerKillCount, xpReward);
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

    public static class FishingQuest extends Quest
    {
        private final Material goalItemType;

        public FishingQuest(String playerUUID, NPC questGiver, Material goalItemType, int goalItemCount, int playerItemCount, double xpReward)
        {
            super(playerUUID, questGiver, goalItemCount, playerItemCount, xpReward);
            HashMap<Material, String> fishingQuestDifficulties = (HashMap<Material, String>) questGiver.getQuestTypes();
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            while (!fishingQuestDifficulties.containsValue(questDifficultyKey)) questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalItemCount <= 0 ? questDifficulty.generateUnitAmount() : goalItemCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);


            if (goalItemType != null)
            {
                this.goalItemType = goalItemType;
            }
            else
            {
                List<Map.Entry<Material, String>> matchingDifficultyMaterials = new ArrayList<>();

                for (Map.Entry<Material, String> entry : fishingQuestDifficulties.entrySet()) {
                    if (entry.getValue().equals(questDifficultyKey)) {
                        matchingDifficultyMaterials.add(entry);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(matchingDifficultyMaterials.size());

                Map.Entry<Material, String> randomEntry = matchingDifficultyMaterials.get(randomIndex);

                this.goalItemType = randomEntry.getKey();
            }

            setGoalName(this.goalItemType.name());
        }

        public Material getGoalItemType() { return goalItemType; }

    }

    public static class BrewingQuest extends Quest
    {
        private final PotionType goalPotionType;

        public BrewingQuest(String playerUUID, NPC questGiver, PotionType goalPotionType, int goalItemCount, int playerItemCount, double xpReward)
        {
            super(playerUUID, questGiver, goalItemCount, playerItemCount, xpReward);
            HashMap<PotionType, String> brewingQuestDifficulties = (HashMap<PotionType, String>) questGiver.getQuestTypes();
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            while (!brewingQuestDifficulties.containsValue(questDifficultyKey)) questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalItemCount <= 0 ? questDifficulty.generateUnitAmount() : goalItemCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);


            if (goalPotionType != null)
            {
                this.goalPotionType = goalPotionType;
            }
            else
            {
                List<Map.Entry<PotionType, String>> matchingDifficultyPotions = new ArrayList<>();

                for (Map.Entry<PotionType, String> entry : brewingQuestDifficulties.entrySet()) {
                    if (entry.getValue().equals(questDifficultyKey)) {
                        matchingDifficultyPotions.add(entry);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(matchingDifficultyPotions.size());

                Map.Entry<PotionType, String> randomEntry = matchingDifficultyPotions.get(randomIndex);

                this.goalPotionType = randomEntry.getKey();
            }

            setGoalName(this.goalPotionType.name());
        }

        public PotionType getGoalItemType() { return goalPotionType; }
    }

    public static class CraftingQuest extends Quest
    {
        private final Material goalItemType;

        public CraftingQuest(String playerUUID, NPC questGiver, Material goalItemType, int goalItemCount, int playerItemCount, double xpReward)
        {
            super(playerUUID, questGiver, goalItemCount, playerItemCount, xpReward);
            HashMap<Material, String> craftingQuestDifficulties = (HashMap<Material, String>) questGiver.getQuestTypes();
            String questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            while (!craftingQuestDifficulties.containsValue(questDifficultyKey)) questDifficultyKey = BeanPass.getInstance().questDifficulties.getRandom();
            QuestDifficulty questDifficulty = BeanPass.getInstance().questDifficulties.get(questDifficultyKey);

            this.goalCount = (goalItemCount <= 0 ? questDifficulty.generateUnitAmount() : goalItemCount);
            this.xpReward = (xpReward <= 0 ? questDifficulty.generateXPAmount(this.goalCount) : xpReward);


            if (goalItemType != null)
            {
                this.goalItemType = goalItemType;
            }
            else
            {
                List<Map.Entry<Material, String>> matchingDifficultyMaterials = new ArrayList<>();

                for (Map.Entry<Material, String> entry : craftingQuestDifficulties.entrySet()) {
                    if (entry.getValue().equals(questDifficultyKey)) {
                        matchingDifficultyMaterials.add(entry);
                    }
                }

                Random random = new Random();
                int randomIndex = random.nextInt(matchingDifficultyMaterials.size());

                Map.Entry<Material, String> randomEntry = matchingDifficultyMaterials.get(randomIndex);

                this.goalItemType = randomEntry.getKey();
            }

            setGoalName(this.goalItemType.name());
        }

        public Material getGoalItemType() { return goalItemType; }

    }

    // Farming Quest

    // Brewing Quest
}
