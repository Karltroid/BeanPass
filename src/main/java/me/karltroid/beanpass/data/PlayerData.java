package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.quests.Quests;
import me.karltroid.beanpass.quests.Quests.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerData
{
    final String UUID;
    Boolean premium;
    List<Integer> skins;

    // current season data
    double xp = 0;
    List<Quest> quests = new ArrayList<>();

    public PlayerData(String UUID, boolean premium, List<Integer> skins, double xp)
    {
        this.UUID = UUID;
        this.premium = premium;
        this.skins = skins;
        setXp(xp);
    }

    public void setPremiumPass(boolean hasPass) { this.premium = hasPass; }
    public void giveSkin(int skinID) { if (this.skins.contains(skinID)) return; this.skins.add(skinID); }
    public void removeSkin(int skinID) { this.skins.removeIf( hat -> hat.equals(skinID)); }
    public boolean hasSkin(int skinID) { return skins.contains(skinID); }
    public int getLevel() // calculate and return the players level based on their xp and each levels required xp
    {
        double playerXP = getXp();
        int playerLevel = 0;

        for (Map.Entry<Integer, Level> entry : BeanPass.getInstance().getSeason().levels.entrySet())
        {
            Level level = entry.getValue();
            playerXP -= level.xpRequired;
            if (playerXP >= 0)
            {
                playerLevel++;
                continue;
            }

            break;
        }

        return playerLevel;
    }
    public double getXp() { return this.xp; }
    public void setXp(double xp) { this.xp = xp; }
    public void addXp(double xp) { setXp(this.xp + xp); }
    public String getUUID() { return UUID; }

    public Quest giveQuest(Quest quest)
    {
        if (quest == null) quest = Quests.getRandomQuestType(UUID);

        quests.add(quest);
        return quest;
    }

    public List<Quest> getQuests() { return quests; }
}
