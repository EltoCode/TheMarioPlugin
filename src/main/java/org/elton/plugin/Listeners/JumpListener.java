package org.elton.plugin.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.elton.plugin.Events.PlayerJumpEvent;
import org.elton.plugin.Tasks.MarioJumpTask;
import org.elton.plugin.TheMarioPlugin;

/**
 * Used to Listen for JumpEvents
 */
public class JumpListener implements Listener {

    //main plugin ref
    private final TheMarioPlugin plugin;
    public JumpListener(TheMarioPlugin plugin) {this.plugin = plugin;}

    @EventHandler
    public void onJump(PlayerJumpEvent e) {
        //gets the player
        Player player = e.getPlayer();
        //creates a new jump task to handle jumping checks
        MarioJumpTask jumpTask = new MarioJumpTask(plugin, player);
        jumpTask.runTaskTimer(plugin, 1L, 1L);

    }
}
