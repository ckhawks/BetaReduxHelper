package pw.stellaric.BetaReduxHelper;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.PluginManager;
import pw.stellaric.BetaReduxHelper.util.SignUtils;

import static org.bukkit.Bukkit.getServer;

public class BlockListen extends BlockListener implements Listener {

    BetaReduxHelper brh;

    SignUtils signUtils;

    public BlockListen(BetaReduxHelper plugin, SignUtils signUtils) {
        this.brh = plugin;
        this.signUtils = signUtils;
    }

    public void registerEvents(BetaReduxHelper plugin) {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_BREAK, this, Event.Priority.Lowest, plugin);
        pm.registerEvent(Event.Type.BLOCK_PLACE, this, Event.Priority.Lowest, plugin);
        pm.registerEvent(Event.Type.SIGN_CHANGE, this, Event.Priority.Normal, plugin);

        // REGISTER SCHEDULERS
        // 20L = 1 second, so 1200L = 60 seconds, or one minute
        // the 0L is the delay before the timer starts
        // the minute * 5 is 1200L or one minute * 5, which is the interval that this repeating task repeats at
        int minute = (int) 1200L;
        this.brh.getServer().getScheduler().scheduleSyncRepeatingTask(this.brh, new Runnable() {
            public void run() {
                callUpdateSigns();
            }
        }, minute * 2, minute * 5); // minute * 5
    }

    // stupid hack because the runnable doesn't know what `this` is
    public void callUpdateSigns() {
        this.signUtils.updateSigns();
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        this.brh.metricsHandler.metricBlocksBroken++;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        this.brh.metricsHandler.metricBlocksPlaced++;
    }

    @Override
    public void onSignChange(SignChangeEvent event) {
        this.signUtils.handleOnSignChangeEvent(event);
    }
}


