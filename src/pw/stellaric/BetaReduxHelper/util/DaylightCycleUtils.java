package pw.stellaric.BetaReduxHelper.util;

import org.bukkit.World;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

import java.time.Instant;

public class DaylightCycleUtils {

    public final static long DAYTIME = 700; // 0
    public final static long SUNSET = 11500;
    public final static long NIGHTTIME = 13700;
    public final static long SUNRISE = 21900;

    private final static int DESIRED_NIGHTS = 2;
    private int nightsCompleted = 0;
    private int daytimesCompleted = 0;
    private final static int DESIRED_DAYS = 2;

    private final static long DURATION_DAY   = SUNSET  - DAYTIME; // 11500 - 700 = 10800
    private final static long DURATION_NIGHT = SUNRISE - NIGHTTIME; // 21900 - 13700 =  8200

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
        }, 0, 100L); // (long) this.announcementIntervalMinutes * minute); // minute * 5
    }

    // if nightsCompleted == DESIRED_NIGHTS

    public boolean isDay() {
        World world = this.brh.getServer().getWorld("world");
        long time = world.getTime();
        if (time >= SUNRISE || time < SUNSET) {
            return true;
        }
        return false;
    }

    // ran every 100 ticks (5 seconds)
    public void shiftTime() {
        World world = this.brh.getServer().getWorld("world");
        long currentWorldTime = world.getTime();

        this.brh.getServer().broadcastMessage("time: " + currentWorldTime + ", nights done: " + nightsCompleted + ", days done: " + daytimesCompleted);

        if (isDay()) {
            // if it is day time
            if (currentWorldTime > (SUNSET - 200)) {
                this.brh.getServer().broadcastMessage("h1");
                daytimesCompleted++;

                if (daytimesCompleted >= DESIRED_DAYS) {
                    // continue into sunset, don't change the time
                    this.brh.log("The day is ending...", true);
                    daytimesCompleted = 0;
                } else {
                    world.setTime(DAYTIME);
                }
            }


        } else {
            // if it is nighttime
            if (currentWorldTime > (SUNRISE - 200)) {
                this.brh.getServer().broadcastMessage("h2");
                nightsCompleted++;

                if (nightsCompleted >= DESIRED_NIGHTS) {
                    // continue into sunrise, don't change the time
                    this.brh.log("The night is ending...", true);
                    nightsCompleted = 0;
                } else {
                    world.setTime(NIGHTTIME);
                }
            }


        }
    }

    public void shiftTimeOld() {
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
            world.setFullTime(world.getFullTime() + 1);
            subtractNextTick = false;
        } else {
            subtractNextTick = true;
        }
    }

}
