package pw.stellaric.BetaReduxHelper.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;
import pw.stellaric.BetaReduxHelper.util.TimeAgo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RecentCommand implements CommandExecutor {

    private final BetaReduxHelper brh;

    // sorted index 0 = oldest, index 4 = newest. max of 10?
    int maxLengthLastOnlinePlayers = 7;
    ArrayList<LeftPlayer> lastOnlinePlayers;
    Configuration config;

    public RecentCommand(BetaReduxHelper plugin) {
        this.brh = plugin;
        this.config = this.brh.getConfiguration();
        this.lastOnlinePlayers = new ArrayList<>();
        onEnable();
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
                    String add = ChatColor.WHITE + leftPlayer.username + ChatColor.GRAY + " (" + agoTime + ")";
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
                    String add = ChatColor.WHITE + leftPlayer.username + ChatColor.GRAY + " (" + agoTime + ")";
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

    public void onEnable() {
        // load lastOnlinePlayers from config
        List<String> lastOnlinePlayersPaths = config.getKeys("recent_players");

        if (lastOnlinePlayersPaths == null || lastOnlinePlayersPaths.isEmpty()) {
            return;
        }

        for (String lastOnlinePlayerPath : lastOnlinePlayersPaths) {
            String username = config.getString("recent_players." + lastOnlinePlayerPath + ".username");
            long epochSeconds = Long.parseLong(config.getString("recent_players." + lastOnlinePlayerPath + ".epochseconds"));

            lastOnlinePlayers.add(new LeftPlayer(username, epochSeconds));
        }

        this.brh.log("Loaded " + lastOnlinePlayers.size() + " recent players from config.");
    }

    public void onDisable() {
        // save lastOnlinePlayers to config
        config.removeProperty("recent_players");
        int index = 1;
        for (LeftPlayer leftPlayer : lastOnlinePlayers) {
            config.setProperty("recent_players." + index + ".username", leftPlayer.username);
            config.setProperty("recent_players." + index + ".epochseconds", leftPlayer.epochSeconds);
            index++;
        }
        config.save();
        this.brh.log("Saved " + lastOnlinePlayers.size() + " recent players to config.");
    }

    public void removeLoggedOffPlayerFromListIfNeeded(Player player) {
        int index = -1;
        for (int i = 0; i < lastOnlinePlayers.size(); i++) {
            String username = lastOnlinePlayers.get(i).username;
            if(username.equalsIgnoreCase(player.getName())){
                index = i;
            }
        }

        if(index != -1)
            lastOnlinePlayers.remove(index);
    }

    public void addLoggedOffPlayerToList(Player player) {
        long secondsNow = Instant.now().getEpochSecond();
        LeftPlayer addition = new LeftPlayer(player.getName(), secondsNow);

        if(lastOnlinePlayers.contains(addition)) {
            int index = 0;
            for (int i = 0; i < lastOnlinePlayers.size(); i++) {
                String username = lastOnlinePlayers.get(i).username;
                if(username.equalsIgnoreCase(addition.username)){
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
    String username;
    long epochSeconds;

    public LeftPlayer(String username, long epochSeconds) {
        this.username = username;
        this.epochSeconds = epochSeconds;
    }
}

