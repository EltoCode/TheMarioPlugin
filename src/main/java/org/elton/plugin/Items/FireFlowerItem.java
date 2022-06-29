package org.elton.plugin.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.elton.plugin.MyUtils.MyNamespacedKeys;
import org.elton.plugin.MyUtils.VectorUtils;

/**
 * A Utility Item class for the custom item Fire Flower
 *
 */
public class FireFlowerItem {

    /**
     * Creates a returns a new instance of a fire flower item
     * @return a new instance of a fire flower item
     */
    public static ItemStack getItemStack() {

        ItemStack i = new ItemStack(Material.POPPY);
        ItemMeta iM = i.getItemMeta();

        String name = "&4&lFire Flower";
        iM.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        iM.getPersistentDataContainer().set(MyNamespacedKeys.getPowerupID(), PersistentDataType.INTEGER, 1);
        i.setItemMeta(iM);

        return i;
    }

    /**
     * Checks if the provided String matches the name of this item, case-insensitive
     * @param check The string to be checked
     * @return true if the provided String matches the name of this item, case-insensitive
     */
    public static boolean isNameMatch(String check) {
        return check.equalsIgnoreCase("fire_flower");
    }

    /**
     * Handles a player powering up with this item
     * @param p The player who needs to power up
     */
    public static void powerUp(Player p) {
        p.sendMessage(ChatColor.DARK_RED + "You are now Fire Mario");

        float r = 0.5F;
        float precision = 0.1F;
        VectorUtils.drawSpiral(p, r,0F, precision, Particle.FLAME);
        VectorUtils.drawSpiral(p, r, (float) Math.PI, precision, Particle.FLAME);
    }

}
