package me.karltroid.beanpass.command;

import com.earth2me.essentials.User;
import me.karltroid.beanpass.BeanPass;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHome implements CommandExecutor
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

        if (!senderPlayer.hasPermission("beanpass.user"))
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        if (args.length == 0)
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "Usage: /sethome <home name>");
            return false;
        }

        User essentialsUser = BeanPass.getInstance().getEssentials().getUser(senderPlayer);
        int playerMaxHomes = BeanPass.getInstance().getPlayerData(senderPlayer.getUniqueId()).getMaxHomes();
        int playerHomesAmount = essentialsUser.getHomes().size();
        if (playerHomesAmount >= playerMaxHomes)
        {
            if (playerMaxHomes == 0) BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You don't have any available sethomes, level up in /beanpass to earn more!");
            else BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You have used all " + playerHomesAmount + "/" + playerMaxHomes + " of your available sethomes, delete one or level up in /beanpass to earn more!");
            return false;
        }

        String homeName = args[0];
        essentialsUser.setHome(homeName, senderPlayer.getLocation());
        BeanPass.sendMessage(senderPlayer, "Home set! " + ChatColor.GRAY + "Visit it with: " + ChatColor.ITALIC + "/home " + homeName);
        return true;
    }
}
