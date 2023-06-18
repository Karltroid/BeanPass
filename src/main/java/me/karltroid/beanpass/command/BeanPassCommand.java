package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BeanPassCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        Player senderPlayer = (Player) sender;

        if (args.length == 0)
        {
            if (!senderPlayer.hasPermission("beanpass.user"))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            if (BeanPass.getInstance().activeGUIs.containsKey(senderPlayer))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You already have a BeanPass menu open!");
                return false;
            }

            new BeanPassGUI(senderPlayer, GUIMenu.BeanPass);
            return true;
        }
        else if (args[0].equalsIgnoreCase("addxp"))
        {
            if (!sender.hasPermission("beanpass.admin")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            if (args.length < 3) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Usage: /beanpass addxp <player> <amount>");
                return false;
            }

            OfflinePlayer targetPlayer = BeanPass.getInstance().getServer().getOfflinePlayer(args[1]);
            UUID targetPlayerUUID = targetPlayer.getUniqueId();

            if (!BeanPass.getInstance().playerDataExists(targetPlayerUUID))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Player data for this season does not exist.");
                return false;
            }

            double xpChange;
            try {
                xpChange = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Invalid amount specified. Please provide a valid number.");
                return false;
            }

            PlayerData playerData = BeanPass.getInstance().getPlayerData(targetPlayerUUID);
            double oldXP = playerData.getXp();
            playerData.addXp(xpChange);

            BeanPass.sendMessage(senderPlayer, ((xpChange > 0) ? "Increased " : "Decreased ") + targetPlayer.getName() + "'s xp by " + Math.abs(xpChange) + " (" + oldXP + " -> " + playerData.getXp() + ")");
            return true;
        }

        return false;
    }
}
