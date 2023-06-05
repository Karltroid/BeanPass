package me.karltroid.beanpass.data;

import org.bukkit.Material;

import java.util.HashMap;

public final class Skins
{
    public static HashMap<String, Skin> database = new HashMap<String, Skin>() {{
        put("witch_hat", new Skin("witch_hat", 116, Material.CARVED_PUMPKIN));
    }};

    public static class Skin
    {
        final String NAME;
        final int CUSTOM_MODEL_DATA;
        final Material SKIN_APPLICANT;

        public Skin(String name, int customModelData, Material skinApplicant)
        {
            NAME = name;
            CUSTOM_MODEL_DATA = customModelData;
            SKIN_APPLICANT = skinApplicant;
        }

        public int getCUSTOM_MODEL_DATA() {
            return CUSTOM_MODEL_DATA;
        }

        public Material getSKIN_APPLICANT() {
            return SKIN_APPLICANT;
        }
    }

    public Skin getSkin(int id) { return database.get(id); }
}
