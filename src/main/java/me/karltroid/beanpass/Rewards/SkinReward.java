package me.karltroid.beanpass.Rewards;

import java.util.UUID;

public class SkinReward implements Reward
{
    private String skinName;

    public SkinReward(String skinName)
    {
        this.skinName = skinName;
    }

    @Override
    public void giveReward(UUID uuid)
    {

    }
}
