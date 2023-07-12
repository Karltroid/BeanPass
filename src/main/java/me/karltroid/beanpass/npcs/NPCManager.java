package me.karltroid.beanpass.npcs;

import java.util.HashMap;
import java.util.Map;

public class NPCManager
{
    HashMap<String, NPC> npcs = new HashMap<>();

    public NPCManager()
    {
        // type name, npc object
        npcs.put("commander", new CommanderNPC());
        npcs.put("lumberjack", new LumberjackNPC());
        npcs.put("miner", new MinerNPC());
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
