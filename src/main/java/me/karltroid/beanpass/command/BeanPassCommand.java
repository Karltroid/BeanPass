package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BeanPassCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player senderPlayer = null;
        if (sender instanceof Player) senderPlayer = (Player) sender;

        if (!senderPlayer.hasPermission("beanpass.user"))
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        if (args.length == 0)
        {
            BeanPassGUI playerBeanPassGUI = BeanPass.getInstance().activeGUIs.get(senderPlayer);
            if (playerBeanPassGUI != null)
            {
                if (!playerBeanPassGUI.getCurrentGUIMenu().equals(GUIMenu.BeanPass)) playerBeanPassGUI.loadMenu(GUIMenu.BeanPass);
                else playerBeanPassGUI.closeEntireGUI();
                return true;
            }

            new BeanPassGUI(senderPlayer, GUIMenu.BeanPass);
            return true;
        }
        else if (args[0].equalsIgnoreCase("close"))
        {
            BeanPassGUI playerBeanPassGUI = BeanPass.getInstance().activeGUIs.get(senderPlayer);
            if (playerBeanPassGUI == null)
            {
                BeanPass.sendMessage(senderPlayer, "You can't close what is not open.");
                return false;
            }

            playerBeanPassGUI.closeEntireGUI();
            return true;
        }
        else if (args[0].equalsIgnoreCase("addxp"))
        {
            if (!sender.hasPermission("beanpass.owner")) {
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
        else if (args[0].equalsIgnoreCase("addxp"))
        {
            if (!sender.hasPermission("beanpass.owner")) {
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
        else if (args[0].equalsIgnoreCase("bedrock"))
        {
            if (!sender.hasPermission("beanpass.owner")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            Player requestedPlayer;

            if (args.length == 1) requestedPlayer = senderPlayer;
            else requestedPlayer = Bukkit.getPlayer(args[1]);

            if (requestedPlayer == null)
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "This player is not online or does not exist.");
                return false;
            }

            PlayerData playerData = BeanPass.getInstance().getPlayerData(requestedPlayer.getUniqueId());
            playerData.toggleBedrockAccount();
            BeanPass.sendMessage(senderPlayer, requestedPlayer.getName() + " bedrockAccount: " + playerData.isBedrockAccount());
            return true;
        }
        else if (args[0].equalsIgnoreCase("premium"))
        {
            if (!sender.hasPermission("beanpass.owner")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            Player requestedPlayer = Bukkit.getPlayer(args[1]);
            int passesPurchased = 1;
            if (args.length >= 3) passesPurchased = Integer.parseInt(args[2]);

            PlayerData playerData = BeanPass.getInstance().getPlayerData(requestedPlayer.getUniqueId());
            if (!playerData.isPremium())
            {
                playerData.givePremiumPass(requestedPlayer, true);
                passesPurchased--;
            }

            if (passesPurchased > 0)
            {
                // give extra bean passes to online players first
                for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                {
                    if (passesPurchased == 0) break;
                    if (onlinePlayer.getUniqueId() == requestedPlayer.getUniqueId()) continue;
                    PlayerData onlinePlayerData = BeanPass.getInstance().getPlayerData(onlinePlayer.getUniqueId());
                    if (onlinePlayerData.isPremium()) continue;

                    onlinePlayerData.givePremiumPass(requestedPlayer, true);
                    passesPurchased--;
                }

                // give extra bean passes to most recent offline players next
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
                {
                    UUID offlinePlayerUUID = offlinePlayer.getUniqueId();
                    if (passesPurchased == 0) break;
                    if (offlinePlayerUUID == requestedPlayer.getUniqueId() || offlinePlayer.isOnline()) continue;
                    PlayerData offlinePlayerData = BeanPass.getInstance().getPlayerData(offlinePlayerUUID);
                    if (offlinePlayerData == null || offlinePlayerData.isPremium()) continue;

                    offlinePlayerData.givePremiumPass(requestedPlayer, true);
                    BeanPass.getInstance().getDataManager().savePlayerData(offlinePlayerUUID);
                    BeanPass.getInstance().unloadPlayerData(offlinePlayerUUID);
                    passesPurchased--;
                }
            }

            return true;
        }
        else if (args[0].equalsIgnoreCase("remove"))
        {
            if (!sender.hasPermission("beanpass.owner"))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            Player requestedPlayer = Bukkit.getPlayer(args[1]);

            PlayerData playerData = BeanPass.getInstance().getPlayerData(requestedPlayer.getUniqueId());
            if (!playerData.isPremium())
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "This player does not have premium already.");
                return false;
            }

            playerData.removePremiumPass(requestedPlayer, true);

            return true;
        }

        return false;
    }

}
