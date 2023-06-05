package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.data.Skins;
import me.karltroid.beanpass.data.Skins.Skin;
import org.bukkit.Material;

import java.util.UUID;

public class SkinReward implements Reward
{
    private final Skin skin;

    public SkinReward(Skin skin)
    {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public void giveReward(UUID uuid)
    {

    }
}
