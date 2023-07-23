package me.karltroid.beanpass.mounts;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class MinecartMount implements IMount
{
    Player player;
    Minecart mountedEntity;
    ArmorStand mountModel;
    List<Entity> mountStructure = new ArrayList<>();

    public MinecartMount(Player player, Entity mountedEntity, Mount mount)
    {
        this.player = player;
        this.mountedEntity = (Minecart)mountedEntity;
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

        System.out.println(mountedEntity.getVelocity().getX() + " - " + mountedEntity.getLocation().getYaw());
        double mountYaw;
        if (mountedEntity.getVelocity().getX() < 0 && mountedEntity.getLocation().getYaw() == 0.0) mountYaw = Math.toRadians(-mountedEntity.getLocation().getYaw());
        else mountYaw = Math.toRadians(mountedEntity.getLocation().getYaw());

        EulerAngle mountYawEulerAngle = new EulerAngle(0,mountYaw,0);

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
