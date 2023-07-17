package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.gui.BeanPassGUI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Reward
{
    void giveReward(UUID uuid);
    void displayReward(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double xAngle, double yAngle, float displayScale);
}
