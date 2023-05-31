package me.karltroid.beanpass.data;

import me.karltroid.beanpass.Rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

enum QuestRewardType
{
    MONEY,
    SET_HOME,
    SKIN,
    NONE
}

public class Level
{
    int xpRequired;
    Reward freeReward;
    Reward premiumReward;

    public Level(int xpRequired, Reward freeReward, Reward premiumReward)
    {
        this.xpRequired = xpRequired;
        this.freeReward = freeReward;
        this.premiumReward = premiumReward;
    }

    public Reward getFreeReward()
    {
        return freeReward;
    }

    public Reward getPremiumReward()
    {
        return premiumReward;
    }
}
