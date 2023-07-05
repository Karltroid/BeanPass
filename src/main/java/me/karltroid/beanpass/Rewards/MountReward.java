package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.mounts.Mount;

import java.util.UUID;

public class MountReward implements Reward
{
    private final Mount mount;

    public MountReward(Mount mount)
    {
        this.mount = mount;
    }

    public Mount getMount() {
        return mount;
    }

    @Override
    public void giveReward(UUID uuid)
    {
        BeanPass.getInstance().getPlayerData(uuid).giveMount(mount, true);
    }
}