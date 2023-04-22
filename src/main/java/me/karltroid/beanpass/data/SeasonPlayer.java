package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;

import java.util.List;
import java.util.Map;

public class SeasonPlayer
{
    double xp = 0;
    Boolean premiumPass = false;
    List<Integer> hats;


    public SeasonPlayer(int xp, Boolean premiumPass, List<Integer> hats)
    {
        setXp(xp);
        this.premiumPass = premiumPass;
        this.hats = hats;
    }


    public int getLevel() // calculate and return the players level based on their xp and each levels required xp
    {
        int playerLevel = 0;
        for (Map.Entry<Integer, Level> entry : BeanPass.main.getActiveSeason().levels.entrySet())
        {
            int levelNumber = entry.getKey();
            Level level = entry.getValue();
            if (this.xp >= level.xpRequired) playerLevel = levelNumber;
            else break;
        }
        return playerLevel;
    }
    public double getXp() { return this.xp; }
    public void setXp(double xp) { this.xp = xp; }
    public void addXp(double xp) { setXp(this.xp + xp); }
    public void setPremiumPass(boolean hasPass) { this.premiumPass = hasPass; }
    public void giveHat(int hatID) { if (this.hats.contains(hatID)) return; this.hats.add(hatID); }
    public void removeHat(int hatID) { this.hats.removeIf( hat -> hat.equals(hatID)); }
    public boolean hasHat(int id) { return hats.contains(id); }
}

