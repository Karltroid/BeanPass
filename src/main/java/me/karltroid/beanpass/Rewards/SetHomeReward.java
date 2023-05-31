package me.karltroid.beanpass.Rewards;

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
        // Code to add another available /sethome for the player
        // Example: HomeManager.addSetHome(player);
    }
}
