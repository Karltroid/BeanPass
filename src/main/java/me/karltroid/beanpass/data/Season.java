package me.karltroid.beanpass.data;

import java.util.HashMap;
import java.util.UUID;

public class Season
{
    int id;
    HashMap<Integer, Level> levels;

    public Season(int id,  HashMap<Integer, Level> levels)
    {
        this.id = id;
        this.levels = levels;
    }

    public int getId()
    {
       return id;
    }

    public HashMap<Integer, Level> getLevels() {
        return levels;
    }
    public Level getLevel(int level) { return levels.get(level); }
}
