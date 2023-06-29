package me.karltroid.beanpass.mounts;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Mount
{
    String name;
    int id;
    EntityType mountApplicant;
    int height;

    public Mount(String name, int id, EntityType mountApplicant, int height)
    {
        this.name = name;
        this.id = id;
        this.mountApplicant = mountApplicant;
        this.height = height;
    }

    public String getName() { return name; }
    public int getId() { return id; }
    public EntityType getMountApplicant() { return mountApplicant; }
    public int getHeight() { return height; }
}
