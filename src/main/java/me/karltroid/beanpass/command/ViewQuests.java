package me.karltroid.beanpass.command;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.Quests;
import me.karltroid.beanpass.enums.ServerGamemode;
import me.karltroid.beanpass.gui.BeanPassGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ViewQuests implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args)
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

        for (Quests.Quest quest : BeanPass.main.getActiveSeason().playerData.get(senderPlayer.getUniqueId()).getQuests(BeanPass.main.getServerGamemode()))
        {
            if (quest instanceof Quests.MiningQuest) senderPlayer.sendMessage(((Quests.MiningQuest)quest).getGoalDescription());
            else if (quest instanceof Quests.KillingQuest) senderPlayer.sendMessage(((Quests.KillingQuest)quest).getGoalDescription());
            else if (quest instanceof Quests.ExplorationQuest) senderPlayer.sendMessage(((Quests.ExplorationQuest)quest).getGoalDescription());
        }

        return true;
    }
}
