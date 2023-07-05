package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenBeanPassPage implements CommandExecutor
{
    GUIMenu menu;

    public OpenBeanPassPage(GUIMenu menu)
    {
        this.menu = menu;
    }

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

        BeanPassGUI playerBeanPassGUI = BeanPass.getInstance().activeGUIs.get(senderPlayer);
        if (playerBeanPassGUI != null) playerBeanPassGUI.loadMenu(menu);
        else new BeanPassGUI(senderPlayer, menu);

        return true;
    }
}
