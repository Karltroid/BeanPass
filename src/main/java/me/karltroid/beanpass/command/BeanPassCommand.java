package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BeanPassCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("This command can only be executed by a player.");
                return false;
            }

            Player senderPlayer = (Player) sender;

            if (!senderPlayer.hasPermission("beanpass.user"))
            {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }

            if (BeanPass.getInstance().activeGUIs.containsKey(senderPlayer))
            {
                senderPlayer.sendMessage("You already have a BeanPass menu open!");
                return false;
            }

            new BeanPassGUI(senderPlayer, GUIMenu.BeanPass);
            return true;
        }
        else if (args[0].equalsIgnoreCase("addxp"))
        {
            if (!sender.hasPermission("beanpass.admin")) {
                sender.sendMessage("You do not have permission to use this command.");
                return false;
            }

            if (args.length < 3) {
                sender.sendMessage("Usage: /beanpass addxp <player> <amount>");
                return false;
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
                xpChange = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid amount specified. Please provide a valid number.");
                return false;
            }

            PlayerData playerData = BeanPass.getInstance().getPlayerData(targetPlayerUUID);
            double oldXP = playerData.getXp();
            playerData.addXp(xpChange);

            sender.sendMessage("Modified " + targetPlayer.getName() + "'s xp by " + xpChange + " (" + oldXP + " -> " + playerData.getXp() + ")");
            return true;
        }

        return false;
    }
}
