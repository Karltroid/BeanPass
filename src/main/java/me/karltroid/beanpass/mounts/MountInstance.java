package me.karltroid.beanpass.mounts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.karltroid.beanpass.BeanPass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MountInstance
{
    Player player;
    Entity mountEntity;
    Mount mount;
    ArmorStand customModelDisplay;

    public MountInstance(Player player, Entity preMountEntity, Mount mount)
    {
        this.player = player;
        this.mount = mount;
        this.mountEntity = preMountEntity;

        Location mountOwnerLocation = player.getLocation();
        World world = mountOwnerLocation.getWorld();

        Horse mountBase = (Horse)preMountEntity;
        mountBase.setBaby();
        mountBase.setInvisible(true);

        ArmorStand armorStand = (ArmorStand)world.spawnEntity(mountOwnerLocation, EntityType.ARMOR_STAND);
        armorStand.setMarker(true);
        armorStand.setInvisible(true);
        ItemStack customModel = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta customModelMeta = customModel.getItemMeta();
        customModelMeta.setCustomModelData(10010);
        customModel.setItemMeta(customModelMeta);
        armorStand.getEquipment().setHelmet(customModel);

        this.customModelDisplay = armorStand;
        player.addPassenger(armorStand);
    }

    public void updateMountMovement()
    {
        // Get the player's yaw
        float yaw = player.getEyeLocation().getYaw();

        // Set the armor stand's rotation
        customModelDisplay.setRotation(yaw, 0);
    }
}
