package me.karltroid.beanpass.Rewards;

import java.util.UUID;

public class MoneyReward implements Reward
{
    private double amount;

    public MoneyReward(double amount)
    {
        this.amount = amount;
    }

    @Override
    public void giveReward(UUID uuid)
    {
        // Code to give the player money
        // Example: EconomyAPI.deposit(player, amount);
        System.out.println("give " + uuid + "$" + getAmount());
    }

    public double getAmount()
    {
        return amount;
    }
}
