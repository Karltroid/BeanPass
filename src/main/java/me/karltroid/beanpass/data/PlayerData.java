package me.karltroid.beanpass.data;

import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.Button;
import me.karltroid.beanpass.quests.Quests;
import me.karltroid.beanpass.quests.Quests.Quest;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData
{
    final UUID UUID;
    Boolean premium;
    List<Integer> skins;

    // current season data
    double xp = 0;
    List<Quest> quests = new ArrayList<>();

    public PlayerData(UUID UUID, boolean premium, List<Integer> skins, double xp)
    {
        this.UUID = UUID;
        this.premium = premium;
        this.skins = skins;
        setXp(xp);
    }

    public void setPremiumPass(boolean hasPass) { this.premium = hasPass; }
    public void giveSkin(int skinID) { if (this.skins.contains(skinID)) return; this.skins.add(skinID); }
    public void removeSkin(int skinID) { this.skins.removeIf( hat -> hat.equals(skinID)); }
    public boolean hasSkin(int skinID) { return skins.contains(skinID); }
    public int getLevel() // calculate and return the players level based on their xp and each levels required xp
    {
        double playerXP = getXp();

        int playerLevel = 0;
        for (Map.Entry<Integer, Level> entry : BeanPass.getInstance().getSeason().levels.entrySet())
        {
            playerLevel++;
            Level level = entry.getValue();
            playerXP -= level.xpRequired;
            if (playerXP < 0) break;
        }

        return playerLevel;
    }
    public double getXp() { return this.xp; }
    public void setXp(double xp) { this.xp = xp; }
    public void addXp(double xp)
    {
        int beforeLevel = getLevel();

        setXp(this.xp + xp);
        int afterLevel = getLevel();

        Player player = Bukkit.getPlayer(getUUID());
        if (BeanPass.getInstance().activeGUIs.containsKey(player))
        {
            BeanPassGUI beanPassGUI = BeanPass.getInstance().activeGUIs.get(player);
            beanPassGUI.reloadLevelElements();
        }

        if (beforeLevel == afterLevel) return;
        // levelled up!
        leveledUp();
    }

    public double getXpNeededForNextLevel()
    {
        double nextLevelXpRequired = BeanPass.getInstance().getSeason().getLevel(getLevel() + 1).getXpRequired();

        double xpLeftover = getXp();

        for(int level = getLevel(); level > 1; level--)
        {
            xpLeftover -= BeanPass.getInstance().getSeason().getLevel(level).getXpRequired();
        }

        return nextLevelXpRequired - xpLeftover;
    }

    void leveledUp()
    {
        int level = getLevel();

        BeanPass.getInstance().getSeason().getLevel(level).getFreeReward().giveReward(getUUID());

        Player player = Bukkit.getPlayer(getUUID());
        World world = player.getWorld();
        Location location = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Leveled up! " + ChatColor.YELLOW + " " + ChatColor.BOLD + "LVL " + level);
        world.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .trail(true)
                .withColor(Color.LIME)
                .withFade(Color.YELLOW)
                .with(FireworkEffect.Type.STAR)
                .withFlicker()
                .withTrail()
                .withFade(Color.fromRGB(0xE6E600))
                .withFlicker()
                .withTrail()
                .build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(level);

        firework.setFireworkMeta(fireworkMeta);

        if (BeanPass.getInstance().activeGUIs.containsKey(player))
        {
            BeanPassGUI beanPassGUI = BeanPass.getInstance().activeGUIs.get(player);
            beanPassGUI.reloadLevelElements();
        }
    }

    public UUID getUUID() { return UUID; }

    public Quest giveQuest(Quest quest)
    {
        if (quest == null) quest = Quests.getRandomQuestType(UUID.toString());

        quests.add(quest);
        return quest;
    }

    public List<Quest> getQuests() { return quests; }
}
