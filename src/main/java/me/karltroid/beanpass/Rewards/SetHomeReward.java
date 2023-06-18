package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;

import java.util.UUID;

public class SetHomeReward implements Reward
{
    private int amount;

    public SetHomeReward(int amount)
    {
        this.amount = amount;
    }

    @Override
    public void giveReward(UUID uuid)
    {
        BeanPass.getInstance().getPlayerData(uuid).increaseMaxHomes(amount);
    }

    public int getAmount()
    {
        return amount;
    }
}
