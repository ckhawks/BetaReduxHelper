package pw.stellaric.BetaReduxHelper;

import org.bukkit.util.config.Configuration;

public class MetricsHandler {
    BetaReduxHelper brh;
    Configuration config;

    public int metricBlocksBroken;
    public int metricBlocksPlaced;

    public int metricMobsKilledHostile;
    public int metricMobsKilledPassive;
    public int metricPlayerDeaths;
    public int metricPlayerDeathsLava;
    public int metricPlayerDeathsPvp;
    public int metricPlayerDeathsFall;
    public int metricMinutesPlayed;
    public int metricChatMessagesSent;

    public MetricsHandler(BetaReduxHelper plugin) {
        this.brh = plugin;
        this.config = this.brh.getConfiguration();
        loadExistingMetrics();
    }

    public void registerEvents() {
        int minute = (int) 1200L;
        // 20L = 1 second, so 1200L = 60 seconds, or one minute
        // the 0L is the delay before the timer starts
        // the minute * 5 is 1200L or one minute * 5, which is the interval that this repeating task repeats at

        this.brh.getServer().getScheduler().scheduleSyncRepeatingTask(this.brh, new Runnable() {
            public void run() {
                updateSavedMetrics();
            }
        }, minute * 2, minute * 5); // minute * 5

        this.brh.getServer().getScheduler().scheduleSyncRepeatingTask(this.brh, new Runnable() {
            public void run() {
                updateMinutesPlayed();
            }
        }, 0, minute); // minute * 5
    }

    public void onDisable(){
        updateSavedMetrics();
    }

    public void loadExistingMetrics() {
        this.metricBlocksBroken = config.getInt("blocks_broken", 0);
        this.metricBlocksPlaced = config.getInt("blocks_placed", 0);
        this.metricMobsKilledHostile = config.getInt("hostile_mobs_killed", 0);
        this.metricMobsKilledPassive = config.getInt("passive_mobs_killed", 0);
        this.metricPlayerDeaths = config.getInt("player_deaths", 0);
        this.metricPlayerDeathsLava = config.getInt("player_deaths_lava", 0);
        this.metricPlayerDeathsPvp = config.getInt("player_deaths_pvp", 0);
        this.metricPlayerDeathsFall = config.getInt("player_deaths_fall", 0);
        this.metricMinutesPlayed = config.getInt("minutes_played", 0);
        this.metricChatMessagesSent = config.getInt("messages_sent", 0);
    }

    public void updateSavedMetrics() {
        this.brh.log("Saving metric values...", true);

        config.setProperty("blocks_broken", this.metricBlocksBroken);
        config.setProperty("blocks_placed", this.metricBlocksPlaced);
        config.setProperty("hostile_mobs_killed", this.metricMobsKilledHostile);
        config.setProperty("passive_mobs_killed", this.metricMobsKilledPassive);
        config.setProperty("player_deaths", this.metricPlayerDeaths);
        config.setProperty("player_deaths_lava", this.metricPlayerDeathsLava);
        config.setProperty("player_deaths_pvp", this.metricPlayerDeathsPvp);
        config.setProperty("player_deaths_fall", this.metricPlayerDeathsFall);
        config.setProperty("minutes_played", this.metricMinutesPlayed);
        config.setProperty("messages_sent", this.metricChatMessagesSent);

        config.save();
        this.brh.log("Saved!", true);
    }

    public void updateMinutesPlayed(){
        int numPlayersOnline = this.brh.getServer().getOnlinePlayers().length;

        this.metricMinutesPlayed += numPlayersOnline;
        this.brh.log(numPlayersOnline + " players online, new value " + this.metricMinutesPlayed, true);
    }
}
