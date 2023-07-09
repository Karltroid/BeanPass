package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.VisualElement;
import me.karltroid.beanpass.mounts.Mount;
import org.bukkit.Material;

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

    @Override
    public void displayReward(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double xAngle, double yAngle, float displayScale)
    {
        beanPassGUI.loadElement(new VisualElement(beanPassGUI, spherePlacement, distance, xAngle, yAngle, displayScale/2, Material.GLASS_BOTTLE, mount.getId()), beanPassGUI.allLevelRewardElements);
    }
}