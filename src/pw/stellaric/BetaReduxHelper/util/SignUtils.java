package pw.stellaric.BetaReduxHelper.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.config.Configuration;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.Integer.parseInt;

public class SignUtils {

    BetaReduxHelper brh;
    Set<SignEntry> signs;
    Configuration config;

    public SignUtils(BetaReduxHelper plugin) {
        this.brh = plugin;
        this.config = this.brh.getConfiguration();
        this.signs = new HashSet<>();
        loadExistingSigns();
    }

    public void updateSigns() {
        this.brh.log("Updating " + this.signs.size() + " signs text...", true);

        // stupid hack to prevent ConcurrentModificationException
        Set<SignEntry> signsToRemove = new HashSet<>();

        // update each of the signs, or check if they still exist
        for (SignEntry sign : this.signs) {
            Block signBlock = this.brh.getServer().getWorlds().get(0).getBlockAt(sign.x, sign.y, sign.z);

            if(signBlock.getType() != Material.WALL_SIGN && signBlock.getType() != Material.SIGN_POST) {
//                this.brh.log("[debug] A sign at " + sign.x + ", " + sign.y + ", " + sign.z + " is missing! Removing");
                signsToRemove.add(sign);
                continue;
            }

//            this.brh.log("[debug] Updating " + sign.type + " sign at " + sign.x + ", " + sign.y + ", " + sign.z);
            Sign signBlockReal = (Sign) signBlock.getState();
            setSignLines(signBlockReal, sign.type);
        }

        // remove signs
        for(SignEntry signEntry : signsToRemove) {
            this.signs.remove(signEntry);
        }

        updateSavedSignsInConfig();
        this.brh.log("Updated sign text!", true);
    }

    public void loadExistingSigns() {
        this.brh.log("Loading signs from config.yml file...");
        try {
            List<String> signsPaths = config.getKeys("signs");

            if(signsPaths == null || signsPaths.isEmpty()) {
                this.brh.log("!n");
                return;
            }

            for(String signPath : signsPaths) {
                //            this.brh.log("signPath: " + signPath);
                String type = config.getString("signs." + signPath + ".type");
                int x = config.getInt("signs." + signPath + ".x", 0);
                int y = config.getInt("signs." + signPath + ".y", 0);
                int z = config.getInt("signs." + signPath + ".z", 0);
                int idx = parseInt(signPath);
                //            this.brh.log("idx: " + idx + ", x: " + x);
                signs.add(new SignEntry(type, x, y, z, idx));
            }
            this.brh.log("Loaded " + signs.size() + " signs from config.yml.");
        } catch (NullPointerException e) {
            this.brh.log("signs key does not exist", true);
        }
    }

