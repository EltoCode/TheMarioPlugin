package org.elton.plugin.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.elton.plugin.Events.PlayerJumpEvent;

/**
 * Used to call events from any statistics. We're using it for the jump statistic
 */
public class StatisticListener implements Listener {

    @EventHandler
    public void onStatIncrement(PlayerStatisticIncrementEvent e) {
        //for jump increments
        if(e.getStatistic() == Statistic.JUMP) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerJumpEvent(e.getPlayer()));
        }

    }
}
