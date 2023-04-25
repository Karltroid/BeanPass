package me.karltroid.beanpass.gui;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Seasons.Season;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class BeanPassGUI implements Listener
{
    ArmorStand textTitle;
    private List<ButtonElement> allButtonElements = new ArrayList<>();
    private List<TextElement> allTextElements = new ArrayList<>();
    ButtonElement selectedButtonElement;
    ButtonElement previousButtonElement;
    Buttons buttons;

    public BeanPassGUI(Player player)
    {
        buttons = new Buttons();

        Season season = BeanPass.main.getActiveSeason();

        //allButtonElements.add(new ButtonElement(Hologram.createHolographicText(this, player, 0, 0, 45, season.getTitle())));

        //guiSphereTest(player, 45, 0);

        allTextElements.add(new TextElement(player, 0, 0, 15, ChatColor.BOLD + "BEANPASS"));
        allButtonElements.add(new ButtonElement(player, 0, 0, -10, buttons.get(10000)));
        allButtonElements.add(new ButtonElement(player, 0, 45, -10, buttons.get(10000)));
        allButtonElements.add(new ButtonElement(player, 0, -45, -10, buttons.get(10000)));

        BeanPass.main.pluginManager.registerEvents(this, BeanPass.main);

        BeanPass.main.activeGUIs.put(player, this);

        new BukkitRunnable() {
            public void run()
            {
                Location playerLocation = player.getLocation();
                playerLocation.add(0, player.getEyeHeight() - Hologram.ARMOR_STAND_HEIGHT, 0);

                Location firstElementLocation = allButtonElements.get(0).originalLocation;
                double distance = Math.sqrt(Math.pow(firstElementLocation.getX() - playerLocation.getX(), 2) + Math.pow(firstElementLocation.getY() - playerLocation.getY(), 2) + Math.pow(firstElementLocation.getZ() - playerLocation.getZ(), 2));
                if (distance > 5)
                {
                    for (ButtonElement element : allButtonElements)
                    {
                        element.armorStand.remove();
                    }
                    for (TextElement element : allTextElements)
                    {
                        element.armorStand.remove();
                    }

                    cancel();
                    BeanPass.main.activeGUIs.remove(player);
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
                    Location newLocation = selectedButtonElement.armorStand.getLocation().subtract(direction.divide(new Vector(2,2,2)));
                    selectedButtonElement.armorStand.teleport(newLocation);
                }
            }
        }.runTaskTimer(BeanPass.main,0,0);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();

        RayTraceResult result = world.rayTraceEntities(player.getEyeLocation(), player.getEyeLocation().getDirection(), 15);
        if (result == null) return;

        Entity hitEntity = result.getHitEntity();
        if (hitEntity == null) return;

        player.sendMessage("Nearest entity: " + hitEntity.getType().toString());
    }

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
