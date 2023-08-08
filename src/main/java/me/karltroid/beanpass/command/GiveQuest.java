package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.npcs.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveQuest implements CommandExecutor
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

        if (args.length == 0)
        {
            if (senderPlayer != null) BeanPass.sendMessage(senderPlayer, "Command Usage: /givequest <npc typename> <player>");
            else BeanPass.getInstance().getLogger().warning("Error server running command /givequest.");
            return false;
        }

        Player affectedPlayer = senderPlayer;

        if (args.length > 1)
        {
            Player requestedPlayer = Bukkit.getPlayer(args[1]);
            if (requestedPlayer == null)
            {
                BeanPass.sendMessage(senderPlayer, "Player \"" + args[1] + "\" does not exist or is not online.");
                return false;
            }

            affectedPlayer = requestedPlayer;
        }

        NPC npc = BeanPass.getInstance().getNpcManager().getNPCByTypeName(args[0]);
        if (npc == null)
        {
            BeanPass.sendMessage(senderPlayer, "This NPC type does not exist");
            return false;
        }
        if (affectedPlayer == null)
        {
            BeanPass.getInstance().getLogger().warning("The player receiving the quest is null");
            return false;
        }

        npc.Interact(affectedPlayer);
        return true;
    }
}
