package me.karltroid.beanpass.command;

import com.earth2me.essentials.User;
import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SetWarp implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Player senderPlayer = (Player) sender;

        if (!senderPlayer.hasPermission("beanpass.user"))
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        if (args.length < 2)
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Usage: /pwarp set <warp name>");
            return false;
        }

        if (!args[0].equals("set")) return false;

        PlayerData playerData = BeanPass.getInstance().getPlayerData(senderPlayer.getUniqueId());
        int playerMaxWarps = playerData.getMaxWarpAmount();
        int playerWarpsAmount = playerData.getWarpAmount();
        if (playerWarpsAmount >= playerMaxWarps)
        {
            if (playerMaxWarps == 0) BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You don't have any available pwarps, level up in /beanpass to earn more!");
            else BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You have used all " + playerWarpsAmount + "/" + playerMaxWarps + " of your available pwarps, delete one or level up in /beanpass to earn more!");
            return false;
        }

        String homeName = args[1];
        Plugin playerWarpsPlugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerWarps");
        if (playerWarpsPlugin == null) return false;

        playerWarpsPlugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "/pwarp set " + homeName + " " + senderPlayer.getName());
        //BeanPass.sendMessage(senderPlayer, "Home set! " + ChatColor.GRAY + "Visit it with: " + ChatColor.ITALIC + "/home " + homeName);
        return true;
    }
}