    public void handleOnSignChangeEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        for (String line : lines) {
            switch(line.toLowerCase()) {
                case "{playerunique}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "all_time_count");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created all time player count sign!");
                        registerSign("all_time_count", event);
                    }
                    break;
                case "{playercount}":
                    event.setCancelled(true);
                    setSignLines((Sign) event.getBlock().getState(), "current_player_count");
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "Created current player count sign!");
                    registerSign("current_player_count", event);
                    break;
                case "{blocksbroken}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "blocks_broken");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created blocks broken count sign!");
                        registerSign("blocks_broken", event);
                    }
                    break;
                case "{blocksplaced}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "blocks_placed");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created blocks placed count sign!");
                        registerSign("blocks_placed", event);
                    }
                    break;
                case "{deaths}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "player_deaths");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created player death count sign!");
                        registerSign("player_deaths", event);
                    }
                    break;
                case "{deathslava}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "player_deaths_lava");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created player lava death count sign!");
                        registerSign("player_deaths_lava", event);
                    }
                    break;
                case "{deathspvp}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "player_deaths_pvp");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created player PvP death count sign!");
                        registerSign("player_deaths_pvp", event);
                    }
                    break;
                case "{deathsfall}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "player_deaths_fall");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created player fall death count sign!");
                        registerSign("player_deaths_fall", event);
                    }
                    break;
                case "{hostilemobs}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "hostile_mobs_killed");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created hostile mobs killed count sign!");
                        registerSign("hostile_mobs_killed", event);
                    }
                    break;
                case "{passivemobs}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "passive_mobs_killed");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created passive mobs killed count sign!");
                        registerSign("passive_mobs_killed", event);
                    }
                    break;
                case "{lastupdated}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "last_updated");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created last updated sign!");
                        registerSign("last_updated", event);
                    }
                    break;
                case "{hoursplayed}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "hours_played");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created hours played sign!");
                        registerSign("hours_played", event);
                    }
                    break;
                case "{chatmsgs}":
                    if(event.getPlayer().hasPermission("betaredux.sign.admin")) {
                        event.setCancelled(true);
                        setSignLines((Sign) event.getBlock().getState(), "messages_sent");
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "Created chat messages sent count sign!");
                        registerSign("messages_sent", event);
                    }
                    break;
            }
        }
    }

    public void setSignLines(Sign sign, String type) {
        switch (type){
            case "all_time_count":
                int numberOfPlayersAllTime = 0;
                numberOfPlayersAllTime = Objects.requireNonNull(new File("world/players").list()).length;
                sign.setLine(0, "All time");
                sign.setLine(1, "players:");
                sign.setLine(2, "" + numberOfPlayersAllTime);
                sign.setLine(3, "");
                sign.update();
                break;
            case "24h_players":
                break;
            case "current_player_count":
                int numberOfPlayersOnlineNow = this.brh.getServer().getOnlinePlayers().length;
                sign.setLine(0, "Players online");
                sign.setLine(1, "now:");
                sign.setLine(2, "" + numberOfPlayersOnlineNow);
                sign.setLine(3, "");
                sign.update();
                break;
            case "blocks_broken":
                sign.setLine(0, "Total blocks");
                sign.setLine(1, "broken:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricBlocksBroken);
                sign.setLine(3, "");
                sign.update();
                break;
            case "blocks_placed":
                sign.setLine(0, "Total blocks");
                sign.setLine(1, "placed:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricBlocksPlaced);
                sign.setLine(3, "");
                sign.update();
                break;
            case "hostile_mobs_killed":
                sign.setLine(0, "Hostile mobs");
                sign.setLine(1, "killed:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricMobsKilledHostile);
                sign.setLine(3, "");
                sign.update();
                break;
            case "passive_mobs_killed":
                sign.setLine(0, "Passive mobs");
                sign.setLine(1, "killed:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricMobsKilledPassive);
                sign.setLine(3, "");
                sign.update();
                break;
            case "player_deaths":
                sign.setLine(0, "Player deaths:");
                sign.setLine(1, "" );
                sign.setLine(2, "" + this.brh.metricsHandler.metricPlayerDeaths);
                sign.setLine(3, "");
                sign.update();
                break;
            case "player_deaths_lava":
                sign.setLine(0, "Player deaths");
                sign.setLine(1, "to lava:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricPlayerDeathsLava);
                sign.setLine(3, "");
                sign.update();
                break;
            case "player_deaths_pvp":
                sign.setLine(0, "Player deaths");
                sign.setLine(1, "to PvP:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricPlayerDeathsPvp);
                sign.setLine(3, "");
                sign.update();
                break;
            case "player_deaths_fall":
                sign.setLine(0, "Player deaths");
                sign.setLine(1, "to falls:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricPlayerDeathsFall);
                sign.setLine(3, "");
                sign.update();
                break;
            case "last_updated":
                LocalDateTime localDateTime = LocalDateTime.now();
                String timeString = localDateTime.getHour() + ":" + localDateTime.getMinute() + " CT";
                sign.setLine(0, "Last updated");
                sign.setLine(1, "at:");
                sign.setLine(2, timeString);
                sign.setLine(3, "");
                sign.update();
                break;
            case "hours_played":
                int totalHoursPlayed = this.brh.metricsHandler.metricMinutesPlayed / 60;
                int daysPlayed = totalHoursPlayed / 24;
                int hoursPlayed = totalHoursPlayed - daysPlayed * 24;
                sign.setLine(0, "Total hours");
                sign.setLine(1, "played:");
                sign.setLine(2, daysPlayed + "d " + hoursPlayed + "h");
                sign.setLine(3, "");
                sign.update();
                break;
            case "messages_sent":
                sign.setLine(0, "Chat messages");
                sign.setLine(1, "sent:");
                sign.setLine(2, "" + this.brh.metricsHandler.metricChatMessagesSent);
                sign.setLine(3, "");
                sign.update();
                break;
        }

    }

    public int getNumberOfSigns(){
        return signs.size();
    }

    public void registerSign(String type, SignChangeEvent event) {
        Block signBlock = event.getBlock();

        signs.add(new SignEntry(
                type,
                signBlock.getLocation().getBlockX(),
                signBlock.getLocation().getBlockY(),
                signBlock.getLocation().getBlockZ(),
                signs.size()
        ));

//        int idx = config.getInt("sign_count", 0) + 1;
//
//        config.setProperty("signs." + idx + ".type", type);
//        config.setProperty("signs." + idx + ".x", signBlock.getLocation().getBlockX());
//        config.setProperty("signs." + idx + ".y", signBlock.getLocation().getBlockY());
//        config.setProperty("signs." + idx + ".z", signBlock.getLocation().getBlockZ());
//        config.setProperty("sign_count", idx);
//        config.save();

        updateSavedSignsInConfig();
    }

    public void updateSavedSignsInConfig() {
        for(SignEntry signEntry : signs) {
            config.setProperty("signs." + signEntry.idx + ".type", signEntry.type);
            config.setProperty("signs." + signEntry.idx + ".x", signEntry.x);
            config.setProperty("signs." + signEntry.idx + ".y", signEntry.y);
            config.setProperty("signs." + signEntry.idx + ".z", signEntry.z);
            config.setProperty("sign_count", signs.size());
        }
        config.save();
    }
}

class SignEntry {
    String type;
    int x;
    int y;
    int z;
    int idx;

    public SignEntry(String type, int x, int y, int z, int idx) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.idx = idx;
    }
}
