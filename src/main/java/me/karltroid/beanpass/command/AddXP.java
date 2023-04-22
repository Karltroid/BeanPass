package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.SeasonPlayer;
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
        if (!senderPlayer.hasPermission("BeanPass.admin"))
        {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 3)
        {
            sender.sendMessage("Usage: /beanpass addxp <player> <xp>");
            return false;
        }

        if (args[0].equalsIgnoreCase("addxp"))
        {
            OfflinePlayer targetPlayer = BeanPass.main.getServer().getOfflinePlayer(args[1]);
            UUID targetPlayerUUID = targetPlayer.getUniqueId();

            if (!BeanPass.main.getActiveSeason().playerData.containsKey(targetPlayerUUID))
            {
                sender.sendMessage("Player not found.");
                return false;
            }

            double xpChange;
            try {
                xpChange = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid xp amount. Please provide an integer.");
                return false;
            }

            SeasonPlayer seasonPlayer = BeanPass.main.getActiveSeason().playerData.get(targetPlayerUUID);
            double oldXP = seasonPlayer.getXp();
            seasonPlayer.addXp(xpChange);

            sender.sendMessage("Modified " + targetPlayer.getName() + "'s xp by " + xpChange + "(" + oldXP + " -> " + seasonPlayer.getXp() + ")");
            return true;
        }

        sender.sendMessage("Unknown subcommand.");
        return false;
    }
}
