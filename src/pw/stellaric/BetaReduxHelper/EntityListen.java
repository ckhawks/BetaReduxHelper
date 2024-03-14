package pw.stellaric.BetaReduxHelper;

import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

import static org.bukkit.Bukkit.getServer;

public class EntityListen extends EntityListener implements Listener {

    BetaReduxHelper brh;

    public EntityListen(BetaReduxHelper brh) {
        this.brh = brh;
    }

    public void registerEvents(BetaReduxHelper plugin) {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, this, Event.Priority.Lowest, plugin);
    }

    @Override
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if(!(entityDamageEvent.getEntity() instanceof LivingEntity)) {
//            this.brh.log("!instanceof LivingEntity");
            return;
        }

        LivingEntity dyingLivingEntity = (LivingEntity) entityDamageEvent.getEntity();

        // this is included to prevent doing damage to dead mobs and it counting twice

//        this.brh.log("isDead? " + dyingLivingEntity.isDead());
        if(!dyingLivingEntity.isDead()){
//            this.brh.log("isDead");
            return;
        }

        // if player health is still above 0 after damage is done
//        this.brh.log("!deathhealth " + dyingLivingEntity.getHealth() + ", " + entityDamageEvent.getDamage());
        if(dyingLivingEntity.getHealth() - entityDamageEvent.getDamage() > 0){
//            this.brh.log("return");
            return;
        }

        if(entityDamageEvent instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) entityDamageEvent;
//            this.brh.log("entity took damage");

//            this.brh.log("entityDamageEvent" + entityDamageEvent.toString());
            Entity killerEntity = event.getDamager();
//            this.brh.log("killerEntity" + killerEntity.toString());


            // Player killed
            if(killerEntity instanceof Player) {

                // Player killed monster
                if(dyingLivingEntity instanceof Monster) {
                    this.brh.metricsHandler.metricMobsKilledHostile++;
//                    this.brh.log("Player killed monster");
                    return;
                }

                // Player killed animal
                if(dyingLivingEntity instanceof Animals) {
                    this.brh.metricsHandler.metricMobsKilledPassive++;
//                    this.brh.log("Player killed animal");
                    return;
                }

                // Player killed player
                if(dyingLivingEntity instanceof Player) {
                    this.brh.metricsHandler.metricPlayerDeathsPvp++;
//                    this.brh.log("Player killed player");
                    return;
                }
                return;
            }
        }

        // Player died to
        if(dyingLivingEntity instanceof Player) {
            this.brh.metricsHandler.metricPlayerDeaths++;
//            this.brh.log("Player died");

            // Player died to lava
            if(entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                this.brh.metricsHandler.metricPlayerDeathsLava++;
                this.brh.log("Player died to lava");
                return;
            }

            // Player died to fall
            if(entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
                this.brh.metricsHandler.metricPlayerDeathsFall++;
//                this.brh.log("Player died to fall");
                return;
            }
        }
    }
}
