package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.TextElement;

import java.util.UUID;

public class SetPWarpReward implements Reward
{
    private int amount;

    public SetPWarpReward(int amount)
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

    @Override
    public void displayReward(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double xAngle, double yAngle, float displayScale)
    {
        beanPassGUI.loadElement(new TextElement(beanPassGUI, spherePlacement, distance, xAngle, yAngle, displayScale, "+" + getAmount() + " warp" + ((getAmount() > 1) ? "s" : "")), beanPassGUI.allLevelRewardElements);
    }
}
