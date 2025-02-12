package me.karltroid.beanpass.mounts;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class MountManager implements Listener
{
    private static final MountManager instance = new MountManager();
    static List<Mount> mounts;
    static List<EntityType> mountTypes;
    static Map<UUID, IMount> mountInstances;
    static final DecimalFormat decimalFormat = new DecimalFormat("#.0");

    public MountManager()
    {
        FileConfiguration config = BeanPass.getInstance().getCosmeticsConfig();
        ConfigurationSection mountsSection = config.getConfigurationSection("Mounts");

        mounts = new ArrayList<>();
        mountTypes = new ArrayList<>();
        mountInstances = new HashMap<>();

        if (mountsSection != null)
        {
            // Loop through each key (skin name) in the "Skins" section
            for (String mountName : mountsSection.getKeys(false))
            {

                // Get the configuration section for the current skin
                ConfigurationSection mountSection = mountsSection.getConfigurationSection(mountName);
                if (mountSection != null)
                {
                    // Read the skin properties from the configuration
                    String entityName = mountSection.getString("Entity");
                    EntityType entityType = null;
                    for (EntityType type : EntityType.values())
                    {
                        if (type.name().equalsIgnoreCase(entityName))
                        {
                            entityType = type;
                            break;
                        }
                    }
                    if (entityType == null)
                    {
                        BeanPass.getInstance().getLogger().warning("Mount type " + entityName + " does not exist, skipping.");
                        continue;
                    }

                    int id = mountSection.getInt("ID");

                    if (!mountTypes.contains(entityType)) mountTypes.add(entityType);
                    Mount mount = new Mount(mountName.toLowerCase(), id, entityType);
                    mounts.add(mount);
                }
            }
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (mountInstances.size() == 0) return;
                for (IMount mountInstance : mountInstances.values()) mountInstance.updateMountModelRotation();
            }
        }.runTaskTimer(BeanPass.getInstance(), 0L, 0L);
    }

    public static MountManager getInstance() { return instance; }

    @EventHandler
    void onPlayerUnmount(VehicleExitEvent event)
    {
        for (IMount mountInstance : mountInstances.values())
        {
            Entity exitedEntity = event.getExited();
            if (mountInstance.getMountStructure().contains(event.getExited()) || mountInstance.getMountStructure().contains(event.getVehicle()))
            {
                if (exitedEntity instanceof Player) destroyMountInstance(mountInstance.getPlayer());
                else event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    void onEntityDeath(EntityDeathEvent event)
    {
        for (IMount mountInstance : mountInstances.values())
        {
            if (mountInstance.getMountStructure().contains(event.getEntity()))
            {
                destroyMountInstance(mountInstance.getPlayer());
                break;
            }
        }
    }

    @EventHandler
    void onPlayerMount(VehicleEnterEvent event)
    {
        if (!(event.getEntered() instanceof Player)) return;
        Vehicle vehicle = event.getVehicle();
        if (!vehicle.getPassengers().isEmpty()) return;

        createMountInstance((Player) event.getEntered(), event.getVehicle());
    }

    @EventHandler
    void onPlayerLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        IMount mountInstance = mountInstances.get(player.getUniqueId());
        if (mountInstance == null) return;

        destroyMountInstance(player);
    }

    public static void createMountInstance(Player player, Entity mountedEntity)
    {
        List<Mount> playerEquippedMounts = PlayerDataManager.getPlayerData(player.getUniqueId()).getEquippedMounts();
        if (playerEquippedMounts.size() == 0) return;

        for (Mount mount : playerEquippedMounts)
        {
            if (mount.getMountApplicant().equals(mountedEntity.getType()))
            {
                switch (mountedEntity.getType())
                {
                    case HORSE:
                        HorseMount horseMount = new HorseMount(player, mountedEntity, mount);
                        if (horseMount.getMountStructure().size() == 0) return;
                        mountInstances.put(player.getUniqueId(), horseMount);
                        break;
                    case MINECART:
                        MinecartMount minecartMount = new MinecartMount(player, mountedEntity, mount);
                        if (minecartMount.getMountStructure().size() == 0) return;
                        mountInstances.put(player.getUniqueId(), minecartMount);
                        break;
                    case BOAT:
                        BoatMount boatMount = new BoatMount(player, mountedEntity, mount);
                        if (boatMount.getMountStructure().size() == 0) return;
                        mountInstances.put(player.getUniqueId(), boatMount);
                        break;
                    default:
                        return;
                }
            }
        }
    }

    public static void destroyAllMountInstances() { for (Player player : Bukkit.getOnlinePlayers()) destroyMountInstance(player); }

    public static void destroyMountInstance(Player player)
    {
        IMount mountInstance = mountInstances.get(player.getUniqueId());
        if (mountInstance == null) return;

        mountInstance.destroyMount();

        mountInstances.remove(player.getUniqueId());
    }

    public static void changeActiveMount(Player player, Mount mount)
    {
        Entity vehicle = player.getVehicle();
        if (vehicle == null || vehicle.getType() != mount.getMountApplicant()) return;

        IMount mountInstance = mountInstances.get(player.getUniqueId());
        if (mountInstance == null) createMountInstance(player, vehicle);
        else mountInstance.setMount(mount);
    }

    public static Mount getMountByName(String name)
    {
        Mount foundMount = null;
        for(Mount mount : mounts)
        {
            if (!mount.getName().equals(name.toLowerCase())) continue;
            foundMount = mount;
            break;
        }
        return foundMount;
    }

    public static Mount getMountById(int id)
    {
        Mount foundMount = null;
        for(Mount mount : mounts)
        {
            if (mount.getId() != id) continue;
            foundMount = mount;
            break;
        }
        return foundMount;
    }
}
