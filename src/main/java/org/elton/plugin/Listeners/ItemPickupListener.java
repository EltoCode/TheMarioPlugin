package org.elton.plugin.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.elton.plugin.Events.MarioPowerupEvent;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.TheMarioPlugin;

/**
 * Class to Listen for Item pickups, used to throw MarioPowerupEvent
 */
public class ItemPickupListener implements Listener {

    //main plugin ref
    private final TheMarioPlugin plugin;
    public ItemPickupListener(TheMarioPlugin plugin) {this.plugin = plugin;}

    @EventHandler
    public void OnItemPickup(EntityPickupItemEvent e) {

        //if not player returns
        if(e instanceof Player)
            return;
        //if not pow
        if(!MyUtils.isMarioPowerup(e.getItem()))
            return;


        //if code is here, then it means a player picked up a mario item
        //calls the even passing player and powerupID of item
        Bukkit.getPluginManager().callEvent(
                new MarioPowerupEvent((Player) e.getEntity(), MyUtils.getPowerupIDFromItem(e.getItem())));
    }

}
