package pw.stellaric.BetaReduxHelper.util;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class RestartUtils {

    BetaReduxHelper brh;

    // 20L = 1 second, so 1200L = 60 seconds, or one minute
    // the 0L is the delay before the timer starts
    // the minute * 5 is 1200L or one minute * 5, which is the interval that this repeating task repeats at
    int minute = (int) 1200L;

    public RestartUtils(BetaReduxHelper plugin){
        this.brh = plugin;
        scheduleRestartTasks();
    }

    public void scheduleRestartTasks() {
        long ticks1 = getTicksUntilHourMinutes(15, 30);
        this.brh.log("Restart countdown in " + ticks1 + " ticks");
        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                beginRestart30Minutes();
            }
        }, ticks1);

        long ticks2 = getTicksUntilHourMinutes(7, 30);
        this.brh.log("Restart countdown in " + ticks2 + " ticks");
        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                beginRestart30Minutes();
            }
        }, ticks2);

        long ticks3 = getTicksUntilHourMinutes(23, 30);
        this.brh.log("Restart countdown in " + ticks3 + " ticks");
        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                beginRestart30Minutes();
            }
        }, ticks3);
    }

    private static long getTicksUntilHourMinutes(int hourOfDay, int minutesOfHour) {
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        if(cal.get(Calendar.HOUR_OF_DAY) >= hourOfDay || cal.get(Calendar.HOUR_OF_DAY) == hourOfDay && cal.get(Calendar.MINUTE) >= minutesOfHour)
            cal.add(Calendar.DATE, 1);  //do it tomorrow if now is after 7:00 pm
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minutesOfHour);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long offset = cal.getTimeInMillis() - now;
        return offset / 50; // each tick is 50ms
    }

    public void beginRestart30Minutes() {
        sendServerMessage("Server will restart in " + ChatColor.GOLD + "30 minutes" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart10Minutes();
            }
        }, minute * 20L);
    }

    public void restart10Minutes() {
        sendServerMessage("Server will restart in " + ChatColor.YELLOW + "10 minutes" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart5Minutes();
            }
        }, minute * 5L);
    }

    public void restart5Minutes() {
        sendServerMessage("Server will restart in " + ChatColor.YELLOW + "5 minutes" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart2Minutes();
            }
        }, minute * 3L);
    }
    public void restart2Minutes() {
        sendServerMessage("Server will restart in " + ChatColor.RED + "2 minutes" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart1Minute();
            }
        }, (long) minute);
    }

    public void restart1Minute() {
        sendServerMessage("Server will restart in " + ChatColor.RED + "1 minute" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart3Seconds();
            }
        }, (long) minute);
    }

    public void restart3Seconds() {
        sendServerMessage("Server will restart in " + ChatColor.RED + "3 seconds" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart2Seconds();
            }
        }, (long) minute / 60);
    }

    public void restart2Seconds() {
        sendServerMessage("Server will restart in " + ChatColor.RED + "2 seconds" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                restart1Second();
            }
        }, (long) minute / 60);
    }

    public void restart1Second() {
        sendServerMessage("Server will restart in " + ChatColor.RED + "1 second" + ChatColor.WHITE + "!");

        this.brh.getServer().getScheduler().scheduleSyncDelayedTask(this.brh, new Runnable() {
            public void run() {
                shutdownServer();
            }
        }, (long) minute / 60);
    }

    public void shutdownServer() {
        kickAllPlayers();
        this.brh.getServer().dispatchCommand(new ConsoleCommandSender(this.brh.getServer()), "stop");
    }

    public void sendServerMessage(String message) {
        this.brh.getServer().broadcastMessage(ChatColor.YELLOW + "SERVER: " + ChatColor.WHITE + message);
        this.brh.log(ChatColor.YELLOW + "SERVER: " + ChatColor.WHITE + message);
    }

    public void kickAllPlayers() {
        Player[] players = this.brh.getServer().getOnlinePlayers();

        for (Player player : players) {
            player.kickPlayer("Server is restarting! Stand up and join back in a minute :)");
        }
    }
}
