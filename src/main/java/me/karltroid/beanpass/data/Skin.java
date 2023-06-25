package me.karltroid.beanpass.data;

import org.bukkit.Material;

public class Skin
{
    String name;
    int id;
    Material skinApplicant;

    public Skin(String name, int id, Material skinApplicant)
    {
        this.name = name;
        this.id = id;
        this.skinApplicant = skinApplicant;
    }

    public String getName() { return name; }

    public int getId() {
        return id;
    }

    public Material getSkinApplicant() {
        return skinApplicant;
    }
}
