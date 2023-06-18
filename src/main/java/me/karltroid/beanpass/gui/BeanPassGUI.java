package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.Rewards.MoneyReward;
import me.karltroid.beanpass.Rewards.Reward;
import me.karltroid.beanpass.Rewards.SetHomeReward;
import me.karltroid.beanpass.Rewards.SkinReward;
import me.karltroid.beanpass.data.Level;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.data.Skins.Skin;
import me.karltroid.beanpass.gui.Elements.*;
import me.karltroid.beanpass.quests.Quests;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
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

    GUIMenu currentMenu;

    public BeanPassGUI(Player player, GUIMenu guiMenu)
    {
        BeanPassGUI alreadyOpenGUI = BeanPass.getInstance().activeGUIs.get(player);
        if (alreadyOpenGUI != null) alreadyOpenGUI.closeEntireGUI();

        this.player = player;
        this.world = player.getWorld();
        this.playerLocation = player.getEyeLocation();
        this.playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());
        this.rewardPage = playerData.getLevel()/LEVELS_PER_PAGE;
        this.originalGamemode = player.getGameMode();
        this.currentMenu = guiMenu;

        player.setGameMode(GameMode.ADVENTURE);

        switch (guiMenu)
        {
            case BeanPass:
                loadBeanPassMenu();
                break;
            case Quests:
                loadQuestsMenu();
                break;
            default:
                Bukkit.getLogger().warning("gui menu does not exist, loading BeanPass menu.");
                loadBeanPassMenu();
                break;
        }


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

    void loadBeanPassMenu()
    {
        this.currentMenu = GUIMenu.BeanPass;
        closeElementList(allElements);

        loadElement(new BeanPassTitle(this, true, 3.1, 0, 21, 1f), null);
        loadElement(new BeanPassBackground(this, false, 3.05, 0, 0, 1.75f), null);
        loadElement(new LeftArrow(this, true, 3.85, -43, 0, 0.3f), null);
        loadElement(new RightArrow(this, true,3.85, 43, 0, 0.3f), null);

        displayLevelData();
        displayNavigationButtons();
    }

    public void loadQuestsMenu()
    {
        this.currentMenu = GUIMenu.Quests;
        closeElementList(allElements);

        loadElement(new QuestsTitle(this, true, 3.1, 0, 21, 1f), null);

        int lineSpacing = 3;
        int y = 8;
        for (Quests.Quest quest : BeanPass.getInstance().getPlayerData(player.getUniqueId()).getQuests())
        {
            loadElement(new TextElement(this, false, 3, 0, y, 0.75f, ChatColor.GREEN + quest.getGoalDescription() + ChatColor.YELLOW + " " + ChatColor.BOLD + quest.getRewardDescription()), null);
            y -= lineSpacing;
        }

        displayNavigationButtons();
    }

    public void loadSkinsMenu()
    {
        this.currentMenu = GUIMenu.Skins;
        closeElementList(allElements);

        loadElement(new SkinsTitle(this, true, 3.1, 0, 25, 1f), null);

        Material[] categories = new Material[] { Material.CARVED_PUMPKIN, Material.NETHERITE_SWORD, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_AXE, Material.NETHERITE_HOE };

        double firstRowYPosition = 10.0;
        double firstColumnXPosition = -35.0;
        double columnSpacing = Math.abs((firstColumnXPosition * 2)/(categories.length - 1));
        int skinsPerColumn = 4;
        double rowSpacing = Math.abs((firstRowYPosition * 1.7)/(skinsPerColumn - 1));
        for(int x = 0; x < categories.length; x++)
        {
            double columnXPosition = firstColumnXPosition + (x * columnSpacing);
            loadElement(new VisualElement(this, false, 3, columnXPosition, firstRowYPosition + 7, 0.375f, categories[x], 0), null);

            for(int y = 0; y < skinsPerColumn; y++)
            {
                // for loop through players skins of this material
                double columnYPosition = firstRowYPosition - (y * rowSpacing);
                loadElement(new ButtonElement(this, false, 3, columnXPosition, columnYPosition, 0.25f, Material.BARRIER, 0), null);
            }
        }

        displayNavigationButtons();
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

    void displayNavigationButtons()
    {
        loadElement(new OpenBeanPassPage(this, true,3, -29, -27, 0.48f), null);
        loadElement(new OpenQuestsPage(this, true,3, 0, -27, 0.48f), null);
        loadElement(new OpenSkinsPage(this, true,3, 29, -27, 0.48f), null);
    }

    void displayLevelData()
    {
        closeElementList(allLevelRewardElements);

        HashMap<Integer, Level> beanpassLevels = BeanPass.getInstance().getSeason().getLevels();

        //loadElement(new TextElement(this, true,1.5, 0, -45, 1f, ChatColor.GREEN + "" + playerData.getXpNeededForNextLevel() + "XP needed for LVL " + playerData.getLevel() + 1), allLevelRewardElements);

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

    public void loadElement(Element element, List<Element> altElementList)
    {
        allElements.add(element);
        if (altElementList != null) altElementList.add(element);

        if (element instanceof ButtonElement) allButtonElements.add((ButtonElement) element);
    }

    public List<Element> getAllElements()
    {
        return allElements;
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

    public void closeElementList(List<Element> elements)
    {
        List<Element> elementsToRemove = new ArrayList<>(elements);
        for (Element element : elementsToRemove) {
            unloadElement(element, elements);
        }
    }

    public void closeEntireGUI()
    {
        player.setGameMode(originalGamemode);

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

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (player != this.player) return;

        closeEntireGUI();
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
