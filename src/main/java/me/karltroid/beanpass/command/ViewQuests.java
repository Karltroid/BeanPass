package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.quests.Quests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewQuests implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
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

        new BeanPassGUI(senderPlayer, GUIMenu.Quests);
        return true;
    }
}
