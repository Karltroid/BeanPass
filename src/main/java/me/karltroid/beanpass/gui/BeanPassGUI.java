package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.Rewards.MoneyReward;
import me.karltroid.beanpass.Rewards.Reward;
import me.karltroid.beanpass.Rewards.SetHomeReward;
import me.karltroid.beanpass.data.Level;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.ButtonElements.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    ButtonElement selectedButtonElement;
    ButtonElement previousButtonElement;
    int rewardPage = 0;
    static final double GUI_ROTATION_CORRECTION = 90; // 1 = 1 block/meter

    public BeanPassGUI(Player player)
    {
        this.player = player;
        this.world = player.getWorld();
        this.playerLocation = player.getEyeLocation();
        this.playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());

        player.setGameMode(GameMode.ADVENTURE);

        loadElement(new VisualElement(this, 3, 0, 22, 1f, Material.GLASS_BOTTLE, 10006));
        loadElement(new VisualElement(this, 3.05, 0, 0, 1.75f, Material.GLASS_BOTTLE, 10000));

        loadElement(new LeftArrow(this, 3.85, -41.5, 0, 0.3f, Material.GLASS_BOTTLE, 10001));
        loadElement(new RightArrow(this, 3.85, 41.5, 0, 0.3f, Material.GLASS_BOTTLE, 10002));
        loadElement(new OpenGetPremiumPage(this, 3, -29, -27, 0.48f, Material.GLASS_BOTTLE, 10003));
        loadElement(new OpenQuestsPage(this, 3, 0, -27, 0.48f, Material.GLASS_BOTTLE, 10005));
        loadElement(new OpenItemsPage(this, 3, 29, -27, 0.48f, Material.GLASS_BOTTLE, 10004));

        loadElement(new TextElement(this, 2, 0, -75, 1f, ChatColor.GREEN + "XP: " + playerData.getXp()));

        HashMap<Integer, Level> beanpassLevels = BeanPass.getInstance().getSeason().getLevels();

        int playerLevel = playerData.getLevel();
        /*int xpNeededForCurrentLevel = 0;
        int xpNeededForNextLevel = beanpassLevels.get(playerLevel + 1).getXpRequired();
        for (int i = 1; i <= beanpassLevels.size(); i++)
        {
            xpNeededForCurrentLevel += beanpassLevels.get(i).getXpRequired();
        }
        player.sendMessage("Level " + playerLevel + " -> " + xpNeededForCurrentLevel + " | " + playerData.getXp() + " | " + xpNeededForNextLevel);
        */
        for (int i = 1; i <= 5; i++)
        {
            int levelNumber = rewardPage + i;
            double xAngle = -32 + (16 * (i-1));
            loadElement(new TextElement(this, 3, xAngle, 0, 0.5f, (playerLevel >= levelNumber ? ChatColor.BOLD : ChatColor.GRAY) + "LVL" + levelNumber));

            Level level = beanpassLevels.get(levelNumber);
            if (level == null) continue;

            Reward freeReward = level.getFreeReward();

            if (freeReward == null) continue;

            if (freeReward instanceof MoneyReward)
            {
                loadElement(new TextElement(this, 3, xAngle, 8, 0.5f, ChatColor.GREEN + "$" + ((MoneyReward) level.getFreeReward()).getAmount()));
            }
            else if (freeReward instanceof SetHomeReward)
            {
                loadElement(new TextElement(this, 3, xAngle, 8, 0.5f, "+" + ((SetHomeReward) level.getFreeReward()).getAmount() + " home"));
            }
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
                    close();
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

    void loadElement(Element element)
    {
        allElements.add(element);

        if (element instanceof ButtonElement) allButtonElements.add((ButtonElement) element);
    }

    void unloadElement(Element element)
    {
        allElements.remove(element);

        if (element instanceof VisualElement)
        {
            if (element instanceof ButtonElement) allButtonElements.remove((ButtonElement) element);
            ((VisualElement)element).itemDisplay.remove();
        }
        else ((TextElement)element).textDisplay.remove();
    }

    public void close()
    {
        player.setGameMode(player.getPreviousGameMode());

        List<Element> elementsToRemove = new ArrayList<>(allElements);
        for (Element element : elementsToRemove) {
            unloadElement(element);
            allElements.remove(element);
        }

        BeanPass.getInstance().activeGUIs.remove(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
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
                loadElement(new ButtonElement(this, radiusOffset, angleYaw, anglePitch, 1f, Material.STONE, 0));
            }
        }
    }
}
