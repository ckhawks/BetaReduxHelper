package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

public class NextAnnouncementCommand implements CommandExecutor {

    private final BetaReduxHelper brh;

    public NextAnnouncementCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("nextannouncement")){
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("betaredux.sign.admin")) {
                    this.brh.announcer.sendAnnouncement();
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have permission.");
                    return true;
                }
            } else {
                ConsoleCommandSender consoleCommandSender = (ConsoleCommandSender) sender;
                this.brh.announcer.sendAnnouncement();
                return true;
            }
        }

        return false;
    }
}