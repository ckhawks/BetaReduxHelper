package pw.stellaric.BetaReduxHelper.util;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

public class SchedulerTask implements Runnable {
    private int taskId;
    BetaReduxHelper brh;
    Player player;
    BukkitScheduler scheduler;
    int runTimes;
    public SchedulerTask(BetaReduxHelper brh, Player player, BukkitScheduler scheduler, int runTimes) {
        this.brh = brh;
        this.player = player;
        this.scheduler = scheduler;
        this.runTimes = runTimes;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    int count = 0;
    @Override
    public void run() {
        playEffectAtLocation(player.getLocation(), Effect.SMOKE);

        if(count >= runTimes){
            scheduler.cancelTask(taskId);
        }
        count++;
    }

    public void playEffectAtLocation(Location location, Effect effect) {
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 0, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 1, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 2, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 3, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 4, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 5, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 6, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 7, 10);
        this.brh.getServer().getWorlds().get(0).playEffect(location, effect, 8, 10);
    }

}
