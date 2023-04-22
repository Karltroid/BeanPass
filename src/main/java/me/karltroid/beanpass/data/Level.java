package me.karltroid.beanpass.data;

public class Level
{
    int xpRequired;
    String freeCommandReward;
    String premiumCommandReward;

    public Level(int xpRequired, String freeCommandReward, String premiumCommandReward)
    {
        this.xpRequired = xpRequired;
        this.freeCommandReward = freeCommandReward;
        this.premiumCommandReward = premiumCommandReward;
    }
}
