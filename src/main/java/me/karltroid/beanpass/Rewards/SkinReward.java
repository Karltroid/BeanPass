package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Skin;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.TextElement;
import me.karltroid.beanpass.gui.VisualElement;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkinReward implements Reward
{
    private final Skin skin;

    public SkinReward(Skin skin)
    {
        if (skin == null) throw new IllegalArgumentException();
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public void giveReward(UUID uuid)
    {
        BeanPass.getInstance().getPlayerData(uuid).giveSkin(skin, true);
    }

    @Override
    public void displayReward(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double xAngle, double yAngle, float displayScale)
    {
        beanPassGUI.loadElement(new VisualElement(beanPassGUI, spherePlacement, distance, xAngle, yAngle, displayScale, 3, skin.getSkinApplicant(), skin.getId()), beanPassGUI.allLevelRewardElements);
    }
}
