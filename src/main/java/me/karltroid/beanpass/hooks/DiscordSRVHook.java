package me.karltroid.beanpass.hooks;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.karltroid.beanpass.BeanPass;
import org.bukkit.ChatColor;

public class DiscordSRVHook
{
    private static final DiscordSRVHook instance = new DiscordSRVHook();
    private TextChannel discordBroadcastTextChannel;
    private DiscordSRVHook() {}

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event)
    {
        discordBroadcastTextChannel = DiscordSRV.getPlugin().getJda().getTextChannelById(BeanPass.getInstance().getGeneralConfig().getString("DiscordBroadcastTextChannelID", "1000319730630008833"));
        BeanPass.getInstance().getLogger().info("Discord SRV Ready For BeanPass");
    }


    public static void sendMessage(String message)
    {
        if (instance.discordBroadcastTextChannel != null)
        {
            instance.discordBroadcastTextChannel.sendMessage("> ## " + ChatColor.stripColor(message)).complete();
        }

    }

    public static void register()
    {
        DiscordSRV.api.subscribe(instance);
    }

    public static void unregister()
    {
        DiscordSRV.api.unsubscribe(instance);
    }
}
