package me.karltroid.beanpass.Rewards;

import me.karltroid.beanpass.BeanPass;
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
            BeanPass.sendMessage(player,  "Level Reward:" + ChatColor.GREEN + " " + ChatColor.BOLD + "+$" + getAmount() + " added to your account!");
        else
            Bukkit.getLogger().severe("Couldn't give player their money reward.");
    }

    public double getAmount()
    {
        return amount;
    }
}
