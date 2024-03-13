package pw.stellaric.BetaReduxHelper.util;

import org.bukkit.World;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

import java.time.Instant;

public class DaylightCycleUtils {
    BetaReduxHelper brh;

    public long timesShifted = 0; // debug
    public boolean cancel = false;
    public long lastDayStartEpochSeconds = 0;
    boolean subtractNextTick = false;
    public boolean showTimeNotices = false;

    public DaylightCycleUtils(BetaReduxHelper plugin) {
        this.brh = plugin;

        // 24000 ticks per day / 20 minutes
        // 1500 / 1.25m
        // 12000 ticks per day/night
        // 1200 seconds
        // 20 minutes per day
        // we want to

        int minute = (int) 1200L;
        // 20L = 1 second, so 1200L = 60 seconds, or one minute
        // the 0L is the delay before the timer starts
        // the minute * 5 is 1200L or one minute * 5, which is the interval that this repeating task repeats at
        this.brh.getServer().getScheduler().scheduleSyncRepeatingTask(this.brh, new Runnable() {
            public void run() {
                shiftTime();
            }
        }, 0, 1L); // (long) this.announcementIntervalMinutes * minute); // minute * 5
    }

    public void shiftTime() {
        World world = this.brh.getServer().getWorld("world");
        long currentWorldTime = world.getTime();
//        this.brh.getServer().broadcastMessage("" + currentWorldTime);

        if (showTimeNotices) {
            if (currentWorldTime % 6000 == 0 || currentWorldTime == 0) {
                long nowEpochSeconds = Instant.now().getEpochSecond();
                if (lastDayStartEpochSeconds != 0) {
//                String agoTime = TimeAgo.toDuration();
                    this.brh.getServer().broadcastMessage("it's " + currentWorldTime + " and it has been " + (nowEpochSeconds - lastDayStartEpochSeconds) + " seconds since last time notice");
                }
                lastDayStartEpochSeconds = nowEpochSeconds;
            }
        }

        // handle sleeping
        if(cancel) {
            world.setTime(1);
            cancel = false;
            return;
        }

        // do nothing when time is going to loop
        if (currentWorldTime == 24000) {
            return;
        }

        if (subtractNextTick){
            world.setTime(currentWorldTime - 1);
            subtractNextTick = false;
        } else {
            subtractNextTick = true;
        }
    }

}
