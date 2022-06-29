package org.elton.plugin.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.elton.plugin.Entities.CherryDouble;
import org.elton.plugin.MyUtils.MyNamespacedKeys;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.MyUtils.VectorUtils;
import org.elton.plugin.TheMarioPlugin;

import java.util.ArrayList;

/**
 * Used to Listen for Player Joins, Handles anything a player might need
 */
public class PlayerJoinListener implements Listener {

    //main plugin ref
    private final TheMarioPlugin plugin;
    public PlayerJoinListener(TheMarioPlugin plugin) {this.plugin = plugin;}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        //gets the player
        Player player = e.getPlayer();

        //if player not intited for mario plugin, intis them
        if(!(MyUtils.isMarioInit(player))) {
            MyUtils.marioInit(player);
        }


        //gives a player his dupes back
        //reload failsafe
        if(!MyUtils.getPlayerDupesMap().containsKey(player.getName())) {
            //expects an empty arraylist on player join anyway
            MyUtils.getPlayerDupesMap().put(player.getName(), new ArrayList<>());
        }
        //gets the number of clones a player should have
        int dupeNum = player.getPersistentDataContainer()
                .get(MyNamespacedKeys.getDuplicateNumber(), PersistentDataType.INTEGER);
        //runs a loop giving these dupes
        for(int i = 0; i<dupeNum; i++){
            //handles getting which offset the double should use
            Vector offset = VectorUtils.getCherryOffset(i);
            CherryDouble.spawnCherryDouble(player,offset);
        }
    }
}
