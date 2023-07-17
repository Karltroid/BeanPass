package me.karltroid.beanpass.mounts;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class BoatMount implements IMount
{
    Player player;
    Boat mountedEntity;
    Boat.Type originalBoatType;
    ArmorStand mountModel;
    List<Entity> mountStructure = new ArrayList<>();

    public BoatMount(Player player, Entity mountedEntity, Mount mount)
    {
        this.player = player;
        this.mountedEntity = (Boat)mountedEntity;
        this.originalBoatType = this.mountedEntity.getBoatType();
        createMount(mount);
    }

    @Override
    public void createMount(Mount mount)
    {
        Location mountOwnerLocation = player.getLocation();
        World world = mountOwnerLocation.getWorld();

        Tadpole customModelRotationBuffer = (Tadpole)world.spawnEntity(mountOwnerLocation, EntityType.TADPOLE);
        customModelRotationBuffer.setAI(false);
        customModelRotationBuffer.setInvisible(true);
        customModelRotationBuffer.setInvulnerable(true);
        customModelRotationBuffer.setSilent(true);
        customModelRotationBuffer.setInvulnerable(true);
        customModelRotationBuffer.setRotation(0,0);

        ArmorStand armorStand = (ArmorStand)world.spawnEntity(mountOwnerLocation, EntityType.ARMOR_STAND);
        armorStand.setMarker(true);
        armorStand.setInvisible(true);
        armorStand.setRotation(0,0);
        armorStand.setInvulnerable(true);
        ItemStack customModel = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta customModelMeta = customModel.getItemMeta();
        customModelMeta.setCustomModelData(mount.getId());
        customModel.setItemMeta(customModelMeta);
        armorStand.getEquipment().setHelmet(customModel);
        mountModel = armorStand;

        mountedEntity.setBoatType(Boat.Type.BAMBOO);

        mountStructure.add(this.mountedEntity);
        mountStructure.add(player);
        mountStructure.add(customModelRotationBuffer);
        mountStructure.add(armorStand);

        // stack all the mountStructure entities together after horse and player
        for (int i = 2; i < mountStructure.size(); i++)
        {
            mountStructure.get(i-1).addPassenger(mountStructure.get(i));
        }
    }

    @Override
    public void destroyMount()
    {
        for (int i = mountStructure.size() - 1; i >= 2; i--)
        {
            mountStructure.get(i - 1).removePassenger(mountStructure.get(i));
            mountStructure.get(i).remove();
        }

        mountedEntity.setBoatType(originalBoatType);
    }

    @Override
    public void setMount(Mount mount)
    {
        ItemStack customModel = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta customModelMeta = customModel.getItemMeta();
        customModelMeta.setCustomModelData(mount.getId());
        customModel.setItemMeta(customModelMeta);
        getMountModel().getEquipment().setHelmet(customModel);
    }

    @Override
    public void updateMountModelRotation()
    {
        if (getMountModel() == null)
        {
            destroyMount();
            return;
        }

        float mountYaw = mountedEntity.getLocation().getYaw();
        EulerAngle mountYawEulerAngle = new EulerAngle(0,(Math.toRadians(mountYaw)),0);

        getMountModel().setHeadPose(mountYawEulerAngle);
    }

    @Override
    public ArmorStand getMountModel()
    {
        return mountModel;
    }

    @Override
    public List<Entity> getMountStructure()
    {
        return mountStructure;
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }
}
