package org.elton.plugin.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;
import org.elton.plugin.Entities.MarioFireball;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.Tasks.FireballTask;
import org.elton.plugin.TheMarioPlugin;

public class SprintListener implements Listener {

    //main plugin ref
    private final TheMarioPlugin plugin;
    public SprintListener(TheMarioPlugin plugin) {this.plugin = plugin;}


    @EventHandler
    public void onSprint(PlayerToggleSprintEvent e) {

        //if sprinting is false return
        if(!e.isSprinting())
            return;

        /////////////////
        //Generic
        /////////////////
        //gets the player
        Player player = e.getPlayer();
        //gets the location in front of player
        Location inFront = MyUtils.getLocInFrontOfPlayer(player);
        //sets the initial velocity into a var for later calc
        //sets y to zero to prevent issues
        Vector initVel =  inFront.getDirection()
                .setY(0)
                .normalize()
                .add(new Vector(0F, 0.25F, 0F));


        ////////////
        //Fireball
        ///////////
        //if the player has the fire flower powerup
        if(MyUtils.getPlayerPowerup(player) == 1) {
            //summons a fireball infront of player
            MarioFireball fireballM = MarioFireball.spawnFireball(player, inFront, initVel);
            //runs a new fireball handling task
            /*new FireballTask(fireballM)
            .runTaskTimer(plugin, 1, 1);*/
        }

    }

}
