package org.elton.plugin.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.elton.plugin.Events.MarioPowerupEvent;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.TheMarioPlugin;

/**
 * Listens for a player powering up
 */
public class PowerupListener implements Listener {

    //main plugin ref
    public final TheMarioPlugin plugin;
    public PowerupListener(TheMarioPlugin plugin) {this.plugin = plugin;}

    @EventHandler
    public void onPowerup(MarioPowerupEvent e){

        Player player = e.getPlayer();
        MyUtils.setPlayerPowerup(player, e.getPowerupID());
        //Handles powerup change
        MyUtils.handlePowerUpChange(player, e.getPowerupID());
    }
}
