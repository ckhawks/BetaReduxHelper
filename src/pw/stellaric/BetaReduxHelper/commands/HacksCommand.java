package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

public class HacksCommand implements CommandExecutor {

    private final BetaReduxHelper brh;

    public HacksCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("hacks") && args.length == 0) {
                player.sendMessage(ChatColor.YELLOW + "Usage: /hacks <on/off>");
                return true;
            }

            if (command.getName().equalsIgnoreCase("hacks") && args.length >= 1 && args[0].equalsIgnoreCase("on")) {
                player.sendMessage(ChatColor.YELLOW + "You have enabled hacks! Don't get caught!");
                Player[] players = this.brh.getServer().getOnlinePlayers();
                for(Player p : players) {
                    if (!p.equals(player)) {
                        p.sendMessage(ChatColor.RED + "WARNING: " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " has turned on hacks! Shame them!");
                    }
                }
                return true;
            }

            if (command.getName().equalsIgnoreCase("hacks") && args.length >= 1 && args[0].equalsIgnoreCase("off")) {
                player.sendMessage(ChatColor.YELLOW + "You have disabled hacks. Thank you.");
                Player[] players = this.brh.getServer().getOnlinePlayers();
                for(Player p : players) {
                    if (!p.equals(player)) {
                        p.sendMessage(ChatColor.RED + "NOTICE: " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " has turned off their hacks. Thank you!");
                    }
                }
                return true;
            }

            player.sendMessage(ChatColor.YELLOW + "Unrecognized usage of command.");
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender consoleCommandSender = (ConsoleCommandSender) sender;
            if (command.getName().equalsIgnoreCase("hacks")) {
                consoleCommandSender.sendMessage("Hacks can only be enabled by a player.");
                return true;
            }
        }

        return false;
    }
}