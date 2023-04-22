package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.SeasonPlayer;
import me.karltroid.beanpass.gui.BeanPassGUI;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OpenBeanPassGUI implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        if (args.length > 0)
        {
            sender.sendMessage("This beanpass subcommand does not exist.");
            return true;
        }

        Player senderPlayer;
        if (sender instanceof Player) senderPlayer = (Player) sender;
        else
        {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (!senderPlayer.hasPermission("BeanPass.user"))
        {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        BeanPass.main.beanPassGUI.open(senderPlayer);

        return false;
    }
}
