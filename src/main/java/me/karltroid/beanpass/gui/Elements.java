package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Skin;
import me.karltroid.beanpass.mounts.Mount;
import org.bukkit.Material;

public class Elements
{
    static class LeftArrow extends ButtonElement implements Button
    {
        public LeftArrow(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10001);
        }

        @Override
        public void click()
        {
            beanPassGUI.changeLevelsPage(-1);
        }
    }

    static class RightArrow extends ButtonElement implements Button
    {
        public RightArrow(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10002);
        }

        @Override
        public void click()
        {
            beanPassGUI.changeLevelsPage(1);
        }
    }

    static class OpenQuestsPage extends ButtonElement implements Button
    {
        public OpenQuestsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10005);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadQuestsMenu();
        }
    }

    static class OpenHatsPage extends ButtonElement implements Button
    {
        public OpenHatsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10014);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadHatsMenu();
        }
    }

    static class OpenMountsPage extends ButtonElement implements Button
    {
        public OpenMountsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10015);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadMountsMenu();
        }
    }

    static class OpenRewardsPage extends ButtonElement implements Button
    {
        public OpenRewardsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10004);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadRewardsMenu();
        }
    }

    static class OpenToolsPage extends ButtonElement implements Button
    {
        public OpenToolsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10016);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadToolsMenu();
        }
    }

    static class OpenGetPremiumPage extends ButtonElement implements Button
    {
        public OpenGetPremiumPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10003);
        }

        @Override
        public void click()
        {
            beanPassGUI.player.sendMessage("Get Premium!");
        }
    }

    static class OpenBeanPassPage extends ButtonElement implements Button
    {
        public OpenBeanPassPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10007);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadBeanPassMenu();
        }
    }

    static class BeanPassTitle extends VisualElement
    {
        public BeanPassTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10006);
        }
    }

    static class RewardsTitle extends VisualElement
    {
        public RewardsTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10009);
        }
    }

    static class HatsTitle extends VisualElement
    {
        public HatsTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10011);
        }
    }

    static class MountsTitle extends VisualElement
    {
        public MountsTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10012);
        }
    }

    static class ToolsTitle extends VisualElement
    {
        public ToolsTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10013);
        }
    }

    static class QuestsTitle extends VisualElement
    {
        public QuestsTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10008);
        }
    }

    static class BeanPassBackground extends VisualElement
    {
        public BeanPassBackground(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10000);
        }
    }

    static class EquipSkin extends ButtonElement implements Button
    {
        Skin skin;

        public EquipSkin(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Skin skin)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, skin.getSkinApplicant(), skin.getId());
            this.skin = skin;
        }

        @Override
        public void click()
        {
            beanPassGUI.playerData.equipSkin(skin, true);
            BeanPass.getInstance().skinManager.updateInventorySkins(beanPassGUI.player, beanPassGUI.player.getInventory());
        }
    }

    static class EquipMount extends ButtonElement implements Button
    {
        Mount mount;

        public EquipMount(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Mount mount)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, mount.getId());
            this.mount = mount;
        }

        @Override
        public void click()
        {
            beanPassGUI.playerData.equipMount(mount, true);
            BeanPass.getInstance().mountManager.changeActiveMount(beanPassGUI.player, mount);
        }
    }
}
