package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.enums.ServerGamemode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.karltroid.beanpass.data.Quests.Quest;

public class SeasonPlayer
{
    double xp = 0;
    Boolean premium = false;
    List<Integer> hats = new ArrayList<>();
    List<Quest> survivalQuests = new ArrayList<>();
    List<Quest> skyblockQuests = new ArrayList<>();
    List<Quest> hardcoreQuests = new ArrayList<>();


    public SeasonPlayer(double xp, Boolean premiumPass)
    {
        setXp(xp);
        this.premium = premiumPass;
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
    public void setPremiumPass(boolean hasPass) { this.premium = hasPass; }
    public void giveHat(int hatID) { if (this.hats.contains(hatID)) return; this.hats.add(hatID); }
    public void removeHat(int hatID) { this.hats.removeIf( hat -> hat.equals(hatID)); }
    public boolean hasHat(int id) { return hats.contains(id); }
    public void giveQuest(Quest quest)
    {
        switch (quest.SERVER_GAMEMODE)
        {
            case SURVIVAL:
                survivalQuests.add(quest);
                break;
            case SKYBLOCK:
                skyblockQuests.add(quest);
                break;
            case HARDCORE:
                hardcoreQuests.add(quest);
                break;
            default:
                break;
        }
    }

    public List<Quest> getQuests(ServerGamemode serverGamemode)
    {
        switch (serverGamemode)
        {
            case SURVIVAL:
                return survivalQuests;
            case SKYBLOCK:
                return skyblockQuests;
            case HARDCORE:
                return hardcoreQuests;
            case ALL:
                List<Quest> allQuests = new ArrayList<>();
                allQuests.addAll(survivalQuests);
                allQuests.addAll(skyblockQuests);
                allQuests.addAll(hardcoreQuests);
                return allQuests;
            default:
                break;
        }

        return null;
    }
}

