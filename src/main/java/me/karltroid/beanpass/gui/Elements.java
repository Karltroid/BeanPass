package me.karltroid.beanpass.gui;

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

    static class OpenSkinsPage extends ButtonElement implements Button
    {
        public OpenSkinsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10004);
        }

        @Override
        public void click()
        {
            beanPassGUI.loadSkinsMenu();
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

    static class SkinsTitle extends VisualElement
    {
        public SkinsTitle(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, Material.GLASS_BOTTLE, 10009);
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
}
