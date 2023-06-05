package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.Rewards.MoneyReward;
import me.karltroid.beanpass.Rewards.Reward;
import me.karltroid.beanpass.Rewards.SetHomeReward;
import me.karltroid.beanpass.Rewards.SkinReward;
import me.karltroid.beanpass.data.Level;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.data.Skins;
import me.karltroid.beanpass.data.Skins.Skin;
import me.karltroid.beanpass.gui.ButtonElements.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BeanPassGUI implements Listener
{
    World world;
    Player player;
    Location playerLocation;
    GameMode originalGamemode;
    PlayerData playerData;
    private List<Element> allElements = new ArrayList<>();
    private List<ButtonElement> allButtonElements = new ArrayList<>();
    private List<Element> allLevelRewardElements = new ArrayList<>();
    ButtonElement selectedButtonElement;
    ButtonElement previousButtonElement;
    int rewardPage = 0;
    static final double GUI_ROTATION_CORRECTION = 90; // 1 = 1 block/meter
    final static int LEVELS_PER_PAGE = 5;

    public BeanPassGUI(Player player)
    {
        this.player = player;
        this.world = player.getWorld();
        this.playerLocation = player.getEyeLocation();
        this.playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());
        this.rewardPage = playerData.getLevel()/LEVELS_PER_PAGE;

        player.setGameMode(GameMode.ADVENTURE);

        loadElement(new VisualElement(this, true, 3.1, 0, 21, 1f, Material.GLASS_BOTTLE, 10006), null);
        loadElement(new VisualElement(this, false, 3.05, 0, 0, 1.75f, Material.GLASS_BOTTLE, 10000), null);

        loadElement(new LeftArrow(this, true, 3.85, -43, 0, 0.3f, Material.GLASS_BOTTLE, 10001), null);
        loadElement(new RightArrow(this, true,3.85, 43, 0, 0.3f, Material.GLASS_BOTTLE, 10002), null);
        loadElement(new OpenGetPremiumPage(this, true,3, -29, -27, 0.48f, Material.GLASS_BOTTLE, 10003), null);
        loadElement(new OpenQuestsPage(this, true,3, 0, -27, 0.48f, Material.GLASS_BOTTLE, 10005), null);
        loadElement(new OpenItemsPage(this, true,3, 29, -27, 0.48f, Material.GLASS_BOTTLE, 10004), null);

        loadElement(new TextElement(this, true,1.5, 0, -45, 1f, ChatColor.GREEN + "" + playerData.getXpNeededForNextLevel() + "XP needed for LVL " + playerData.getLevel() + 1), allLevelRewardElements);

        /*int xpNeededForCurrentLevel = 0;
        int xpNeededForNextLevel = beanpassLevels.get(playerLevel + 1).getXpRequired();
        for (int i = 1; i <= beanpassLevels.size(); i++)
        {
            xpNeededForCurrentLevel += beanpassLevels.get(i).getXpRequired();
        }
        player.sendMessage("Level " + playerLevel + " -> " + xpNeededForCurrentLevel + " | " + playerData.getXp() + " | " + xpNeededForNextLevel);
        */

        displayLevelData();

        BeanPass.getInstance().getPluginManager().registerEvents(this, BeanPass.getInstance());

        BeanPass.getInstance().activeGUIs.put(player, this);

        new BukkitRunnable() {
            public void run()
            {
                Location playerLocation = player.getLocation();
                playerLocation.add(0, player.getEyeHeight(), 0);

                Location firstElementLocation = allElements.get(0).location;
                double distance = Math.sqrt(Math.pow(firstElementLocation.getX() - playerLocation.getX(), 2) + Math.pow(firstElementLocation.getY() - playerLocation.getY(), 2) + Math.pow(firstElementLocation.getZ() - playerLocation.getZ(), 2));
                if (distance > 5)
                {
                    cancel();
                    closeEntireGUI();
                }

                ButtonElement newSelectedElement = null;

                for (ButtonElement element : allButtonElements)
                {
                    if (!element.isPlayerLooking(player)) continue;

                    newSelectedElement = element;
                    break;
                }

                if (selectedButtonElement != null && newSelectedElement == selectedButtonElement) return;
                previousButtonElement = selectedButtonElement;
                selectedButtonElement = newSelectedElement;

                if (previousButtonElement != null) previousButtonElement.unselect();
                if (selectedButtonElement != null) selectedButtonElement.select();

            }
        }.runTaskTimer(BeanPass.getInstance(),0,0);
    }

    public void reloadLevelElements()
    {
        if (allLevelRewardElements.isEmpty()) return;
        this.rewardPage = playerData.getLevel()/LEVELS_PER_PAGE;
        changeLevelsPage(0);
    }

    public void changeLevelsPage(int pageChange)
    {
        int newPage = this.rewardPage + pageChange;
        if (newPage < 0) return;

        this.rewardPage = newPage;
        displayLevelData();
    }

    void displayLevelData()
    {
        closeElementList(allLevelRewardElements);

        HashMap<Integer, Level> beanpassLevels = BeanPass.getInstance().getSeason().getLevels();

        int playerLevel = playerData.getLevel();

        for (int i = 1; i <= LEVELS_PER_PAGE; i++)
        {
            int levelNumber = (rewardPage * LEVELS_PER_PAGE) + i;
            double angleIncrement = 14.5;
            double xAngle = (angleIncrement * -2) + (angleIncrement * (i-1));

            if (playerLevel == levelNumber)
            {
                loadElement(new TextElement(this, false, 3, xAngle, 0, 0.7f, ChatColor.GOLD + "" + ChatColor.BOLD + "LVL" + levelNumber), allLevelRewardElements);
            }
            else if (playerLevel > levelNumber)
            {
                loadElement(new TextElement(this, false, 3, xAngle, 0, 0.6f, ChatColor.BOLD + "LVL" + levelNumber), allLevelRewardElements);
            }
            else loadElement(new TextElement(this, false, 3, xAngle, 0, 0.50f, ChatColor.GRAY + "LVL" + levelNumber), allLevelRewardElements);

            Level level = beanpassLevels.get(levelNumber);
            if (level == null) continue;

            Reward freeReward = level.getFreeReward();
            if (freeReward != null)
            {
                if (freeReward instanceof MoneyReward)
                {
                    loadElement(new TextElement(this, false, 3, xAngle, 6, 0.5f, ChatColor.GREEN + "$" + ((MoneyReward) level.getFreeReward()).getAmount()), allLevelRewardElements);
                }
                else if (freeReward instanceof SetHomeReward)
                {
                    loadElement(new TextElement(this, false, 3, xAngle, 6, 0.5f, "+" + ((SetHomeReward) level.getFreeReward()).getAmount() + " home"), allLevelRewardElements);
                }
            }

            Reward premiumReward = level.getPremiumReward();
            if (premiumReward != null)
            {
                if (premiumReward instanceof SkinReward)
                {
                    SkinReward skinReward = (SkinReward) premiumReward;
                    Skin skin = skinReward.getSkin();
                    if (skin == null) continue;
                    loadElement(new VisualElement(this, false, 3, xAngle, -6, 0.5f, skin.getSKIN_APPLICANT(), skin.getCUSTOM_MODEL_DATA()), allLevelRewardElements);
                }
            }
        }
    }

    void loadElement(Element element, List<Element> altElementList)
    {
        allElements.add(element);
        if (altElementList != null) altElementList.add(element);

        if (element instanceof ButtonElement) allButtonElements.add((ButtonElement) element);
    }

    void unloadElement(Element element, List<Element> altElementList)
    {
        allElements.remove(element);
        if (altElementList != null && altElementList != allElements) altElementList.remove(element);

        if (element instanceof VisualElement)
        {
            if (element instanceof ButtonElement) allButtonElements.remove((ButtonElement) element);
            ((VisualElement)element).itemDisplay.remove();
        }
        else ((TextElement)element).textDisplay.remove();
    }

    void closeElementList(List<Element> elements)
    {
        List<Element> elementsToRemove = new ArrayList<>(elements);
        for (Element element : elementsToRemove) {
            unloadElement(element, elements);
        }
    }

    public void closeEntireGUI()
    {
        player.setGameMode(player.getPreviousGameMode());

        closeElementList(allElements);

        BeanPass.getInstance().activeGUIs.remove(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (selectedButtonElement == null) return;

        Player player = event.getPlayer();
        if (player != this.player) return;

        Button button = selectedButtonElement;
        button.click();
    }

    void guiSphereTest(double angleIncrement, double radiusOffset)
    {
        for (double angleYaw = 0.0; angleYaw < 360; angleYaw += angleIncrement)
        {
            for (double anglePitch = 0.0; anglePitch < 360; anglePitch += angleIncrement)
            {
                // Call createHolographicText with the angle offsets for X and Y positioning on the sphere
                loadElement(new ButtonElement(this, true, radiusOffset, angleYaw, anglePitch, 1f, Material.STONE, 0), null);
            }
        }
    }
}
