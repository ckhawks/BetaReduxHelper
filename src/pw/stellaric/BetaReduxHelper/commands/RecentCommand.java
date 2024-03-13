package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;
import pw.stellaric.BetaReduxHelper.util.TimeAgo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

public class RecentCommand implements CommandExecutor {

    private final BetaReduxHelper brh;

    // sorted index 0 = oldest, index 4 = newest. max of 10?
    int maxLengthLastOnlinePlayers = 7;
    ArrayList<LeftPlayer> lastOnlinePlayers;

    public RecentCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
        this.lastOnlinePlayers = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("recent")) {
                List<String> playerStrings = new ArrayList<>();

                for(int j = lastOnlinePlayers.size() - 1; j >= 0; j--){
                    LeftPlayer leftPlayer = lastOnlinePlayers.get(j);
                    long secondsNow = Instant.now().getEpochSecond();
                    String agoTime = TimeAgo.toDuration(secondsNow - leftPlayer.epochSeconds);
                    String add = ChatColor.WHITE + leftPlayer.player.getDisplayName() + ChatColor.GRAY + " (" + agoTime + ")";
                    playerStrings.add(add);
                }

                String recentPlayersString = String.join(", ", playerStrings);
                if(recentPlayersString.equalsIgnoreCase(""))
                    recentPlayersString = ChatColor.GRAY + "None";

                player.sendMessage(ChatColor.YELLOW + "Recently seen players: " + recentPlayersString);
                return true;
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            ConsoleCommandSender consoleCommandSender = (ConsoleCommandSender) sender;
            if (command.getName().equalsIgnoreCase("recent")) {
                List<String> playerStrings = new ArrayList<>();

                for(int j = lastOnlinePlayers.size() - 1; j >= 0; j--){
                    LeftPlayer leftPlayer = lastOnlinePlayers.get(j);
                    long secondsNow = Instant.now().getEpochSecond();
                    String agoTime = TimeAgo.toDuration(secondsNow - leftPlayer.epochSeconds);
                    String add = ChatColor.WHITE + leftPlayer.player.getDisplayName() + ChatColor.GRAY + " (" + agoTime + ")";
                    playerStrings.add(add);
                }

                String recentPlayersString = String.join(", ", playerStrings);
                if(recentPlayersString.equalsIgnoreCase(""))
                    recentPlayersString = ChatColor.DARK_GRAY + "None";

                consoleCommandSender.sendMessage(ChatColor.YELLOW + "Recently seen players: " + recentPlayersString);
                return true;
            }
        }

        return false;
    }

    public void removeLoggedOffPlayerFromListIfNeeded(Player player) {
        int index = -1;
        for (int i = 0; i < lastOnlinePlayers.size(); i++) {
            if(lastOnlinePlayers.get(i).player.getDisplayName().equalsIgnoreCase(player.getDisplayName())){
                index = i;
            }
        }

        if(index != -1)
            lastOnlinePlayers.remove(index);
    }

    public void addLoggedOffPlayerToList(Player player) {
        long secondsNow = Instant.now().getEpochSecond();
        LeftPlayer addition = new LeftPlayer(player, secondsNow);

        if(lastOnlinePlayers.contains(addition)) {
            int index = 0;
            for (int i = 0; i < lastOnlinePlayers.size(); i++) {
                if(lastOnlinePlayers.get(i).player.getDisplayName().equalsIgnoreCase(addition.player.getDisplayName())){
                    index = i;
                }
            }
            lastOnlinePlayers.remove(index);
        }

        // this _shouldn't_ run if the above runs
        if(lastOnlinePlayers.size() >= maxLengthLastOnlinePlayers) {
            lastOnlinePlayers.remove(0);
        }

        lastOnlinePlayers.add(addition);
    }
}

class LeftPlayer {
    Player player;
    long epochSeconds;

    public LeftPlayer(Player player, long epochSeconds) {
        this.player = player;
        this.epochSeconds = epochSeconds;
    }
}

