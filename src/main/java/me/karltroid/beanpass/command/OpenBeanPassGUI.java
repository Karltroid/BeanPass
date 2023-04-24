package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpenBeanPassGUI implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
    {
        if (args.length > 0) return false;

        Player senderPlayer;
        if (sender instanceof Player) senderPlayer = (Player) sender;
        else
        {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        if (!senderPlayer.hasPermission("beanpass.user"))
        {
            sender.sendMessage("You do not have permission to use this command.");
            return false;
        }

        new BeanPassGUI(senderPlayer);

        return true;
    }
}
