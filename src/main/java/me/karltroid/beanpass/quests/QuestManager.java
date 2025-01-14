package me.karltroid.beanpass.quests;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import de.ancash.actionbar.ActionBarAPI;
import me.karltroid.beanpass.BeanPass;
import me.karltroid.beanpass.data.PlayerData;
import me.karltroid.beanpass.data.PlayerDataManager;
import me.karltroid.beanpass.quests.Quests.*;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuestManager implements Listener
{
    final String BOLD_GREEN = ChatColor.GREEN + "" + ChatColor.BOLD;
    final String BOLD_GRAY = ChatColor.GRAY + "" + ChatColor.BOLD;
    final String ITALIC_YELLOW = ChatColor.YELLOW + "" + ChatColor.ITALIC;


    void completeQuest(Player player, PlayerData playerData, Quest quest)
    {
        playerData.getQuests().remove(quest);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        BeanPass.sendMessage(player, BOLD_GREEN + "QUEST COMPLETED: " + ChatColor.GREEN + quest.getGoalDescription());
        playerData.addXp(quest.getXPReward());
    }


    /*@EventHandler (priority = EventPriority.HIGHEST)
    public void onFarmingQuestProgressed(PlayerInteractEvent event)
    {
        System.out.println("a");
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        System.out.println("b");

        Block b = event.getClickedBlock();
        if (b == null) return;
        System.out.println("c");

        Ageable ageable = (Ageable) b.getBlockData();
        int age = ageable.getAge();
        System.out.println(age);
        System.out.println(ageable.getMaximumAge());
        if (age != ageable.getMaximumAge()) return;
        System.out.println("d");

        PlayerData playerData = BeanPass.getInstance().getPlayerData(event.getPlayer().getUniqueId());
        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            System.out.println("e");
            if (!(quest instanceof MiningQuest)) continue;
            if (!(b.getType().name().contains(((MiningQuest) quest).getGoalBlockType().name()))) continue;

            MiningQuest miningQuest = (MiningQuest) quest;
            miningQuest.incrementPlayerCount(1);
            if (miningQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, miningQuest);
        }
    }*/

    @EventHandler (priority = EventPriority.LOWEST)
    void onMiningAndFarmingQuestProgressed(BlockBreakEvent event)
    {
        // if the block goal is something that grows, only count it if its fully grown
        Block block = event.getBlock();
        BlockData blockData = block.getState().getBlockData();
        if (blockData instanceof Ageable)
        {
            Ageable ageable = (Ageable) blockData;
            if (ageable.getAge() != ageable.getMaximumAge()) return;
        }

        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerData playerData = PlayerDataManager.getPlayerData(playerUUID);

        Material blockMinedType = event.getBlock().getType();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof MiningQuest)) continue;
            if (!(blockMinedType.name().contains(((MiningQuest) quest).getGoalBlockType().name()))) continue;
            if (isBlockProtected(block) || isBlockManMade(block))
            {
                ActionBarAPI.sendActionBar(event.getPlayer(), ChatColor.RED + "Blocks placed by players don't count for quests");
                continue;
            }

            MiningQuest miningQuest = (MiningQuest) quest;
            miningQuest.incrementPlayerCount(1);
            if (miningQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, miningQuest);
        }
    }

    @EventHandler
    void onFishingQuestProgressed(PlayerFishEvent event)
    {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        UUID playerUUID = event.getPlayer().getUniqueId();
        PlayerData playerData = PlayerDataManager.getPlayerData(playerUUID);

        Entity caught = event.getCaught();

        if (caught == null || caught.getType() != EntityType.DROPPED_ITEM) return;

        Material caughtMaterial = ((Item)event.getCaught()).getItemStack().getType();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof FishingQuest)) continue;
            if (caughtMaterial != ((FishingQuest) quest).getGoalItemType()) continue;

            FishingQuest fishingQuest = (FishingQuest) quest;
            fishingQuest.incrementPlayerCount(1);
            if (fishingQuest.isCompleted()) completeQuest(event.getPlayer(), playerData, fishingQuest);
        }
    }

    @EventHandler
    void onCraftingQuestProgressed(CraftItemEvent event)
    {
        Player player = (Player)event.getWhoClicked();
        PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

        // Check if the click resulted in a potion being taken out
        ItemStack itemCrafted = event.getCurrentItem();
        if (itemCrafted == null) return;
        Material itemCraftedType = itemCrafted.getType();
        int amount = itemCrafted.getAmount();
        ItemStack cursorItem = event.getCursor();

        if (event.isShiftClick())
        {
            ItemStack[] matrix = event.getInventory().getMatrix();
            ItemStack smallestItem = null;
            for(int i = 2; i <= 65; i++)
            {
                for (ItemStack item : matrix) {
                    if (item == null || item.getAmount() >= i) continue;

                    smallestItem = item;
                    break;
                }

                if (smallestItem == null) continue;

                amount *= smallestItem.getAmount();
                break;
            }

            // check to make sure the amount crafted fit in their inventory, if not adjust amount accordingly
            int itemCraftedMaxStackSize = itemCrafted.getMaxStackSize();
            int playerInvSlotsOpen = 0;
            int playerInvAmountOfCraftedTypeFillable = 0;
            for (ItemStack slot : player.getInventory().getStorageContents()) {
                if (slot == null || slot.getType() == Material.AIR) {
                    playerInvSlotsOpen++;
                }
                else if (slot.getType() == itemCraftedType && slot.getAmount() < itemCraftedMaxStackSize)
                {
                    playerInvAmountOfCraftedTypeFillable += itemCraftedMaxStackSize - slot.getAmount();
                }
            }
            if (playerInvSlotsOpen == 0 && playerInvAmountOfCraftedTypeFillable == 0) return;

            int maxCraftedItemThatFitsInv = playerInvSlotsOpen * itemCraftedMaxStackSize + playerInvAmountOfCraftedTypeFillable;
            if (maxCraftedItemThatFitsInv < amount) amount = maxCraftedItemThatFitsInv;
        }
        else if (cursorItem != null && !(cursorItem.getType().equals(Material.AIR)))
        {
            // item in user's cursor, make sure item crafted can stack into it, else return
            if (cursorItem.getType() != itemCraftedType) return;
            if (cursorItem.getAmount() >= itemCrafted.getMaxStackSize()) return;
        }

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof CraftingQuest)) continue;
            CraftingQuest craftingQuest = (CraftingQuest) quest;

            if (!(itemCraftedType.equals(craftingQuest.getGoalItemType()))) continue;
            craftingQuest.incrementPlayerCount(amount);
            if (craftingQuest.isCompleted()) completeQuest(player, playerData, craftingQuest);
        }
    }

    @EventHandler
    public void onAnimalBreedingQuestProgressed(EntityBreedEvent event)
    {
        if (!(event.getBreeder() instanceof Player)) return;
        Player player = (Player)event.getBreeder();
        if (player == null) return;

        EntityType entityTypeBred = event.getEntityType();

        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = PlayerDataManager.getPlayerData(playerUUID);

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof BreedingQuest)) continue;
            BreedingQuest breedingQuest = (BreedingQuest) quest;

            if (entityTypeBred != ((BreedingQuest) quest).getGoalEntityType()) continue;
            breedingQuest.incrementPlayerCount(1);
            if (breedingQuest.isCompleted()) completeQuest(player, playerData, breedingQuest);
        }
    }

    @EventHandler
    public void onCraftingQuestProgressed_Furnace(FurnaceExtractEvent event)
    {
        Player player = event.getPlayer();
        PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

        // Check if the click resulted in a potion being taken out
        Material itemSmeltedType = event.getItemType();
        int amountSmelted = event.getItemAmount();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof CraftingQuest)) continue;
            CraftingQuest craftingQuest = (CraftingQuest) quest;

            if (!(itemSmeltedType.equals(craftingQuest.getGoalItemType()))) continue;
            craftingQuest.incrementPlayerCount(amountSmelted);
            if (craftingQuest.isCompleted()) completeQuest(player, playerData, craftingQuest);
        }
    }

    @EventHandler
    void onBrewingQuestProgressed(InventoryClickEvent event)
    {
        if (!(event.getClickedInventory() instanceof BrewerInventory)) return;

        Player player = (Player)event.getWhoClicked();
        PlayerData playerData = PlayerDataManager.getPlayerData(player.getUniqueId());

        // Check if the click resulted in a potion being taken out
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() != Material.POTION) return;

        PotionMeta potionMeta = (PotionMeta)currentItem.getItemMeta();
        NamespacedKey brewedKey = new NamespacedKey(BeanPass.getInstance(), "brewed");
        if (potionMeta.getPersistentDataContainer().has(brewedKey, PersistentDataType.BYTE)) return;
        else
        {
            potionMeta.getPersistentDataContainer().set(brewedKey, PersistentDataType.BYTE, (byte) 1);
            currentItem.setItemMeta(potionMeta);
        }

        PotionType potionType = (potionMeta).getBasePotionData().getType();

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof BrewingQuest)) continue;
            BrewingQuest brewingQuest = (BrewingQuest) quest;

            if (!(potionType.equals(brewingQuest.getGoalItemType()))) continue;
            brewingQuest.incrementPlayerCount(1);
            if (brewingQuest.isCompleted()) completeQuest(player, playerData, brewingQuest);
        }
    }

    @EventHandler
    void onKillingQuestProgressed(EntityDeathEvent event)
    {
        EntityType entityTypeKilled = event.getEntityType();
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        UUID playerUUID = player.getUniqueId();
        PlayerData playerData = PlayerDataManager.getPlayerData(playerUUID);

        List<Quest> playerQuests = new ArrayList<>(playerData.getQuests());
        for (Quest quest : playerQuests)
        {
            if (!(quest instanceof KillingQuest)) continue;
            KillingQuest killingQuest = (KillingQuest) quest;

            if (entityTypeKilled != ((KillingQuest) quest).getGoalEntityType()) continue;
            killingQuest.incrementPlayerCount(1);
            if (killingQuest.isCompleted()) completeQuest(player, playerData, killingQuest);
        }
    }

    public boolean isBlockProtected(Block block)
    {
        com.sk89q.worldedit.util.Location worldEditLocation = BukkitAdapter.adapt(block.getLocation());
        ApplicableRegionSet regions = BeanPass.getInstance().getWorldGuard().getPlatform().getRegionContainer().createQuery().getApplicableRegions(worldEditLocation);

        return !regions.getRegions().isEmpty();
    }

    public boolean isBlockManMade(Block block)
    {
        BlockData blockData = block.getState().getBlockData();
        if (blockData instanceof Ageable) return false;

        CoreProtectAPI coreProtectAPI = BeanPass.getInstance().getCoreProtectAPI();
        List<String[]> lookupResult = coreProtectAPI.blockLookup(block, 0).stream()
                .filter(result -> coreProtectAPI.parseResult(result).getBlockData().getMaterial().equals(block.getType()))
                .collect(Collectors.toList());
        if (lookupResult.isEmpty()) return false;


        int blockState = 0;
        for (String[] strings : lookupResult)
        {
            ParseResult result = coreProtectAPI.parseResult(strings);

            if (result.getActionId() == 1) blockState++;
            else if (result.getActionId() == 0) blockState--;
        }

        // >=0, block was placed by player
        // <0, block was not placed by player
        return blockState >= 0;
    }
}
