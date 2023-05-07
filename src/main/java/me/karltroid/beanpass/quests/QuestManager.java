package me.karltroid.beanpass.quests;


import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Quests;
import me.karltroid.beanpass.data.Quests.MiningQuest;
import me.karltroid.beanpass.data.Quests.Quest;
import me.karltroid.beanpass.data.SeasonPlayer;
import me.karltroid.beanpass.enums.ServerGamemode;
import me.karltroid.beanpass.quests.QuestDifficulties.QuestDifficulty;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.UUID;

public class QuestManager implements Listener
{
    HashMap<Material, String> miningQuestDifficulties = new HashMap<>();

    public QuestManager()
    {
        loadQuestDifficulties();
    }

    void loadQuestDifficulties()
    {
        FileConfiguration config = BeanPass.main.getConfig();
        ConfigurationSection difficultiesSection = config.getConfigurationSection("MiningQuestDifficulties");

        if (difficultiesSection == null)
        {
            BeanPass.main.getLogger().warning("Couldn't get the MiningQuestDifficulties section in your Config.yml");
            return;
        }

        for (String materialName : difficultiesSection.getKeys(false))
        {
            ConfigurationSection difficultySection = difficultiesSection.getConfigurationSection(materialName);

            if (difficultySection == null)
            {
                BeanPass.main.getLogger().warning("Couldn't get the " + materialName + " mining difficulty in your Config.yml");
                continue;
            }

            Material material = Material.matchMaterial(materialName);
            String difficulty = difficultySection.getString("difficulty");

            miningQuestDifficulties.put(material, difficulty);
        }
    }

    public HashMap<Material, String> getMiningQuestDifficulties()
    {
        return miningQuestDifficulties;
    }

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

        miningQuest.incrementPlayerCount();
        if (miningQuest.isCompleted())
        {
            seasonPlayer.addXp(miningQuest.getXPReward());
            seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).remove(miningQuest);

            event.getPlayer().sendMessage(ChatColor.GREEN + "COMPLETED: " + miningQuest.getGoalDescription());
            event.getPlayer().sendMessage(ChatColor.YELLOW + miningQuest.getRewardDescription());
            seasonPlayer.getQuests(BeanPass.main.getServerGamemode()).add(new MiningQuest(ServerGamemode.SURVIVAL, playerUUID.toString(), -1, null, -1, 0));
        }
    }
}
