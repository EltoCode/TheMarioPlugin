package org.elton.plugin.Tasks;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.elton.plugin.Entities.MarioFireball;

/**
 * Bukkit runnable that executes fireball behaviour
 */
public class FireballTask extends BukkitRunnable {

    //The fireball that "owns" this tasks
    MarioFireball fireballM;

    /**
     * Default Constructor
     * @param fireballM The Fireball that "owns" this task
     */
    public FireballTask(MarioFireball fireballM) {
        this.fireballM = fireballM;
    }

    @Override
    public void run() {
        //cancels the task if fireball is dead
        if(fireballM.isDead())
            this.cancel();

        //if on ground, adds a bounce
        if(fireballM.isOnGround()){
            //puts the initVel into a var
            Vector initVel1 = fireballM.getInitVel();
            fireballM.setBounce(initVel1.setY(0.4F));
        }

        //remove if in fire or water
        Material mat = fireballM.getLocation().getBlock().getType();
        if(mat == Material.WATER || mat == Material.LAVA) {fireballM.remove();}

        //Collision Checking//
        fireballM.collisionCheck();

        //Damage Checking//
        fireballM.damageCheck();

        //summons some particles
        fireballM.summonParticles();
    }

}
