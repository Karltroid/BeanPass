package me.karltroid.beanpass.mounts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.karltroid.beanpass.BeanPass;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.*;

public class MountManager implements Listener
{
    Map<UUID, MountInstance> mountInstances = new HashMap<>();

    public MountManager()
    {
        BeanPass.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(BeanPass.getInstance(), PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event)
            {
                Player player = event.getPlayer();
                MountInstance mountInstance = mountInstances.get(player.getUniqueId());
                if (mountInstance == null) return;

                PacketContainer packet = event.getPacket();
                StructureModifier<Float> floats = packet.getFloat();

                float sidewaysMovement = floats.read(0);
                float forwardsMovement = floats.read(1);
                mountInstance.updateMountMovement(sidewaysMovement, forwardsMovement);
            }
        });
    }

    @EventHandler
    void onPlayerUnmount(VehicleExitEvent event)
    {
        if (!(event.getExited() instanceof Player)) return;
        MountInstance mountInstance = mountInstances.get(event.getExited().getUniqueId());
        if (mountInstance == null) return;
        mountInstances.remove(mountInstance);
    }

    public void createMountInstance(Player player)
    {
        Mount mountTest = new Mount("test", 1, EntityType.HORSE, 1);
        mountInstances.put(player.getUniqueId(), new MountInstance(player, null, mountTest));
    }
}
