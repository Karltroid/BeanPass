package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class NPCManager
{
    HashMap<String, NPC> npcs = new HashMap<>();

    public NPCManager()
    {
        // type name, npc object
        npcs.put("commander", new CommanderNPC("Commander", "CommanderKillingQuests"));
        npcs.put("lumberjack", new LumberjackNPC("Lumberjack", "LumberjackMiningQuests"));
        npcs.put("miner", new MinerNPC("Miner", "MinerMiningQuests"));
        npcs.put("butcher", new MinerNPC("Butcher", "ButcherKillingQuests"));
    }

    public NPC getNPCByTypeName(String name)
    {
        return npcs.get(name.toLowerCase());
    }

    public String getNPCTypeNameFromObject(NPC npc)
    {
        String npcName = null;

        for (Map.Entry<String, NPC> entry : npcs.entrySet())
        {
            if (entry.getValue().equals(npc))
            {
                npcName = entry.getKey();
                break;
            }
        }

        return npcName;
    }
}
