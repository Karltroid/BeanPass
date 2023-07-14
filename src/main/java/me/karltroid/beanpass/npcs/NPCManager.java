package me.karltroid.beanpass.npcs;

import java.util.HashMap;
import java.util.Map;

public class NPCManager
{
    HashMap<String, NPC> npcs = new HashMap<>();

    public NPCManager()
    {
        // type name, npc object
        npcs.put("commander", new CommanderNPC("Commander"));
        npcs.put("lumberjack", new LumberjackNPC("Lumberjack"));
        npcs.put("miner", new MinerNPC("Miner"));
        npcs.put("butcher", new ButcherNPC("Butcher"));
        npcs.put("fisher", new FisherNPC("Fisher"));
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
