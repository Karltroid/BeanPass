package me.karltroid.beanpass.npcs;

import me.karltroid.beanpass.BeanPass;

import java.util.HashMap;
import java.util.Map;

public class NPCManager
{
    HashMap<String, NPC> npcs = new HashMap<>();
    public int questsPerNPCPerDay;

    public NPCManager()
    {
        questsPerNPCPerDay = BeanPass.getInstance().getGeneralConfig().getInt("QuestsPerNPCPerDay");

        // type name, npc object
        npcs.put("commander", new CommanderNPC());
        npcs.put("lumberjack", new LumberjackNPC());
        npcs.put("miner", new MinerNPC());
        npcs.put("butcher", new ButcherNPC());
        npcs.put("fisher", new FisherNPC());
        npcs.put("witch", new WitchNPC());
        npcs.put("bartender", new BartenderNPC());
        npcs.put("baker", new BakerNPC());
        npcs.put("artist", new ArtistNPC());
        npcs.put("blacksmith", new BlacksmithNPC());
        npcs.put("farmer", new FarmerNPC());
        npcs.put("builder", new BuilderNPC());
        npcs.put("animalbreeder", new AnimalBreederNPC());
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
