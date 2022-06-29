package org.elton.plugin.MyUtils;

import org.bukkit.NamespacedKey;
import org.elton.plugin.TheMarioPlugin;

/**
 * Class to get namespaced keys
 */
public class MyNamespacedKeys {

    //main plugin ref

    //keys
    private static NamespacedKey isFireball;
    private static NamespacedKey powerupID;
    private static NamespacedKey hasPowerup;
    private static NamespacedKey duplicateNumber;

    /**
     * Creates namespaced keys for this plugin
     */
    public static void createNamespacedKeys() {  //Migrated from static{} because it's safer.

        TheMarioPlugin plugin = MyUtils.getPlugin();
        //keys
        isFireball = new NamespacedKey(plugin, "isFireball");
        powerupID = new NamespacedKey(plugin, "powerupID");
        hasPowerup = new NamespacedKey(plugin, "hasPowerup");
        duplicateNumber = new NamespacedKey(plugin, "duplicateNumber");
    }

    /**
     * Gets the isFireball namespaced key
     * @return the isFireball namespaced key
     */
    public static NamespacedKey getIsFireball() {
        return isFireball;
    }
    /**
     * Gets the PowerupID namespaced key
     * @return the PowerupID namespaced key
     */
    public static NamespacedKey getPowerupID() { return powerupID; }
    /**
     * Gets the hasPowerup namespaced key
     * @return the hasPowerup namespaced key
     */
    public static NamespacedKey getHasPowerup() { return hasPowerup; }
    /**
     * Gets the doubleNumber namespaced key
     * @return the doubleNumber namespaced key
     */
    public static NamespacedKey getDuplicateNumber() { return duplicateNumber; }
}
