package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

public class UpdateSignsCommand implements CommandExecutor {

    private final BetaReduxHelper brh;

    public UpdateSignsCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("updatesigns") && player.hasPermission("betaredux.sign.admin")) {
                this.brh.signUtils.updateSigns(); // update
                player.sendMessage(ChatColor.YELLOW + "" + this.brh.signUtils.getNumberOfSigns() + " signs have been updated.");
                return true;
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender consoleCommandSender = (ConsoleCommandSender) sender;
            if (command.getName().equalsIgnoreCase("updatesigns")) {
                this.brh.signUtils.updateSigns(); // update
                consoleCommandSender.sendMessage(ChatColor.YELLOW + "" + this.brh.signUtils.getNumberOfSigns() + " signs have been updated.");
                return true;
            }
        }

        return false;
    }
}