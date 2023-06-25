package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Skin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
        BeanPass.getInstance().getPlayerData(uuid).giveSkin(skin, true);
    }
}
