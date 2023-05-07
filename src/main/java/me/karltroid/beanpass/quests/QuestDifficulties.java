package me.karltroid.beanpass.quests;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuestDifficulties
{
    Map<String, QuestDifficulty> difficulties = new HashMap<>();

    public QuestDifficulties()
    {
        loadDifficulties();
    }

    public QuestDifficulty get(String difficulty) { return difficulties.get(difficulty); }

    public String getRandom()
    {
        Random random = new Random();
        int randomIndex = random.nextInt(difficulties.size());
        String randomKey = (String) difficulties.keySet().toArray()[randomIndex];

        return randomKey;
    }

    private void loadDifficulties()
    {
        FileConfiguration config = BeanPass.main.getConfig();
        ConfigurationSection difficultiesSection = config.getConfigurationSection("Difficulties");

        if (difficultiesSection == null)
        {
            BeanPass.main.getLogger().warning("Couldn't get the Difficulties section in your Config.yml");
            return;
        }

        for (String difficulty : difficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = difficultiesSection.getConfigurationSection(difficulty);

            if (difficultySection == null) {
                BeanPass.main.getLogger().warning("Couldn't get the " + difficulty + " difficulty in your Config.yml");
                continue;
            }

            int baseUnits = difficultySection.getInt("base_units");
            int maxUnitMultiplier = difficultySection.getInt("max_unit_multiplier");
            double xpPerUnit = difficultySection.getDouble("xp_per_unit");

            QuestDifficulty questDifficulty = new QuestDifficulty(baseUnits, maxUnitMultiplier, xpPerUnit);
            difficulties.put(difficulty, questDifficulty);
        }
    }

    public static class QuestDifficulty
    {
        private final int baseUnits;
        private final int maxUnitMultiplier;
        private final double xpPerUnit;

        public QuestDifficulty(int baseUnits, int maxUnitMultiplier, double xpPerUnit)
        {
            this.baseUnits = baseUnits;
            this.maxUnitMultiplier = maxUnitMultiplier;
            this.xpPerUnit = xpPerUnit;
        }

        public int generateUnitAmount()
        {
            Random random = new Random();
            int multiplier = random.nextInt(maxUnitMultiplier) + 1;
            return baseUnits * multiplier;
        }

        public double generateXPAmount(int units)
        {
            return units * xpPerUnit;
        }
    }
}


