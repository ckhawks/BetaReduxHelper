package pw.stellaric.BetaReduxHelper;

import com.avaje.ebean.Update;
import lishid.openinv.commands.OpenInvPluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import pw.stellaric.BetaReduxHelper.commands.NextAnnouncementCommand;
import pw.stellaric.BetaReduxHelper.commands.RecentCommand;
import pw.stellaric.BetaReduxHelper.commands.RestartNowCommand;
import pw.stellaric.BetaReduxHelper.commands.UpdateSignsCommand;
import pw.stellaric.BetaReduxHelper.util.DaylightCycleUtils;
import pw.stellaric.BetaReduxHelper.util.RestartUtils;
import pw.stellaric.BetaReduxHelper.util.SignUtils;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class BetaReduxHelper extends JavaPlugin {
    public PermissionManager permissions = null;
    public MetricsHandler metricsHandler = null;
    public SignUtils signUtils;
    public RestartUtils restartUtils;
    public DaylightCycleUtils daylightCycleUtils;

    public Announcer announcer;
    BlockListen blockListen;
    EntityListen entityListen;
    PlayerListen playerListen;
    RecentCommand recentCommand;
    UpdateSignsCommand updateSignsCommand;
    NextAnnouncementCommand nextAnnouncementCommand;
    RestartNowCommand restartNowCommand;
    Configuration config;
    boolean debugMode;



//    public static void main(String[] args) {
//        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//        // to see how IntelliJ IDEA suggests fixing it.
//        System.out.printf("Hello and welcome!");
//
//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("i = " + i);
//        }
//    }


    @Override
    public void onEnable() {
        this.config = this.getConfiguration();
        this.signUtils = new SignUtils(this);
        this.restartUtils = new RestartUtils(this);
        this.daylightCycleUtils = new DaylightCycleUtils(this);

        // register commands
        recentCommand = new RecentCommand(this);
        this.getCommand("recent").setExecutor(recentCommand);
        updateSignsCommand = new UpdateSignsCommand(this);
        this.getCommand("updatesigns").setExecutor(updateSignsCommand);
        nextAnnouncementCommand = new NextAnnouncementCommand(this);
        this.getCommand("nextannouncement").setExecutor(nextAnnouncementCommand);
        restartNowCommand = new RestartNowCommand(this);
        this.getCommand("restartnow").setExecutor(restartNowCommand);

        // register miscellaneous handler classes
        metricsHandler = new MetricsHandler(this);
        metricsHandler.registerEvents();
        announcer = new Announcer(this);
        announcer.registerEvents();



        PluginManager pm = Bukkit.getServer().getPluginManager();

        // register events
        playerListen = new PlayerListen(this);
        playerListen.registerEvents(this);
        blockListen = new BlockListen(this, signUtils);
        blockListen.registerEvents(this);
        entityListen = new EntityListen(this);
        entityListen.registerEvents(this);

        if (pm.isPluginEnabled("PermissionsEx")) {
            this.permissions = PermissionsEx.getPermissionManager();


//            this.pm.registerEvent(Event.Type.PLAYER_CHAT, this.pListener, Event.Priority.Normal, this);
//            this.pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.pListener, Event.Priority.Normal, this);
//            this.console.sendMessage(this.getDescription().getName() + " (v" + this.getDescription().getVersion() + ") enabled");
        } else {
            log("PermissionsEx plugin not found or wrong version.");
        }

        // start auto-saver
        int minute = (int) 1200L;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                getServer().dispatchCommand(new ConsoleCommandSender(getServer()), "save-all");
            }
        }, minute * 10, minute * 10); // (long) this.announcementIntervalMinutes * minute); // minute * 5
//
////        pm.registerEvent(Event.Type.PLAYER_CHAT, new PlayerChat());
//        pm.registerEvent(Event.Type.PLAYER_JOIN, new PlayerListen(), Event.Priority.Highest, this);
//        pm.registerEvent(Event.Type.PLAYER_QUIT, new PlayerListen(), Event.Priority.Highest, this);
        log("Enabled.");
    }

    @Override
    public void onDisable() {
        signUtils.updateSavedSignsInConfig();
        metricsHandler.onDisable();
        log("Disabled.");
    }

    public void log(String message) {
        message = message.replaceAll("(§([a-f0-9]))", "");
        message = message.replaceAll("(&([a-f0-9]))", "");
        Logger.getLogger("Minecraft").log(Level.INFO, "[BetaReduxHelper] " + message);
    }

    public void log(String message, boolean debugMessage) {
        if(debugMessage){
            if(debugMode){
                log(message);
                return;
            }
        } else {
            log(message);
            return;
        }
    }




}