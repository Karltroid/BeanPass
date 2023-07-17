package me.karltroid.beanpass.npcs;

public class BartenderNPC extends WitchNPC
{
    public BartenderNPC()
    {
        super();

        this.greetings = new String[]{
                "Welcome to the Nautilus Tavern!"
        };
        this.farewells = new String[]{
                "Be careful, don't drink too much.",
                "See yah, let's have a drink sometime!"
        };
        this.questAsks = new String[]{
                "Business is booming, do you think you could help me get some more drinks?"
        };
        this.differentQuestAsksP1 = new String[]{
                "You weren't drinking the product were you? I told you "
        };
        this.differentQuestAsksP2 = new String[]{
                ". If that drink is too hard to brew I can have you brew a different one if you want?"
        };
    }
}
