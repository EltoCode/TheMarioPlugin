package org.elton.plugin.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player picks up a powerup
 */
public class MarioPowerupEvent extends PlayerEvent{

    private final int powerupID;
    private static final HandlerList handlers = new HandlerList();

    public MarioPowerupEvent(Player player, int powerupID) {
        super(player);
        this.powerupID = powerupID;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the powerup ID of the item picked up
     * @return the powerup ID of the item picked up
     */
    public int getPowerupID() {
        return powerupID;
    }
}