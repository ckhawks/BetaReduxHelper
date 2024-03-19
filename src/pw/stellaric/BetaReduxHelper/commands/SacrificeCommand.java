package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;
import pw.stellaric.BetaReduxHelper.util.SchedulerTask;

import java.util.HashMap;
import java.util.List;

// 1 diamond
    // 2 gold
    // 8 iron
    // 32 redstone
    // set day time
    // turn off rain
    // return to spawn
    // 8 sponge
    // 16 ice
    // 8 cobweb
public class SacrificeCommand implements CommandExecutor {

    private final BetaReduxHelper brh;
    private final String costMessage = ChatColor.YELLOW + "Cost: "
            + ChatColor.GREEN + "1 diamond" + ChatColor.WHITE + " or "
            + ChatColor.GREEN + "2 gold" + ChatColor.WHITE + " or "
            + ChatColor.GREEN + "8 iron" + ChatColor.WHITE + " or "
            + ChatColor.GREEN + "32 redstone";
    private final String typesMessage = ChatColor.YELLOW + "Types: "
            + ChatColor.GOLD + "day" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "night" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "clearweather" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "thunderstorm" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "spawn" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "8sponge" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "16ice" + ChatColor.WHITE + ", "
            + ChatColor.GOLD + "8cobweb";

    private final HashMap<Material, String[]> itemNames = new HashMap<>();


