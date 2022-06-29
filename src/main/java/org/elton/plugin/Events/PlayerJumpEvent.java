package org.elton.plugin.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player jumps. Lasts till the player is on the ground again
 */
public class PlayerJumpEvent extends PlayerEvent{
    private static final HandlerList handlers = new HandlerList();

    public PlayerJumpEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}