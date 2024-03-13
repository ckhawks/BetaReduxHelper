package pw.stellaric.BetaReduxHelper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class PlayerListen extends PlayerListener implements Listener {

    public Set<Player> sleepingPlayers;

    public float requiredPercentageSleepingToPassDay = 0.6F;

    BetaReduxHelper brh;

    public PlayerListen(BetaReduxHelper plugin){
        this.brh = plugin;
        this.sleepingPlayers = new HashSet<>();
    }

    public void registerEvents(BetaReduxHelper plugin) {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, this, Event.Priority.Normal, plugin);
//        pm.registerEvent(Event.Type.PLAYER_LOGIN, this, Event.Priority.Normal, plugin);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this, Event.Priority.Normal, plugin);
//        pm.registerEvent(Event.Type.PLAYER_INTERACT, this, Priority.Normal, plugin);
//        pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, this, Priority.Normal,
//                plugin);
        pm.registerEvent(Event.Type.PLAYER_CHAT, this, Event.Priority.High, plugin);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this, Event.Priority.High, plugin);
        pm.registerEvent(Event.Type.PLAYER_BED_ENTER, this, Event.Priority.Normal, plugin);
        pm.registerEvent(Event.Type.PLAYER_BED_LEAVE, this, Event.Priority.Normal, plugin);
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String prefix = getPrefix(player);
        if(prefix == null){
            prefix = "";
        }
        prefix = prefix.replaceAll("(&([a-f0-9]))", "§$2");
        String message = ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + ChatColor.WHITE + prefix + player.getDisplayName();
        String messageNoColors = "[+] " + prefix + player.getDisplayName();


        // Add suffix if file does not exist
        File file = new File("world/players/" + name + ".dat");
        boolean exists = file.exists();
        if (!exists) {
            message += ChatColor.WHITE + " - They're new!";
            messageNoColors += " - They're new!";
        }

        event.setJoinMessage(message);
        Logger.getLogger("Minecraft").log(Level.INFO, messageNoColors);
        brh.recentCommand.removeLoggedOffPlayerFromListIfNeeded(player);

//        if (bColors.getInstance().playerColor.containsKey(player.getName())) {
//            ChatColor color = bColors.getInstance().playerColor.get(player.getName());
//            event.setFormat("§f<" + color + player.getName() + "§f> " + event.getMessage());
//        }
    }
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String prefix = getPrefix(player);
        if(prefix == null){
            prefix = "";
        }
        prefix = prefix.replaceAll("(&([a-f0-9]))", "§$2");

        String message = ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + ChatColor.WHITE + prefix + player.getDisplayName();
        String messageNoColors = "[-] " + prefix + player.getDisplayName();

        event.setQuitMessage(message);
        Logger.getLogger("Minecraft").log(Level.INFO, messageNoColors);
        brh.recentCommand.addLoggedOffPlayerToList(event.getPlayer());
    }

    @Override
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        sleepingPlayers.add(event.getPlayer());
        getServer().broadcastMessage(event.getPlayer().getDisplayName() + ChatColor.YELLOW + " is going to bed... " + getSleepingPlayersCount());
        this.brh.log(event.getPlayer().getDisplayName() + ChatColor.YELLOW + " is going to bed... " + getSleepingPlayersCount());

        getServer().getScheduler().scheduleSyncDelayedTask(this.brh, (Runnable) this::checkSleepingCount, (long) 20 * 5);
    }

    @Override
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        this.brh.log(event.getPlayer().getDisplayName() + " left bed at " + event.getBed().getLocation());
        sleepingPlayers.remove(event.getPlayer());
        resetSleepingIgnoredAll(); // this will fire multiple times so optimize if we get 100 players online

        long time = getServer().getWorlds().get(0).getTime();
        if (time >= 0 && time < 200) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Good morning!");
            this.brh.log("Good morning!");
            sleepingPlayers.clear();
        } else {
            getServer().broadcastMessage(event.getPlayer().getDisplayName() + ChatColor.YELLOW + " left their bed. " + getSleepingPlayersCount());
            this.brh.log(event.getPlayer().getDisplayName() + ChatColor.YELLOW + " left their bed. " + getSleepingPlayersCount());
        }
    }

    public String getSleepingPlayersCount() {
        return "(" + sleepingPlayers.size() + "/" + getRequiredNumberOfSleepingPlayers() + ")";
    }

    public int getRequiredNumberOfSleepingPlayers() {
//        getServer().broadcastMessage("float " + requiredPercentageSleepingToPassDay * getServer().getWorlds().get(0).getPlayers().size());
//        getServer().broadcastMessage("ceil " + Math.ceil(requiredPercentageSleepingToPassDay * getServer().getWorlds().get(0).getPlayers().size()));
//        getServer().broadcastMessage("return " + (int) Math.ceil(requiredPercentageSleepingToPassDay * getServer().getWorlds().get(0).getPlayers().size()));
        return (int) Math.ceil(requiredPercentageSleepingToPassDay * getServer().getWorlds().get(0).getPlayers().size());
    }

    public void checkSleepingCount() {

        float percentagePlayersSleeping = (float) sleepingPlayers.size() / getServer().getWorlds().get(0).getPlayers().size();
//        int requiredCount = getServer().getWorlds().get(0).getPlayers().size() / 2;
//        getServer().broadcastMessage("sleepingPlayers.size(): " + sleepingPlayers.size() + "percentagePlayersSleeping " + percentagePlayersSleeping);

        if(percentagePlayersSleeping >= requiredPercentageSleepingToPassDay) {
            for (Player p : getServer().getWorlds().get(0).getPlayers()) {

                // funky hack to make it so people who aren't sleeping don't matter
                if (!p.isSleeping()) {
                    p.setSleepingIgnored(true);
                    this.brh.daylightCycleUtils.cancel = true;
                }
            }
        }
    }

    public void resetSleepingIgnoredAll() {
        for (Player p : getServer().getWorlds().get(0).getPlayers()) {
            p.setSleepingIgnored(false);
        }
    }


    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