    public SacrificeCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
        itemNames.put(Material.DIAMOND, new String[]{"Diamond", "Diamonds"});
        itemNames.put(Material.IRON_INGOT, new String[]{"Iron Ingot", "Iron Ingots"});
        itemNames.put(Material.GOLD_INGOT, new String[]{"Gold Ingot", "Gold Ingots"});
        itemNames.put(Material.REDSTONE, new String[]{"Redstone Dust", "Redstone Dust"});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("/sacrifice can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("sacrifice")) {
            if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.YELLOW + "Sacrifice options:");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.GREEN + "/sacrifice help" + ChatColor.WHITE + " Displays this message");
                sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.GREEN + "/sacrifice <type>" + ChatColor.WHITE + " Takes sacrifice in exchange for reward");
                sender.sendMessage(typesMessage);
                sender.sendMessage(costMessage);
                sender.sendMessage(ChatColor.YELLOW + "Hold the item in your hand when conducting the sacrifice.");
                sender.sendMessage("");
                return true;
            }

            if(args.length >= 1) {
                if (!playerHasEnoughSacrificeItemHolding(player)) {
                    sender.sendMessage("You aren't holding enough of a sacrificial item.");
                    sender.sendMessage(costMessage);
                    return true;
                }

                ItemStack sacrificed = null;
                switch(args[0]) {
                    case "day":
                        sacrificed = takePlayerSacrificialItem(player);
                        this.brh.getServer().getWorlds().get(0).setTime(1);
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to make it day");
                        break;
                    case "night":
                        sacrificed = takePlayerSacrificialItem(player);
                        this.brh.getServer().getWorlds().get(0).setTime(12000);
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to make it night");
                        break;
                    case "clearweather":
                        sacrificed = takePlayerSacrificialItem(player);
                        this.brh.getServer().getWorlds().get(0).setThundering(false);
                        this.brh.getServer().getWorlds().get(0).setWeatherDuration(1);
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to clear the weather");
                        break;
                    case "thunderstorm":
                        sacrificed = takePlayerSacrificialItem(player);
                        this.brh.getServer().getWorlds().get(0).setThundering(true);
                        this.brh.getServer().getWorlds().get(0).setWeatherDuration(600);
                        this.brh.getServer().getWorlds().get(0).setThunderDuration(600);
                        this.brh.getServer().getWorlds().get(0).setStorm(true);
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to start a thunderstorm");
                        break;
                    case "spawn":
                        sacrificed = takePlayerSacrificialItem(player);
                        Location spawnLocation = this.brh.getServer().getWorlds().get(0).getSpawnLocation();
                        player.teleport(spawnLocation);
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to teleport to spawn");
                        break;
                    case "8sponge":
                        sacrificed = takePlayerSacrificialItem(player);
                        ItemStack dropItemStackSponge = new ItemStack(Material.SPONGE, 1);
                        for(int i = 0; i < 8; i++) {
                            this.brh.getServer().getWorlds().get(0).dropItemNaturally(player.getLocation(), dropItemStackSponge);
                        }
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to receive 8 sponge");
                        break;
                    case "16ice":
                        sacrificed = takePlayerSacrificialItem(player);
                        ItemStack dropItemStackIce = new ItemStack(Material.ICE, 1);
                        for(int i = 0; i < 16; i++) {
                            this.brh.getServer().getWorlds().get(0).dropItemNaturally(player.getLocation(), dropItemStackIce);
                        }
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to receive 16 ice");
                        break;
                    case "8cobweb":
                        sacrificed = takePlayerSacrificialItem(player);
                        ItemStack dropItemStackCobweb = new ItemStack(Material.WEB, 1);
                        for(int i = 0; i < 8; i++) {
                            this.brh.getServer().getWorlds().get(0).dropItemNaturally(player.getLocation(), dropItemStackCobweb);
                        }
                        playSacrificeEffect(player);
                        displaySacrificialMessage(player, sacrificed, "to receive 8 cobweb");
                        break;
                    default:
                        player.sendMessage(ChatColor.YELLOW + "That is not a valid sacrificial type.");
                        player.sendMessage(typesMessage);
                        break;
                }
                return true;
            }
        }

        return false;
    }

    public void playSacrificeEffect(Player player) {
        this.brh.getServer().getWorlds().get(0).playEffect(player.getLocation(), Effect.CLICK1, 0, 10);
        SchedulerTask task = new SchedulerTask(this.brh, player, this.brh.getServer().getScheduler(), 6);

        // schedule task
        int taskId = this.brh.getServer().getScheduler().scheduleSyncRepeatingTask(brh, task, 0, 1); // (long) this.announcementIntervalMinutes * minute); // minute * 5
        task.setTaskId(taskId);
    }

    public void displaySacrificialMessage(Player player, ItemStack sacrificed, String action){
        String[] itemNameArray = itemNames.get(sacrificed.getType());
        String itemName;
        int itemAmount = sacrificed.getAmount();
        if(itemAmount == 1) {
            itemName = itemNameArray[0];
        } else {
            itemName = itemNameArray[1];
        }
        this.brh.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE + " sacrificed " + ChatColor.GREEN + itemAmount + " " + itemName + ChatColor.WHITE + " " + action + "!");
    }

    public boolean playerHasEnoughSacrificeItemHolding(Player player) {
        ItemStack holdingItems = player.getItemInHand();
        int amount = holdingItems.getAmount();
        Material material = holdingItems.getType();

        if(material == Material.DIAMOND && amount >= 1) {
            return true;
        }
        if(material == Material.GOLD_INGOT && amount >= 2) {
            return true;
        }
        if(material == Material.IRON_INGOT && amount >= 8) {
            return true;
        }
        if(material == Material.REDSTONE && amount >= 32) {
            return true;
        }

        return false;
    }

    public ItemStack takePlayerSacrificialItem(Player player) {
        ItemStack holdingItems = player.getItemInHand();
        int amount = holdingItems.getAmount();
        Material material = holdingItems.getType();

        if(material == Material.DIAMOND && amount >= 1) {
            if(amount > 1) {
                holdingItems.setAmount(amount - 1);
                player.setItemInHand(holdingItems);
            } else if (amount == 1) {
                player.setItemInHand(null);
            }

            return new ItemStack(material, 1);
        }
        if(material == Material.GOLD_INGOT && amount >= 2) {
            if(amount > 2) {
                holdingItems.setAmount(amount - 2);
                player.setItemInHand(holdingItems);
            } else if (amount == 2) {
                player.setItemInHand(null);
            }
            return new ItemStack(material, 2);
        }
        if(material == Material.IRON_INGOT && amount >= 8) {
            if(amount > 8) {
                holdingItems.setAmount(amount - 8);
                player.setItemInHand(holdingItems);
            } else if (amount == 8) {
                player.setItemInHand(null);
            }
            return new ItemStack(material, 8);
        }
        if(material == Material.REDSTONE && amount >= 32) {
            if(amount > 32) {
                holdingItems.setAmount(amount - 32);
                player.setItemInHand(holdingItems);
            } else if (amount == 32) {
                player.setItemInHand(null);
            }
            return new ItemStack(material, 32);
        }

        return null;
    }
}