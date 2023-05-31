package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AddXP implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        Player senderPlayer = (Player) sender;
        if (!senderPlayer.hasPermission("beanpass.admin"))
        {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }

        if (args.length < 3)
        {
            sender.sendMessage("Usage: /beanpass addxp <player> <xp>");
            return true;
        }

        OfflinePlayer targetPlayer = BeanPass.getInstance().getServer().getOfflinePlayer(args[1]);
        UUID targetPlayerUUID = targetPlayer.getUniqueId();

        if (!BeanPass.getInstance().playerDataExists(targetPlayerUUID))
        {
            sender.sendMessage("Player has not played this season. (not found)");
            return false;
        }

        double xpChange;
        try {
            xpChange = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid xp amount. Please provide an integer.");
            return false;
        }

        PlayerData playerData = BeanPass.getInstance().getPlayerData(targetPlayerUUID);
        double oldXP = playerData.getXp();
        playerData.addXp(xpChange);

        sender.sendMessage("Modified " + targetPlayer.getName() + "'s xp by " + xpChange + "(" + oldXP + " -> " + playerData.getXp() + ")");
        return true;
    }
}
