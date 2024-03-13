package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

public class RestartNowCommand implements CommandExecutor {

    private final BetaReduxHelper brh;

    public RestartNowCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("restartnow") && player.hasPermission("betaredux.sign.admin")) {
                this.brh.restartUtils.restart2Minutes();
                player.sendMessage(ChatColor.YELLOW + "Manual restart countdown has been initiated.");
                return true;
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender consoleCommandSender = (ConsoleCommandSender) sender;
            if (command.getName().equalsIgnoreCase("restartnow")) {
                this.brh.restartUtils.restart2Minutes();
                consoleCommandSender.sendMessage("Manual restart countdown has been initiated.");
                return true;
            }
        }

        return false;
    }
}