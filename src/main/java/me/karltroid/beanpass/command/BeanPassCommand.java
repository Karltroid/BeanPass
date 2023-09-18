package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.data.PlayerDataManager;
import me.karltroid.beanpass.data.Skin;
import me.karltroid.beanpass.data.SkinManager;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIManager;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.hooks.DiscordSRVHook;
import me.karltroid.beanpass.mounts.Mount;
import me.karltroid.beanpass.mounts.MountManager;
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

        if (args.length == 0)
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.user"))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            BeanPassGUI playerBeanPassGUI = GUIManager.getGUI(senderPlayer);
            if (playerBeanPassGUI != null)
            {
                if (!playerBeanPassGUI.getCurrentGUIMenu().equals(GUIMenu.BeanPass)) playerBeanPassGUI.loadMenu(GUIMenu.BeanPass);
                else GUIManager.closeGUI(senderPlayer);
                return true;
            }

            GUIManager.openGUI(senderPlayer, GUIMenu.BeanPass);
            return true;
        }
        else if (args[0].equalsIgnoreCase("close"))
        {
            BeanPassGUI playerBeanPassGUI = GUIManager.getGUI(senderPlayer);
            if (playerBeanPassGUI == null)
            {
                BeanPass.sendMessage(senderPlayer, "You can't close what is not open.");
                return false;
            }

            GUIManager.closeGUI(senderPlayer);
            return true;
        }
        else if (args[0].equalsIgnoreCase("addxp"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            if (args.length < 3) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Usage: /beanpass addxp <player> <amount>");
                return false;
            }

            OfflinePlayer targetPlayer = BeanPass.getInstance().getServer().getOfflinePlayer(args[1]);
            UUID targetPlayerUUID = targetPlayer.getUniqueId();
            PlayerData playerData = PlayerDataManager.getPlayerData(targetPlayerUUID);

            if (playerData == null)
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Player data failed to load.");
                return false;
            }

            double xpChange;
            try {
                xpChange = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Invalid amount specified. Please provide a valid number.");
                return false;
            }

            double oldXP = playerData.getXp();

            playerData.addXp(xpChange);
            BeanPass.sendMessage(senderPlayer, ((xpChange > 0) ? "Increased " : "Decreased ") + targetPlayer.getName() + "'s xp by " + Math.abs(xpChange) + " (" + oldXP + " -> " + playerData.getXp() + ")");
            if (!playerData.getPlayer().isOnline()) PlayerDataManager.unloadPlayerData(targetPlayerUUID);
            return true;
        }
        else if (args[0].equalsIgnoreCase("addxp"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            if (args.length < 3) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Usage: /beanpass addxp <player> <amount>");
                return false;
            }

            OfflinePlayer targetPlayer = BeanPass.getInstance().getServer().getOfflinePlayer(args[1]);
            UUID targetPlayerUUID = targetPlayer.getUniqueId();
            PlayerData playerData = PlayerDataManager.getPlayerData(targetPlayerUUID);

            if (playerData == null)
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

            double oldXP = playerData.getXp();

            playerData.addXp(xpChange);
            BeanPass.sendMessage(senderPlayer, ((xpChange > 0) ? "Increased " : "Decreased ") + targetPlayer.getName() + "'s xp by " + Math.abs(xpChange) + " (" + oldXP + " -> " + playerData.getXp() + ")");
            return true;
        }
        else if (args[0].equalsIgnoreCase("givecosmetic"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner"))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            if (args.length < 3)
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Usage: /beanpass givecosmetic <player> <cosmetic id>");
                return false;
            }

            OfflinePlayer targetPlayer = BeanPass.getInstance().getServer().getOfflinePlayer(args[1]);
            UUID targetPlayerUUID = targetPlayer.getUniqueId();
            PlayerData targetPlayerData = PlayerDataManager.getPlayerData(targetPlayerUUID);

            if (targetPlayerData == null)
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Player data for this season does not exist.");
                return false;
            }

            Mount mount = MountManager.getMountById(Integer.parseInt(args[2]));
            if(mount != null)
            {
                targetPlayerData.giveMount(mount, true);
                BeanPass.sendMessage(senderPlayer, targetPlayer.getName() + " has received a " + mount.getMountApplicant() + " mount #" + mount.getId() + ", " + mount.getName());
            }
            else
            {
                Skin skin = SkinManager.getSkinById(Integer.parseInt(args[2]));
                if (skin != null)
                {
                    targetPlayerData.giveSkin(skin, true);
                    BeanPass.sendMessage(senderPlayer, targetPlayer + " has received a " + skin.getSkinApplicant() + " skin #" + skin.getId() + ", " + skin.getName());
                }
            }

            if (!targetPlayerData.getPlayer().isOnline()) PlayerDataManager.unloadPlayerData(targetPlayerUUID);

            return true;
        }
        else if (args[0].equalsIgnoreCase("bedrock"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner")) {
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

            PlayerData playerData = PlayerDataManager.getPlayerData(requestedPlayer.getUniqueId());
            playerData.toggleBedrockAccount();
            BeanPass.sendMessage(senderPlayer, requestedPlayer.getName() + " bedrockAccount: " + playerData.isBedrockAccount());
            return true;
        }
        else if (args[0].equalsIgnoreCase("premium"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            OfflinePlayer requestedPlayer = Bukkit.getPlayer(args[1]);

            if (requestedPlayer == null)
            {
                BeanPass.getInstance().getLogger().severe(args[1] + " tried to purchase x" + args[2] + " beanpass but that user was not found.");
                DiscordSRVHook.sendMessage(args[1] + " tried to purchase x" + args[2] + " beanpass but that user was not found.");
                return false;
            }

            int passesPurchased = 1;
            if (args.length >= 3) passesPurchased = Integer.parseInt(args[2]);

            PlayerData playerData = PlayerDataManager.getPlayerData(requestedPlayer.getUniqueId());
            if (playerData == null)
            {
                BeanPass.getInstance().getLogger().severe(args[1] + " tried to purchase x" + args[2] + " beanpass there was a problem getting their player data.");
                DiscordSRVHook.sendMessage(args[1] + " tried to purchase x" + args[2] + " beanpass there was a problem getting their player data.");
                return false;
            }
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
                    PlayerData onlinePlayerData = PlayerDataManager.getPlayerData(onlinePlayer.getUniqueId());
                    if (onlinePlayerData == null || onlinePlayerData.isPremium()) continue;

                    onlinePlayerData.givePremiumPass(requestedPlayer, true);
                    passesPurchased--;
                }

                // give extra bean passes to most recent offline players next
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
                {
                    UUID offlinePlayerUUID = offlinePlayer.getUniqueId();
                    if (passesPurchased == 0) break;
                    if (offlinePlayerUUID == requestedPlayer.getUniqueId() || offlinePlayer.isOnline()) continue;
                    PlayerData offlinePlayerData = PlayerDataManager.getPlayerData(offlinePlayerUUID);
                    if (offlinePlayerData == null || offlinePlayerData.isPremium()) continue;

                    offlinePlayerData.givePremiumPass(requestedPlayer, true);
                    PlayerDataManager.unloadPlayerData(offlinePlayerUUID);
                    passesPurchased--;
                }
            }

            return true;
        }
        else if (args[0].equalsIgnoreCase("ispremium"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner")) {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            OfflinePlayer requestedPlayer = Bukkit.getPlayer(args[1]);

            if (requestedPlayer == null)
            {
                BeanPass.sendMessage(senderPlayer, "That player does not exist");
                return false;
            }

            PlayerData playerData = PlayerDataManager.getPlayerData(requestedPlayer.getUniqueId());
            if (playerData == null)
            {
                BeanPass.sendMessage(senderPlayer, "There was a problem getting their player data.");
                return false;
            }

            BeanPass.sendMessage(senderPlayer, requestedPlayer.getName() + (playerData.isPremium() ? " is a premium user" : " is not a premium user"));
            return true;
        }
        else if (args[0].equalsIgnoreCase("remove"))
        {
            if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner"))
            {
                BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
                return false;
            }

            Player requestedPlayer = Bukkit.getPlayer(args[1]);

            PlayerData playerData = PlayerDataManager.getPlayerData(requestedPlayer.getUniqueId());
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
