package me.karltroid.beanpass.Rewards;

import java.util.UUID;

public class MoneyReward implements Reward
{
    private int amount;

    public MoneyReward(int amount)
    {
        this.amount = amount;
    }

    @Override
    public void giveReward(UUID uuid)
    {
        // Code to give the player money
        // Example: EconomyAPI.deposit(player, amount);
    }
}
