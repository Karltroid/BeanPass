package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BeanPassGUI implements Listener
{
    Player player;
    PlayerData playerData;
    private List<DisplayElement> allDisplayElements = new ArrayList<>();
    private List<ButtonElement> allButtonElements = new ArrayList<>();
    private List<TextElement> allTextElements = new ArrayList<>();
    ButtonElement selectedButtonElement;
    ButtonElement previousButtonElement;
    Buttons buttons;

    public BeanPassGUI(Player player)
    {
        this.player = player;
        this.playerData = BeanPass.getInstance().getPlayerData(player.getUniqueId());
        buttons = new Buttons();

        //allButtonElements.add(new ButtonElement(Hologram.createHolographicText(this, player, 0, 0, 45, season.getTitle())));

        //guiSphereTest(player, 45, 0);

        //allTextElements.add(new TextElement(player, 0, 0, 15, ChatColor.BOLD + "BEANPASS"));
        allDisplayElements.add(new DisplayElement(player, 0, 0, 22, buttons.get(10006)));
        allDisplayElements.add(new DisplayElement(player, 0, 0, 0, buttons.get(10000)));
        allButtonElements.add(new ButtonElement(player, 0, -40, 0, buttons.get(10001)));
        allButtonElements.add(new ButtonElement(player, 0, 40, 0, buttons.get(10002)));
        allButtonElements.add(new ButtonElement(player, -1, -28, -25, buttons.get(10003)));
        allButtonElements.add(new ButtonElement(player, -1, 0, -25, buttons.get(10005)));
        allButtonElements.add(new ButtonElement(player, -1, 28, -25, buttons.get(10004)));

        allTextElements.add(new TextElement(player, -2, 0, 0, ChatColor.GREEN + "XP: " + playerData.getXp()));


        BeanPass.getInstance().getPluginManager().registerEvents(this, BeanPass.getInstance());

        BeanPass.getInstance().activeGUIs.put(player, this);

        new BukkitRunnable() {
            public void run()
            {
                Location playerLocation = player.getLocation();
                playerLocation.add(0, player.getEyeHeight() - Hologram.ARMOR_STAND_HEIGHT, 0);

                Location firstElementLocation = allButtonElements.get(0).originalLocation;
                double distance = Math.sqrt(Math.pow(firstElementLocation.getX() - playerLocation.getX(), 2) + Math.pow(firstElementLocation.getY() - playerLocation.getY(), 2) + Math.pow(firstElementLocation.getZ() - playerLocation.getZ(), 2));
                if (distance > 5)
                {
                    cancel();
                    close();
                }

                ButtonElement newSelectedElement = null;

                for (ButtonElement element : allButtonElements)
                {
                    if (!Hologram.isLookingAt(player, element.armorStand.getLocation())) continue;

                    newSelectedElement = element;
                    break;
                }

                if (selectedButtonElement != null && newSelectedElement == selectedButtonElement) return;
                previousButtonElement = selectedButtonElement;
                selectedButtonElement = newSelectedElement;

                if (previousButtonElement != null) previousButtonElement.armorStand.teleport(previousButtonElement.originalLocation);
                if (selectedButtonElement != null)
                {
                    Vector direction = playerLocation.getDirection().normalize();
                    Location newLocation = selectedButtonElement.armorStand.getLocation().subtract(direction.divide(new Vector(3,3,3)));
                    selectedButtonElement.armorStand.teleport(newLocation);
                }
            }
        }.runTaskTimer(BeanPass.getInstance(),0,0);
    }

    public void close()
    {
        for (DisplayElement element : allDisplayElements) element.armorStand.remove();

        for (ButtonElement element : allButtonElements) element.armorStand.remove();

        for (TextElement element : allTextElements) element.armorStand.remove();

        BeanPass.getInstance().activeGUIs.remove(player);
    }

    /*@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();

        RayTraceResult result = world.rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 15);
        if (result == null) return;

        Entity hitEntity = result.getHitEntity();
        if (hitEntity == null) return;

        player.sendMessage("Nearest entity: " + hitEntity.getType().toString());
    }*/

    void guiSphereTest(Player player, double angleIncrement, double radiusOffset)
    {
        for (double angleYaw = 0.0; angleYaw < 360; angleYaw += angleIncrement)
        {
            for (double anglePitch = 0.0; anglePitch < 360; anglePitch += angleIncrement)
            {
                // Call createHolographicText with the angle offsets for X and Y positioning on the sphere
                allButtonElements.add(new ButtonElement(player, radiusOffset, angleYaw, anglePitch, buttons.get(10000)));
            }
        }
    }
}
