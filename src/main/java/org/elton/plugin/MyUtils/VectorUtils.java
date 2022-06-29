package org.elton.plugin.MyUtils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Class to house utils related to vectors
 * TODO - consider merge with MyUtils
 */
public final class VectorUtils {

    /**
     * Calculates the reflection vector, where
     * @param d is the incident vector
     * @param n is the normal of the mirror plane
     * @return the reflection vector caused by d on n
     */
    public static Vector calculateReflect(Vector d, Vector n) {
        //Some vector math
        // d is incident vector
        // n is normal of mirror plane (might need to get normal of plane)

        // d.n
        double dn = d.dot(n);

        //2n(d.n)
        Vector v_2ndn = n.multiply(2 * dn);

        //return r = d - 2n(d.n)
        return d.subtract(v_2ndn);
    }

    /**
     * Draws a spiral with the given parameters at the given player
     * @param p The player to draw the spiral at
     * @param radius The starting radius of the spiral
     * @param start The start angle of the spiral
     * @param precision The amount of points to make the spiral
     * @param particle The particle to draw the spiral with
     */
    public static void drawSpiral(Player p, float radius, float start, float precision, Particle particle) {

        double end = start + 2*Math.PI; //a full circle ends at 2pi
        new BukkitRunnable() {
            //h and k represent offset
            double h;
            double k;
            float t = start; //iterator
            double x;
            double y = p.getLocation().getY();
            double z;
            double r = radius;

            @Override
            public void run() {
                //external for loop to speed up drawing
                for(int i = 0; i<4; i++) {
                    //terminates at condition, basically a for loop
                    if (!(t <= end))
                        this.cancel();
                    //h and k represent offset
                    h = p.getLocation().getX();
                    k = p.getLocation().getZ();
                    //circle formula
                    x = r * Math.sin(t) + h;
                    z = r * Math.cos(t) + k;
                    p.getWorld().spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0.001);

                    //increments y and radius
                    y += 0.05;
                    r += 0.01;
                    //increments iterator
                    t += precision;
                }
            }
        }.runTaskTimer(MyUtils.getPlugin(), 0L, 1L);
    }

    /**
     * Gets the offset the cherry double needs to be at
     * @param dupeNum The number of the cherry double
     * @return The required offset, or null if the number is above max cherry double
     */
    public static Vector getCherryOffset(int dupeNum) {
        return switch (dupeNum) {
            case 0 -> new Vector(1, 0, 0);
            case 1 -> new Vector(0, 0, 1);
            case 2 -> new Vector(-1, 0, 0);
            case 3 -> new Vector(0, 0, -1);
            default -> null;
        };
    }

    /**
     * Gets a valid upward location for this location
     * A location is considered invalid if there is a solid block at the location
     * @param loc The location to check
     * @return A valid upward location for the given location
     */
    public static Location getValidLocation(Location loc) {
        /*int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();*/

        Location newLoc = loc.clone();

        Block block = newLoc.getBlock();

        while(!(block.isPassable())) {
            newLoc.add(0, 1,0);
            block = newLoc.getBlock();
        }
        return newLoc;
    }


}
