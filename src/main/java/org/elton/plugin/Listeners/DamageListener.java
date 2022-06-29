package org.elton.plugin.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.elton.plugin.MyUtils.MyNamespacedKeys;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.TheMarioPlugin;

/**
 * Class to Listen for Damage
 */
public class DamageListener implements Listener {

    //main plugin ref
    private final TheMarioPlugin plugin;
    public DamageListener(TheMarioPlugin plugin) {this.plugin = plugin;}

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        //if not player return
        if(!(e.getEntity() instanceof Player player))
            return;

        //we don't consider fall damage
        if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;

        //gets what powerup the player has
        int pwrID = MyUtils.getPlayerPowerup(player);

        //if the player has no powerup return
        if(pwrID == 0)
            return;
        //if the player is double cherry mario
        if(pwrID == 2) {
            //gets the number of clones the player has
            int dupeNum = player.getPersistentDataContainer()
                    .get(MyNamespacedKeys.getDuplicateNumber(), PersistentDataType.INTEGER);
            //if they have clones left, then remove a clone and return
            if(dupeNum > 0) {
                player.sendMessage(ChatColor.DARK_PURPLE + "You took damage! You lost a clone!");
                MyUtils.killCherryDouble(player);
                player.setNoDamageTicks(60);
                e.setDamage(0.0);
                return;
            }
        }

        //if code here it means he is a regular powerup and or has no clones left

        player.sendMessage(ChatColor.YELLOW + "You took damage! You lost your PowerUp!");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
        player.setNoDamageTicks(60);
        e.setDamage(0.0); //we don't cancel because we want the damage effect to play out
        MyUtils.setPlayerPowerup(player, 0); //sets pwoerup to 0
    }
}
