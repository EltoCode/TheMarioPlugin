package org.elton.plugin.Entities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftSlime;
import org.bukkit.entity.*;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.elton.plugin.MyUtils.MyNamespacedKeys;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.Tasks.FireballTask;

import java.util.List;

import static org.elton.plugin.MyUtils.VectorUtils.calculateReflect;

/**
 * A Class to make managing mario fireballs easier
 */
public class MarioFireball extends CraftSlime {

    /**
     * sets the max number of bounces a fireball can do before it terminates itself
     * set this value to negative 1 for infinite bounces
     */
    public static int MAX_BOUNCE = 8;

    /**
     * bukkit slime object to make into a fireball
     */
    private final Slime slime;
    /**
     * the initial velocity with which to throw the fireball
     */
    private Vector initVel;

    /**
     * The player who spawned this fireball, null if not spawned by player
     */
    private final Entity owner;

    /**
     * Counts the number of bounces the fireball had.
     */
    private int bounceCount = 0;

    /**
     * Constructor that initializes values of fireball
     * @param slime bukkit slime object to make into a fireball
     * @param initVel the initial velocity with which to throw the fireball
     * @param owner The owner of this fireball
     */
    public MarioFireball(Slime slime, Vector initVel, Entity owner) {
        super((CraftServer) slime.getServer(), ((CraftSlime) slime).getHandle());
        this.slime = slime;
        this.initVel = initVel;
        this.owner = owner;

        //Edits data of slime to make it fireball
        this.slime.setVisualFire(true);
        this.slime.setInvulnerable(true);
        this.slime.setSize(0);
        this.slime.setVelocity(initVel);
        this.slime.setAware(false);
        this.slime.setInvisible(true);
        this.slime.setLootTable(LootTables.EMPTY.getLootTable());
        this.slime.getPersistentDataContainer().set(MyNamespacedKeys.getIsFireball(),
                     PersistentDataType.INTEGER, 1);

        new FireballTask(this)
                .runTaskTimer(MyUtils.getPlugin(), 1L, 1L);
    }

    /**
     * Gets the slime associated with this object
     * @return The slime entity
     */
    public Slime getSlime() {
        return slime;
    }

    /**
     * Gets a clone the initial Velocity of this object.
     * Do not use method to try and change the velocity vector.
     * @return the initial velocity
     */
    public Vector getInitVel() {
        return initVel.clone();
    }

    /**
     * Gets the entity who spawned this fireball, null if no owner.
     * @return the owner of this fireball
     */
    public Entity getOwner() {return owner;}

    /**
     * Sets the initial Velocity of this object.
     * @param initVel the velocity to be set
     */
    public void setInitVel(Vector initVel) {
        this.initVel = initVel;
    }

    /**
     * Spawns a MarioFireball while safely performing the required function
     * @param player The owner of this fireball
     * @param loc The location to spawn this fireball at
     * @param initVel The initial velocity of this fireball
     * @return The instance of this fireball spawned
     */
    public static MarioFireball spawnFireball(Player player, Location loc, Vector initVel) {

        //summons a slime
        Slime fb = (Slime) player.getWorld().spawnEntity(
                loc,
                EntityType.SLIME);
        //Creates a wrapper class to make management easier
        MarioFireball fireballM = new MarioFireball(fb, initVel, player);
        //we want the slime to look straight ahead
        fireballM.setRotation(loc.getYaw(), 0.0F);
        //plays sound
        //player.playSound(loc, Sound.ENTITY_SNOW_GOLEM_HURT, 1F, 1F); //I don't like any sounds
        //returns the created fireball
        return  fireballM;
    }



    /**
     * Sets the new velocity and rotation of the fireball
     * @param bounce The angle with which to bounce the entity
     */
    public void setBounce(Vector bounce) {

        //changes the current velocity and init velocity to the bounce
        this.setVelocity(bounce);
        this.setInitVel(bounce);
        //creates a new location to help with getting the rotation of bounce
        Location bounceLoc = new Location(this.getWorld(), 0, 0, 0);
        bounceLoc.setDirection(bounce);
        //sets the rotation in the direction of the new velocity
        this.setRotation(bounceLoc.getYaw(), 0.0F);

        //checks if the bounce count is equal to the set max bounce,
        //if it is, removes the entity
        //increments after check
        if(this.bounceCount++ == MarioFireball.MAX_BOUNCE)
            this.slime.remove();
    }

    /**
     * When called, checks and handles collisions for this fireball
     */
    public void collisionCheck() {

        //gets los of entity to 1 block, ie: block in front of entity
        getLineOfSight(null, 1)
                .stream()
                .filter(block -> !block.isPassable())
                .forEach(block -> {
                    //if the code reaches here it means the block in front of the entity is solid

                    //gets the face of the collision
                    Block current = getLocation().getBlock();

                    //gets the face of the nVector
                    BlockFace bf = current.getFace(block);

                    //these values cause getDirection to spaz out
                    if(bf == BlockFace.SELF || bf == null)
                        return;

                    //gets the normal of the reflection plane
                    Vector nVector = bf.getDirection().normalize();

                    //calculates the reflect vector
                    Vector reflect = calculateReflect(getInitVel().normalize(), nVector);

                    //Does all the bounce work
                    setBounce(reflect);

                }); //end of .getLineOfSight stream

    }

    /**
     * When called checks and handles damage for this fireball
     */
    public void damageCheck() {
        //gets entities within 1 block of slime
        List<Entity> nearbyEntities = getNearbyEntities(0.5, 0.5, 0.5);
        if(nearbyEntities.size() > 0) {

            //gets the first item in collection which should be the closest one
            //checks if that is castable to living entity, if not nulls it
            LivingEntity livE = (nearbyEntities.get(0) instanceof LivingEntity ?
                    (LivingEntity) nearbyEntities.get(0) : null);
            //if it's null, or if it's the owner returns
            if(livE == null || livE.equals(owner)) {return;}

            //if the entity is another fireball, removes both
            if(livE.getPersistentDataContainer()
                    .has(MyNamespacedKeys.getIsFireball(),PersistentDataType.INTEGER)) {
                //plays particles and sound
                this.slime.getWorld().spawnParticle(Particle.FLAME, this.slime.getLocation(), 25);
                this.slime.getWorld().playSound(this.slime.getLocation(), Sound.BLOCK_LAVA_POP, 1F, 1F);
                livE.remove();
                this.remove();
            }

            //sets the entity on fire
            livE.setFireTicks(2000);
            //checks if fireball has an owner, if it does, passes that with damage
            Entity owner = getOwner();
            if(owner == null) {livE.damage(20.0);}
            else {livE.damage(20.0, owner);}
        }
    }

    /**
     * Simple function that spawns particles for fireballs
     */
    public void summonParticles() {

        //gets the location of the slime
        Location loc = this.slime.getLocation();

        //summons particle
        this.slime.getWorld().spawnParticle(Particle.LAVA, loc, 2);

    }
}
