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
        Player senderPlayer = (Player) sender;

        if (!senderPlayer.hasPermission("beanpass.admin"))
        {
            BeanPass.sendMessage(senderPlayer, ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        if (args.length == 0)
        {
            BeanPass.sendMessage(senderPlayer, "Command Usage: /givequest <npc typename> <player>");
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
        npc.PromptQuestDifficulty(affectedPlayer);

        return true;
    }
}
