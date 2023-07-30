package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.TextElement;
import me.karltroid.beanpass.other.Utils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyReward implements Reward
{
    private double amount;

    public MoneyReward(double amount)
    {
        this.amount = amount;
    }

    @Override
    public void giveReward(UUID uuid)
    {
        // Code to give the player money
        // Example: EconomyAPI.deposit(player, amount);
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        EconomyResponse response = BeanPass.getInstance().getEconomy().depositPlayer(Bukkit.getOfflinePlayer(uuid), getAmount());
        if (response.transactionSuccess() && player.isOnline())
            BeanPass.sendMessage(player,  ChatColor.GREEN + "$" + Utils.formatDouble(getAmount()) + " added to your balance!");
        else
            Bukkit.getLogger().severe("Couldn't give player their money reward.");
    }

    public double getAmount()
    {
        return amount;
    }

    @Override
    public void displayReward(BeanPassGUI beanPassGUI, boolean spherePlacement, double distance, double xAngle, double yAngle, float displayScale)
    {
        beanPassGUI.loadElement(new TextElement(beanPassGUI, spherePlacement, distance, xAngle, yAngle, displayScale, net.md_5.bungee.api.ChatColor.GREEN + "$" + Utils.formatDouble(getAmount())), beanPassGUI.allLevelRewardElements);
    }
}
