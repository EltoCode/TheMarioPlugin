package org.elton.plugin.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.elton.plugin.Entities.CherryDouble;
import org.elton.plugin.MyUtils.MyNamespacedKeys;
import org.elton.plugin.MyUtils.VectorUtils;

/**
 * A Utility Item class for the custom item Double Cherry
 *
 */
public class DoubleCherryItem {

    /**
     * Creates a returns a new instance of a fire flower item
     * @return a new instance of a fire flower item
     */
    public static ItemStack getItemStack() {

        ItemStack i = new ItemStack(Material.SWEET_BERRIES);
        ItemMeta iM = i.getItemMeta();

        String name = "&5&lDouble Cherry";
        iM.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        iM.getPersistentDataContainer().set(MyNamespacedKeys.getPowerupID(), PersistentDataType.INTEGER, 2);
        i.setItemMeta(iM);

        return i;
    }

    /**
     * Checks if the provided String matches the name of this item, case-insensitive
     * @param check The string to be checked
     * @return true if the provided String matches the name of this item, case-insensitive
     */
    public static boolean isNameMatch(String check) {
        return check.equalsIgnoreCase("double_cherry");
    }

    /**
     * Handles a player powering up with this item
     * @param p The player who needs to power up
     */
    public static void powerUp(Player p) {

        //gets the player's data container
        PersistentDataContainer data = p.getPersistentDataContainer();
        //gets the duplicate number
        int dupeNum = data.get(MyNamespacedKeys.getDuplicateNumber(), PersistentDataType.INTEGER);

        //if dupes more than 4, then send a different message
        if(dupeNum > 3) {
            p.sendMessage(ChatColor.DARK_PURPLE + "You collected another cherry! But you are at max doubles");
            return;
        }

        //handles getting which offset the double should use
        Vector offset = VectorUtils.getCherryOffset(dupeNum);

        //if this is the first duplicate, then send a different message
        if(dupeNum == 0) {
            p.sendMessage(ChatColor.DARK_PURPLE + "You are now Double Cherry Mario. Collect more cherries for more doubles");
        }
        else {  //this means dupe number is 1-3
            p.sendMessage(ChatColor.DARK_PURPLE + "You collected another cherry! You got a new duplicate!");
        }
        //increments the dupeNum for player
        data.set(MyNamespacedKeys.getDuplicateNumber(), PersistentDataType.INTEGER, dupeNum+1);
        //summons a dupe
        CherryDouble.spawnCherryDouble(p, offset);
        //some particles
        p.spawnParticle(Particle.NOTE, p.getLocation(), 50, 1, 1, 1);
    }
}
