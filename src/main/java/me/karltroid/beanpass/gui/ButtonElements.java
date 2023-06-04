package me.karltroid.beanpass.gui;

import org.bukkit.Location;
import org.bukkit.Material;

public class ButtonElements
{
    static class LeftArrow extends ButtonElement implements Button
    {
        public LeftArrow(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, material, customModelData);
        }

        @Override
        public void click()
        {
            beanPassGUI.changeLevelsPage(-1);
        }
    }

    static class RightArrow extends ButtonElement implements Button
    {
        public RightArrow(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, material, customModelData);
        }

        @Override
        public void click()
        {
            beanPassGUI.changeLevelsPage(1);
        }
    }

    static class OpenQuestsPage extends ButtonElement implements Button
    {
        public OpenQuestsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, material, customModelData);
        }

        @Override
        public void click()
        {
            beanPassGUI.player.sendMessage("Quests!");
        }
    }

    static class OpenItemsPage extends ButtonElement implements Button
    {
        public OpenItemsPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, material, customModelData);
        }

        @Override
        public void click()
        {
            beanPassGUI.player.sendMessage("Items!");
        }
    }

    static class OpenGetPremiumPage extends ButtonElement implements Button
    {
        public OpenGetPremiumPage(BeanPassGUI beanPassGUI, boolean spherePlacement, double radiusOffset, double angleOffsetX, double angleOffsetY, float displayScale, Material material, int customModelData)
        {
            super(beanPassGUI, spherePlacement, radiusOffset, angleOffsetX, angleOffsetY, displayScale, material, customModelData);
        }

        @Override
        public void click()
        {
            beanPassGUI.player.sendMessage("Premium!");
        }
    }
}
