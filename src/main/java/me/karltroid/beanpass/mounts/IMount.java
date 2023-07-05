package me.karltroid.beanpass.mounts;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.List;

public interface IMount
{
    void createMount(Mount mount);
    void destroyMount();
    default void setMount(Mount mount)
    {
        ItemStack customModel = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta customModelMeta = customModel.getItemMeta();
        customModelMeta.setCustomModelData(mount.getId());
        customModel.setItemMeta(customModelMeta);
        getMountModel().getEquipment().setHelmet(customModel);
    }
    default void updateMountModelRotation()
    {
        if (getMountModel() == null)
        {
            destroyMount();
            return;
        }

        float playerYaw = getPlayer().getEyeLocation().getYaw();
        EulerAngle playerYawEulerAngle = new EulerAngle(0,(Math.toRadians(playerYaw)),0);

        getMountModel().setHeadPose(playerYawEulerAngle);
    }

    List<Entity> getMountStructure();
    public ArmorStand getMountModel();
    Player getPlayer();
}
