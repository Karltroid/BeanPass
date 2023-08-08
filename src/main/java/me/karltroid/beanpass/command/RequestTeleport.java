package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RequestTeleport implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player senderPlayer = null;
        if (sender instanceof Player) senderPlayer = (Player) sender;

        if (senderPlayer != null && !senderPlayer.hasPermission("beanpass.owner"))
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        if (args.length < 5)
        {
            if (senderPlayer != null) BeanPass.sendMessage(senderPlayer, "Command Usage: /requestteleport <player> <world> <x> <y> <z> <yes_msg> | <no_msg>");
            else BeanPass.getInstance().getLogger().warning("Error server running command /yesnoteleport.");
            return false;
        }

        Player requestedPlayer = Bukkit.getPlayer(args[0]);
        if (requestedPlayer == null)
        {
            BeanPass.sendMessage(senderPlayer, "Player \"" + args[0] + "\" does not exist or is not online.");
            return false;
        }

        World world = Bukkit.getWorld(args[1]);
        double x = Double.parseDouble(args[2]);
        double y = Double.parseDouble(args[3]);
        double z = Double.parseDouble(args[4]);
        Location teleportLocation = new Location(world, x, y, z, requestedPlayer.getLocation().getYaw(), requestedPlayer.getLocation().getPitch());

        PlayerData playerData = BeanPass.getInstance().getPlayerData(requestedPlayer.getUniqueId());

        playerData.responseFuture = new CompletableFuture<>();
        BeanPassGUI beanPassGUI = new BeanPassGUI(requestedPlayer, GUIMenu.YesNoQuestion);

        StringBuilder yesResponse = new StringBuilder();

        int noMsgStartIndex = -1;
        for (int yesMsgIndex = 5; yesMsgIndex < args.length; yesMsgIndex++)
        {
            if (args[yesMsgIndex].equals("|"))
            {
                noMsgStartIndex = yesMsgIndex + 1;
                break;
            }
            yesResponse.append(args[yesMsgIndex]).append(" ");
        }

        StringBuilder noResponse = new StringBuilder();
        for (int noMsgIndex = noMsgStartIndex; noMsgIndex < args.length; noMsgIndex++)
        {
            noResponse.append(args[noMsgIndex]).append(" ");
        }

        playerData.responseFuture.thenAccept(accepted -> {
            if (accepted)
            {
                requestedPlayer.teleport(teleportLocation);
                requestedPlayer.sendMessage(ChatColor.BOLD + String.valueOf(yesResponse));
            }
            else
            {
                if (String.valueOf(noResponse).startsWith("/kick"))
                {
                    String kickReason = String.valueOf(noResponse).replace("/kick ", "");
                    beanPassGUI.closeEntireGUI();
                    requestedPlayer.kickPlayer(kickReason);
                }
                else
                {
                    requestedPlayer.sendMessage(ChatColor.BOLD + String.valueOf(noResponse));
                }
            }
        });
        return true;
    }
}