//        this.brh.log("chat event " + event);
//        this.brh.log("permission plugins " + event.getPlayer().hasPermission("betaredux.plugins"));

        // Restrict /plugins
        if (!event.getPlayer().hasPermission("betaredux.plugins") && (event.getMessage().startsWith("/plugins") || event.getMessage().startsWith("/pl"))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Sorry, you don't have permission.");
        }

        // Restrict /version
        if (!event.getPlayer().hasPermission("betaredux.version") && (event.getMessage().startsWith("/version") || event.getMessage().startsWith("/ver"))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Sorry, you don't have permission.");
        }

//        // when someone uses /help command, change it to /me is stupid!
//
//        // split the command
//        String[] args = event.getMessage().split(" ");
//
//        if(args.length>0 && args[0].compareToIgnoreCase("/help")==0){
//
//            // this doesn't work like it should
//            //event.setMessage("/me is stupid!");
//
//            // use this instead:
//            event.setCancelled(true);
//            event.getPlayer().chat("/me is stupid!");
//
//            System.out.println("lol he just made a fool out of himself");
//
//        }

    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if(event.getMessage().equalsIgnoreCase("dayinfo")) {
            event.getPlayer().sendMessage("current time: " + event.getPlayer().getWorld().getTime() + ", times shifted: " + this.brh.daylightCycleUtils.timesShifted);
        }

        if(event.getMessage().equalsIgnoreCase("showtimenotices")) {
            this.brh.daylightCycleUtils.showTimeNotices = true;
            this.brh.getServer().broadcastMessage("time notices are enabled");
        }

        if(event.getMessage().equalsIgnoreCase("hidetimenotices")) {
            this.brh.daylightCycleUtils.showTimeNotices = true;
            this.brh.getServer().broadcastMessage("time notices are disabled");
        }

        this.brh.metricsHandler.metricChatMessagesSent++;

        // Chat formatting
        if (this.brh.permissions != null) {
            if (!event.isCancelled()) {
                Player p = event.getPlayer();
                String msg = event.getMessage();
                // not doing this because the format dieded when people put a % in chat
                String format = (parseChat(p, msg) + " ").replaceAll("%", "%%");
                event.setFormat(format);
//                event.setMessage(parseChat(p, msg);
//                this.brh.log("Format: " + format);


            } else {
                this.brh.log("It was cancelled already");
            }
        } else {
            this.brh.log("No PermissionsEx");
        }

    }

    public String parseChat(Player p, String msg) {
        String prefix = this.getPrefix(p);
        String prefixOwn = this.getOwnPrefix(p);
//        String prefixGroup = this.getGroupPrefix(p);
        String suffix = this.getSuffix(p);
        String suffixOwn = this.getOwnSuffix(p);
        String group = this.getGroup(p);
        if (prefix == null) {
            prefix = "";
        }

        if (suffix == null) {
            suffix = "";
        }

        if (prefixOwn == null) {
            prefixOwn = "";
        }

        if (suffixOwn == null) {
            suffixOwn = "";
        }

        if (group == null) {
            group = "";
        }

//        String output = prefix + prefixOwn + p.getDisplayName() + suffixOwn + suffix + " " + msg;
        String output = prefix + p.getDisplayName() + suffix + " " + msg;
        output = output.replaceAll("(&([a-f0-9]))", "§$2");

        return output;
    }



    public String getPrefix(Player player) {
        if (this.brh.permissions != null) {
            return this.brh.permissions.getUser(player).getPrefix(player.getWorld().getName());
        } else {
            this.brh.log("SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

    public String getOwnPrefix(Player player) {
        if (this.brh.permissions != null) {
            return this.brh.permissions.getUser(player).getOwnPrefix(player.getWorld().getName());
        } else {
            this.brh.log("SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

    public String getSuffix(Player player) {
        if (this.brh.permissions != null) {
            return this.brh.permissions.getUser(player).getSuffix(player.getWorld().getName());
        } else {
            this.brh.log("SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

    public String getOwnSuffix(Player player) {
        if (this.brh.permissions != null) {
            return this.brh.permissions.getUser(player).getOwnSuffix(player.getWorld().getName());
        } else {
            this.brh.log("SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

    public String getGroupPrefix(String group, String worldname) {
        if (this.brh.permissions != null) {
            return this.brh.permissions.getGroup(group).getPrefix(worldname);
        } else {
            this.brh.log("SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

    public String getGroupSuffix(String group, String worldname) {
        if (this.brh.permissions != null) {
            return this.brh.permissions.getGroup(group).getSuffix(worldname);
        } else {
            this.brh.log("[PExChat::getSuffix] SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

    public String getGroup(Player player) {
        if (this.brh.permissions != null) {
            String[] groups = this.brh.permissions.getUser(player).getGroupsNames(player.getWorld().getName());
            return groups[0];
        } else {
            this.brh.log("SEVERE: There is no Permissions module, why are we running?!??!?");
            return null;
        }
    }

//    @Override
//    public void onPlayerChat(PlayerChatEvent event) {
//        ConversationUtils.onChat(event);
//    }
}
