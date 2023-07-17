package me.karltroid.beanpass.data;

import com.earth2me.essentials.User;
import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.Rewards.Reward;
import me.karltroid.beanpass.gui.BeanPassGUI;
import me.karltroid.beanpass.gui.GUIMenu;
import me.karltroid.beanpass.mounts.Mount;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerData
{
    // current instance player data
    OfflinePlayer player;
    public CompletableFuture<Boolean> responseFuture;
    boolean bedrockAccount;

    // current season player data
    double xp;
    int lastKnownLevel;
    List<Quest> quests = new ArrayList<>();
    Boolean premium;

    // persistent player data
    int maxHomes;
    List<Integer> ownedSkins;
    List<Integer> ownedMounts;
    List<Skin> equippedSkins = new ArrayList<>();
    List<Mount> equippedMounts = new ArrayList<>();

    public PlayerData(UUID UUID, boolean premium, List<Integer> ownedSkins, List<Integer> ownedMounts, double xp, int lastKnownLevel, int maxHomes)
    {
        this.player = Bukkit.getOfflinePlayer(UUID);
        this.premium = premium;
        this.ownedSkins = ownedSkins;
        this.ownedMounts = ownedMounts;
        this.xp = xp;
        this.lastKnownLevel = lastKnownLevel;
        this.maxHomes = maxHomes;
        this.bedrockAccount = player.getName().startsWith(".");
    }

    public void setPremiumPass(boolean hasPass) { this.premium = hasPass; }
    public void giveSkin(Skin skin, boolean alert)
    {
        if (ownedSkins.contains(skin.getId()))
        {
            if (alert) BeanPass.sendMessage(player, ChatColor.RED + "You were given a " + skin.getName() + " but you already own this skin.");
            return;
        }
        if (alert) BeanPass.sendMessage(player, ChatColor.GREEN + "You were given a " + skin.getName() + "! Equip it in the /skins menu!");
        ownedSkins.add(skin.getId());
    }
    public void giveMount(Mount mount, boolean alert)
    {
        if (ownedMounts.contains(mount.getId()))
        {
            if (alert) BeanPass.sendMessage(player, ChatColor.RED + "You were given a " + mount.getName() + " but you already own this mount.");
            return;
        }
        if (alert) BeanPass.sendMessage(player, ChatColor.GREEN + "You were given a " + mount.getName() + "! Equip it in the /mounts menu!");
        ownedMounts.add(mount.getId());
    }
    public void giveSkinById(Integer skinId, boolean alert)
    {
        Skin skin = BeanPass.getInstance().skinManager.getSkinById(skinId);
        if (hasSkin(skinId))
        {
            if (alert) BeanPass.sendMessage(player, ChatColor.RED + "You were given a " + skin.getName() + " but you already own this skin.");
            return;
        }
        ownedSkins.add(skinId);
    }
    public void giveMountById(Integer mountId, boolean alert)
    {
        Mount mount = BeanPass.getInstance().mountManager.getMountById(mountId);
        if (hasMount(mountId))
        {
            if (alert) BeanPass.sendMessage(player, ChatColor.RED + "You were given a " + mount.getName() + " but you already own this mount.");
            return;
        }
        ownedMounts.add(mountId);
    }
    public boolean isBedrockAccount()
    {
        return bedrockAccount;
    }
    public void equipSkin(Skin skin, boolean alert)
    {
        for (Skin equippedSkin : equippedSkins)
        {
            if (equippedSkin.getSkinApplicant().equals(skin.getSkinApplicant()))
            {
                unequipSkin(equippedSkin);
                break;
            }
        }

        if (alert) BeanPass.sendMessage(player, "Equipped skin! The " + skin.getName().toLowerCase().replace("_", " ") + " will appear on any " + skin.skinApplicant.name().toLowerCase().replace("_", " ") + " you equip now.");
        equippedSkins.add(skin);
    }
    public void equipMount(Mount mount, boolean alert)
    {
        for (Mount equippedMount : equippedMounts)
        {
            if (equippedMount.getMountApplicant().equals(mount.getMountApplicant()))
            {
                unequipMount(equippedMount);
                break;
            }
        }

        if (alert) BeanPass.sendMessage(player, "Equipped mount! The " + mount.getName().toLowerCase().replace("_", " ") + " mount will appear instead for any " + mount.getMountApplicant().name().toLowerCase().replace("_", " ") + " you ride now.");
        equippedMounts.add(mount);
    }
    public void unequipSkin(Skin skin)
    {
        equippedSkins.remove(skin);
    }
    public void unequipMount(Mount mount)
    {
        equippedMounts.remove(mount);
    }
    public List<Integer> getAllOwnedSkinIds()
    {
        return ownedSkins;
    }
    public List<Integer> getAllOwnedMountIds()
    {
        return ownedMounts;
    }
    public void removeSkin(int skinID) { this.ownedSkins.removeIf( hat -> hat.equals(skinID)); }
    public void removeMount(int mountID) { this.ownedMounts.removeIf( mount -> mount.equals(mountID)); }
    public boolean hasSkin(int skinID) { return ownedSkins.contains(skinID); }
    public boolean hasMount(int mountID) { return ownedMounts.contains(mountID); }
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
    public void addXp(double xp)
    {
        int beforeLevel = getLevel();

        this.xp += xp;
        int afterLevel = getLevel();

        Player p = (player.getPlayer());
        if (BeanPass.getInstance().activeGUIs.containsKey(p))
        {
            BeanPassGUI beanPassGUI = BeanPass.getInstance().activeGUIs.get(p);
            beanPassGUI.reloadLevelElements();
        }

        if (beforeLevel == afterLevel) return;
        // levelled up!
        leveledUp();
    }

    public void increaseMaxHomes(int increment)
    {
        maxHomes += increment;
        OfflinePlayer player = Bukkit.getOfflinePlayer(getUUID());
        User essentialsPlayer = BeanPass.getInstance().getEssentials().getUser(getUUID());
        BeanPass.sendMessage(player, "You can now set " + increment + " more " + ((increment > 1) ? "homes" : "home") + "! " + essentialsPlayer.getHomes().size() + "/" + maxHomes + " used.");
    }

    public int getMaxHomeAmount()
    {
        return maxHomes;
    }

    public List<Mount> getEquippedMounts() { return equippedMounts; }

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
        int newLevel = getLevel();
        Player player = Bukkit.getPlayer(getUUID());
        World world = player.getWorld();

        for(int l = lastKnownLevel + 1; l <= newLevel; l++)
        {
            // alert all the players that this player levelled up (give the player a personalized message)
            BeanPass.sendMessage(player, ChatColor.GREEN + "" + ChatColor.BOLD + "You leveled up to" + ChatColor.YELLOW + " " + ChatColor.BOLD + "LVL " + l);
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (p == player) continue;
                BeanPass.sendMessage(p, ChatColor.GREEN + player.getDisplayName() + " leveled up to" + ChatColor.YELLOW + " " + "LVL " + l);
            }

            Location location = player.getLocation();
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
            fireworkMeta.setPower(l);
            firework.setFireworkMeta(fireworkMeta);

            Reward freeReward = BeanPass.getInstance().getSeason().getLevel(l).getFreeReward();
            if (freeReward != null) freeReward.giveReward(getUUID());

            // in the future check if they have premium !!!!!!!!
            Reward premiumReward = BeanPass.getInstance().getSeason().getLevel(l).getPremiumReward();
            if (premiumReward != null) premiumReward.giveReward(getUUID());
        }

        if (BeanPass.getInstance().activeGUIs.containsKey(player))
        {
            BeanPassGUI beanPassGUI = BeanPass.getInstance().activeGUIs.get(player);
            if (beanPassGUI.getCurrentGUIMenu() == GUIMenu.BeanPass) beanPassGUI.reloadLevelElements();
        }

        lastKnownLevel = newLevel;
    }

    public UUID getUUID() { return player.getUniqueId(); }

    public Quest giveQuest(Quest quest, boolean alert)
    {
        quests.add(quest);
        if (alert) BeanPass.sendMessage(player, ChatColor.GREEN + "" + ChatColor.BOLD + "NEW QUEST: " + ChatColor.GREEN + quest.getGoalDescription() + ChatColor.YELLOW + " " + ChatColor.ITALIC + quest.getRewardDescription());
        return quest;
    }

    public void removeQuest(Quest quest, boolean alert)
    {
        if (!quests.contains(quest)) return;

        quests.remove(quest);
        if (alert) BeanPass.sendMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "REMOVED QUEST: " + ChatColor.RED + quest.getGoalDescription());
    }

    public List<Quest> getQuests() { return quests; }

    public double getBalance()
    {
        return BeanPass.getInstance().getEconomy().getBalance(player);
    }

    public int getHomeAmount()
    {
        return BeanPass.getInstance().getEssentials().getUser(getUUID()).getHomes().size();
    }
}
