package me.karltroid.beanpass.other;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeadPetRevival implements Listener
{
    private final NamespacedKey petDataKey = new NamespacedKey(BeanPass.getInstance(), "petData");

    @EventHandler
    public void onPetDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player owner = null;

        if (entity instanceof Tameable && (((Tameable) entity).isTamed()))
        {
            Tameable pet = (Tameable) entity;
            owner = (Player) pet.getOwner();

            // Serialize pet data (For simplicity, we use a single String here)
            //String petData = serializePetData(pet);

            // Create the bone item with pet data
            //ItemStack boneItem = createBoneItem(petData);

            // Drop the bone item at the pet's location
            //pet.getWorld().dropItemNaturally(pet.getLocation(), boneItem);
        }
        else if (entity.getCustomName() != null)
        {

        }
    }

    private String serializePetData(Wolf pet) {
        // Your code to convert pet data into a serialized format (e.g., JSON or any other custom format)
        return "SomeSerializedData";
    }
}
