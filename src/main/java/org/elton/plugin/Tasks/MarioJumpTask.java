package org.elton.plugin.Tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.TheMarioPlugin;

/**
 * Bukkit runnable that executes jump collision behaviors
 */
public class MarioJumpTask extends BukkitRunnable {

    TheMarioPlugin plugin;
    Player player;

    public MarioJumpTask(TheMarioPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {

        //if the player is moving downwards or not moving, cancels event
        if(player.getVelocity().getY() <= 0.0) {this.cancel(); return;}

        //gets the head location
        Location headLoc = player.getEyeLocation().add(0F, 0.75F, 0.0);
        //gets the block at headLoc
        Block headBlock = headLoc.getBlock();

        //if the Block is bricks, breaks the block, spawns particles&sound, sets y velocity of player to 0
        if(headBlock.getType() == Material.BRICKS) {
            //Handles Bricks
            bricksHandle(headLoc, headBlock);
        }

        //if the block is a shulker box, deposits items, plays some particles&sound too
        if(headBlock.getType() == Material.YELLOW_SHULKER_BOX) {
            //Handles a shulker box
            shulkerHandle(headLoc, headBlock);
        }

    }

    /**
     * Handles the occurrence of a bricks at a location
     * Will work with other blocks, but ideally should only be called if the block is bricks
     * @param headLoc The Location of a player head
     * @param headBlock The Location of the block to handle
     */
    private void bricksHandle(Location headLoc, Block headBlock) {
        //particles and sound
        player.spawnParticle(Particle.BLOCK_DUST, headLoc,
                50, 0.1, 0.1, 0.1, 0.1,
                headBlock.getBlockData());
        player.playSound(headLoc, headBlock.getBlockData().getSoundGroup().getBreakSound(),
                1f, 1f);
        //breaks block
        headBlock.breakNaturally(new ItemStack(Material.STICK));
        //resets velocity
        player.setVelocity(player.getVelocity().setY(0));
    }

    /**
     * Handles the occurrence of a shulker box at a location
     * Should only be called after checking if the block is a shulker, will throw error otherwise
     * @param headLoc The Location of a player head
     * @param headBlock The Location of the block to handle
     */
    private void shulkerHandle(Location headLoc, Block headBlock) {
        ShulkerBox sb = (ShulkerBox) headBlock.getState();
        //gets the inventory of sb
        Inventory inv = sb.getInventory();

        //checks if inventory empty, if so cancels task
        if(inv.isEmpty()) {
            //plays an alternative animation
            MyUtils.playShulkerAnimation(headLoc, 1L);
            player.playSound(headLoc, Sound.BLOCK_WOOL_BREAK, 1F, 1F);
            this.cancel();
            return;
        }

        //if here means inv not empty

        //playes the shulker animation and sound
        MyUtils.playShulkerAnimation(headLoc, 4L);
        player.playSound(headLoc,Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F);

        //gets the first item from inventory
        ItemStack itemFromInv = MyUtils.invPop(inv);

        //TODO - maybe add code that checks for empty space
        //spawns an item stack slightly above headBlock
        Item droppedItem = player.getWorld().dropItem(headBlock.getLocation().add(0.5, 1, 0.5),
                itemFromInv);
        //adds velocity to the item to make it drop
        droppedItem.setVelocity(player.getLocation().getDirection().setY(0.005).normalize());

        //cancels the task after 1 item drops
        this.cancel();
    }
}
