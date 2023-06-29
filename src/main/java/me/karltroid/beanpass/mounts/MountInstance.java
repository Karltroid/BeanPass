package me.karltroid.beanpass.mounts;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import me.karltroid.beanpass.BeanPass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MountInstance
{
    Player mountOwner;
    Entity preMountEntity;
    Mount mount;

    // 0 = baby horse for applying velocity and stepping up blocks
    // 1...x = armor stands for height and displaying custom model at top
    List<Entity> mountStructure = new ArrayList<>();

    public MountInstance(Player mountOwner, Entity preMountEntity, Mount mount)
    {
        //this.preMountEntity = preMountEntity;
        this.mountOwner = mountOwner;
        this.mount = mount;

        Location mountOwnerLocation = mountOwner.getLocation();
        World world = mountOwnerLocation.getWorld();

        Horse mountBase = (Horse) world.spawnEntity(mountOwnerLocation, EntityType.HORSE);
        mountBase.setBaby();
        mountBase.setOwner(mountOwner);
        mountBase.setTamed(true);
        mountStructure.add(mountBase);

        List<ArmorStand> mountHeightAndDisplay = new ArrayList<>();
        for (int i = 0; i < mount.getHeight(); i++)
        {
            ArmorStand armorStand = (ArmorStand)world.spawnEntity(mountOwnerLocation, EntityType.ARMOR_STAND);
            armorStand.setSmall(true);
            // if i == last i, put custom model on head.
            mountHeightAndDisplay.add(armorStand);
        }
        mountStructure.addAll(mountHeightAndDisplay);

        // stack all the mountStructure entities together
        for (int i = 1; i < mountStructure.size(); i++) mountStructure.get(i-1).addPassenger(mountStructure.get(i));
        mountStructure.get(mountStructure.size()-1).addPassenger(mountOwner);
    }

    public void updateMountMovement(float xVel, float zVel)
    {
        float speed = 1.1f;
        Horse mountBase = (Horse) mountStructure.get(0);
        Location playerEyeLocation = mountOwner.getEyeLocation();
        Vector playerDirection = playerEyeLocation.getDirection().normalize();

        // Calculate the direction relative to the player's side movement
        Vector rightDirection = playerDirection.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(-1);
        Vector velocity = playerDirection.clone().multiply(speed*zVel).add(rightDirection.clone().multiply(xVel));
        velocity.setY(mountBase.getVelocity().getY());

        mountBase.setVelocity(velocity);
    }
}
