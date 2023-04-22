package me.karltroid.beanpass.data;

import java.util.HashMap;
import java.util.UUID;

public final class Seasons
{
    public HashMap<Integer, Season> database = new HashMap<>();

    public Seasons()
    {
        database.put(1, new Season("A New Beginning", new HashMap<Integer, Level>(){{
            put(0, new Level(0, null, null));
            put(1, new Level(100, "eco give <player> 50", null));
        }}));
    }

    public class Season
    {
        String title;
        HashMap<Integer, Level> levels;
        // add background/theme vars later
        public HashMap<UUID, SeasonPlayer> playerData = new HashMap<>();

        Season(String title, HashMap<Integer, Level> levels)
        {
            this.title = title;
            this.levels = levels;
        }
    }

    public Season getSeason(int id) { return database.get(id); }
}
