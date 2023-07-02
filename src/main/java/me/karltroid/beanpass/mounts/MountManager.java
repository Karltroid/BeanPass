package me.karltroid.beanpass.mounts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.karltroid.beanpass.BeanPass;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class MountManager implements Listener
{
    Map<UUID, MountInstance> mountInstances = new HashMap<>();

    public MountManager()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (MountInstance mountInstance : mountInstances.values()) mountInstance.updateMountMovement();
            }
        }.runTaskTimer(BeanPass.getInstance(), 0L, 1L);
    }

    @EventHandler
    void onPlayerUnmount(VehicleExitEvent event)
    {
        for (MountInstance mountInstance : mountInstances.values())
        {
            if (mountInstance.player == event.getExited() || mountInstance.mountEntity == event.getVehicle())
            {
                destroyMountInstance(mountInstance.player);
                break;
            }
        }
    }

    @EventHandler
    void onPlayerMount(VehicleEnterEvent event)
    {
        if (!(event.getEntered() instanceof Player)) return;
        Player player = (Player) event.getEntered();
        if (!(event.getVehicle() instanceof Horse)) return;
        Horse horse = (Horse)event.getVehicle();
        if (!(horse.isTamed())) return;
        if (horse.getInventory().getSaddle() == null) return;

        createMountInstance(player, horse);
    }

    @EventHandler
    void onPlayerLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        MountInstance mountInstance = mountInstances.get(player.getUniqueId());
        if (mountInstance == null) return;

        destroyMountInstance(player);
    }

    @EventHandler
    void onServerShutdown(PluginDisableEvent event)
    {
        for (MountInstance mountInstance : mountInstances.values())
        {
            destroyMountInstance(mountInstance.player);
        }
    }

    public void createMountInstance(Player player, Entity mountedEntity)
    {
        Mount mountTest = new Mount("test", 1, EntityType.HORSE, 1);
        mountInstances.put(player.getUniqueId(), new MountInstance(player, mountedEntity, mountTest));
    }

    public void destroyMountInstance(Player player)
    {
        MountInstance mountInstance = mountInstances.get(player.getUniqueId());
        if (mountInstance == null) return;

        mountInstance.player.removePassenger(mountInstance.customModelDisplay);
        mountInstance.customModelDisplay.remove();

        Horse horse = (Horse)mountInstance.mountEntity;
        horse.setAdult();
        horse.setInvisible(false);

        mountInstances.remove(player.getUniqueId());
    }
}
