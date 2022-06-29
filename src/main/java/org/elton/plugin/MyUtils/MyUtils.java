package org.elton.plugin.MyUtils;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.elton.plugin.Entities.CherryDouble;
import org.elton.plugin.Items.DoubleCherryItem;
import org.elton.plugin.Items.FireFlowerItem;
import org.elton.plugin.TheMarioPlugin;

import java.util.*;

import static io.netty.buffer.Unpooled.buffer;

/**
 * A static class that houses some useful functions
 */
public final class MyUtils {

    ////////////////////////
    //HashMaps
    ///////////////////////
    /**
     * An arraylist containing each players dupes
     */
    private static final HashMap<String, ArrayList<CherryDouble>> playerDupes = new HashMap<>();

    /**
     * Gets the player dupes hashmap
     * @return The player dupes hashmap
     */
    public static HashMap<String, ArrayList<CherryDouble>> getPlayerDupesMap() {
        return playerDupes;
    }

    ////////////////////////
    //Main Plugin Ref
    ///////////////////////

    //A static instance of the plugin for any other util classes that require it
    private static TheMarioPlugin plugin;

    /**
     * Sets the static instance of the plugin to be used by util classes.
     * ONLY CALL THIS FUNCTION ONCE AT STARTUP
     * @param plugin The plugin instance to be ser
     * @return true if this is the first time the function is called, false otherwise
     */
    public static boolean onEnable(TheMarioPlugin plugin) {
        if(MyUtils.plugin == null) {
            MyUtils.plugin = plugin;
            plugin.getLogger().info("Utility class plugin set.");

            //creates namespaced keys
            MyNamespacedKeys.createNamespacedKeys();

            //reload failsafe for Hashmaps
            for(Player p : Bukkit.getOnlinePlayers()) {
                HashMap<String, ArrayList<CherryDouble>> dupeMap = MyUtils.getPlayerDupesMap();
                dupeMap.putIfAbsent(p.getName(), new ArrayList<>());

                plugin.getLogger().info("Enable routine Complete");
            }

            return true;
        }
        else {
            plugin.getLogger().warning("The Utility Class was initialised with this plugin before." +
                    "\n Do not call MyUtils.setPlugin() twice");
            return false;
        }
    }

    /**
     * Gets the plugin associated with this class
     * If MyUtils.setPlugin() was not called before, will return null
     * @return the plugin associated with this class
     */
    public static TheMarioPlugin getPlugin() {
        return MyUtils.plugin;
    }

    ////////////////////////
    //Players
    ////////////////////////
    /**
     * Gets the location a bit infront of an Player.
     * @param p The player to get the inFront location of
     * @return The location infront of the entity
     */
    public static Location getLocInFrontOfPlayer(Player p) {
        //gets the location of the player
        Location loc = p.getEyeLocation();
        //returns the location in front of player
        return loc.add(loc.getDirection());
    }

    /**
     * Checks wheter the give player was initialised for the mario plugin
     * @param p The player to check
     * @return true if the player was initialized, false othrwise
     */
    public static boolean isMarioInit(Player p) {
        return p.getPersistentDataContainer().has(MyNamespacedKeys.getHasPowerup(), PersistentDataType.INTEGER);
    }

    /**
     * Initialized the given player for the mario plugin
     * @param p The player to initialize
     * @return true if the player was initialized, false if the player was initialised before
     */
    public static boolean marioInit(Player p) {
        //if the player is alreadt init, return false
        if(isMarioInit(p)) {
            MyUtils.plugin.getLogger().warning("Player " + p.getName() + " has already been initialised.");
            return false;
        }
        else {
            //adds required data containes
            p.getPersistentDataContainer()
                    .set(MyNamespacedKeys.getHasPowerup(),PersistentDataType.INTEGER, 0);
            MyUtils.plugin.getLogger().warning("Player " + p.getName() + " has been initialised.");
            p.getPersistentDataContainer()
                    .set(MyNamespacedKeys.getDuplicateNumber(),PersistentDataType.INTEGER, 0);
            MyUtils.getPlayerDupesMap().put(p.getName(), new ArrayList<>());
            return true;
        }
    }

    /**
     * Sets the hasPowerup value for a player
     * @param p The player to set the value for
     * @param pwrID The powerup ID to set to
     */
    public static void setPlayerPowerup(Player p, int pwrID) {
        p.getPersistentDataContainer()
                .set(MyNamespacedKeys.getHasPowerup(), PersistentDataType.INTEGER, pwrID);
    }

    /**
     * Gets the id of the powerup the player has
     * @param p The player to get the id from
     * @return The powerup id of the player, 0 if the player has no powerup, -1 if the player has not been inited yet
     */
    public static int getPlayerPowerup(Player p) {

        if(MyUtils.isMarioInit(p)) {
            return p.getPersistentDataContainer()
                    .get(MyNamespacedKeys.getHasPowerup(), PersistentDataType.INTEGER);
        }
        else
            return -1;
    }

    /**
     * Handles the powerup change for the given power up id
     * @param p The player to handle the powerup change for
     * @param pwrID The powerup ID to handle
     */
    public static void handlePowerUpChange(Player p, int pwrID) {

        //if not turned into cherry mario, rmeove all doubles
        if(pwrID != 2){
            MyUtils.clearCherryDoublesForPlayer(p);
        }

        //handles changing between powerups
        switch (pwrID) {
            case 1 -> FireFlowerItem.powerUp(p);
            case 2 -> DoubleCherryItem.powerUp(p);
        }
    }

    ////////////////////////
    /// INVENTORY
    ////////////////////////
    /**
     * Gets the index of the first filled slot in the given inventory
     * @param inv The inventory to search
     * @return The index of the first filled slot in the given inventory
     */
    public static int invGetFirstFilledIndex(Inventory inv) {

        //if the inventory is empty, returns -1
        if(inv.isEmpty()) {return  -1;}

        //gets the fire of all content in invrntory including air spaces that are null
        ItemStack firstItem = Arrays.stream(inv.getContents())
                .filter(Objects::nonNull)   //filters out null
                .findFirst()//gets the first non null instance
                .orElse(null); //should never happen

        //returns index of matching item stack
        return inv.first(firstItem);
    }

    /**
     * Gets the first item from the inventory, then removes it from inventory
     * @param inv The inventory to pop from
     * @return The item popped from the inventory
     */
    public static ItemStack invPop(Inventory inv) {

        //if the inventory is empty, returns air
        if(inv.isEmpty()) {return  new ItemStack(Material.AIR);}

        //gets the index of the first empty slot
        int firstItemIndex = MyUtils.invGetFirstFilledIndex(inv);
        //creates an item stack of the first item in invnetory
        ItemStack itemFromInv = inv.getItem(firstItemIndex);
        //removes indexed item from inventory
        inv.clear(firstItemIndex);
        //returns the first item
        return itemFromInv;
    }


    /////////////////////////
    //Items
    ////////////////////////

    /**
     * Checks whether the item is a mario powerup item
     * @param i the item to be checked
     * @return true if the item is a mario powerup item
     */
    public static boolean isMarioPowerup(ItemStack i) {
        return i.getItemMeta()
                .getPersistentDataContainer()
                .has(MyNamespacedKeys.getPowerupID(), PersistentDataType.INTEGER);
    }

    /**
     * Checks whether the item is a mario powerup item
     * @param i the item to be checked
     * @return true if the item is a mario powerup item
     */
    public static boolean isMarioPowerup(Item i) {
        return MyUtils.isMarioPowerup(i.getItemStack());
    }

    /**
     * Gets the powerupID of the item
     * @param i the item to be checked
     * @return the powerupID of the item, or -1 if it's not a powerup item
     */
    public static int getPowerupIDFromItem(ItemStack i) {
        //if not a powerup returns -1
        if(!isMarioPowerup(i))
            return -1;
        //returns value
        return i.getItemMeta()
                .getPersistentDataContainer()
                .get(MyNamespacedKeys.getPowerupID(), PersistentDataType.INTEGER);
    }
    /**
     * Gets the powerupID of the item
     * @param i the item to be checked
     * @return the powerupID of the item, or -1 if it's not a powerup item
     */
    public static int getPowerupIDFromItem(Item i) {
        //retusn the value
        return MyUtils.getPowerupIDFromItem(i.getItemStack());
    }

    /////////////////////////////////
    //NMS
    /////////////////////////////////

    /**
     * Animates the opening and closing of a shulker box by the specified delay
     * @param boxLoc The location of the shulker box
     * @param delay The time between the openign and closing animation
     * @return true if the block at the provided location is a shulker box, false otherwise
     */
    public static boolean playShulkerAnimation(Location boxLoc, long delay) {

        //if the block at boxLoc is not a shulkerbox, returns false
        if(!Tag.SHULKER_BOXES.isTagged(boxLoc.getBlock().getType())) {return false;}

        //gets the nms equivilant of the shulker box
        ServerLevel world = ((CraftWorld)boxLoc.getWorld()).getHandle();
        BlockPos pos = new BlockPos(boxLoc.getX(), boxLoc.getY(), boxLoc.getZ());
        ShulkerBoxBlockEntity tileChest = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);

        //tells the server to run a block event of the opening anim
        world.blockEvent(pos, tileChest.getBlockState().getBlock(), 1, 1);  //i is key, j is value

        //runs this 3 tock later
        new BukkitRunnable() {
            @Override
            public void run() {
                //tells the server to run a block event of the closing anim
                world.blockEvent(pos, tileChest.getBlockState().getBlock(), 1, 0);
            }
        }.runTaskLater(MyUtils.getPlugin(), delay);

        //succesfully completed
        return true;
    }

    /**
     * Updates a npc position to a player's position.
     * TODO - add a parameter that allows selection of which player should receive this update
     * @param npc The npc who's pos needs to be updated
     * @param player The player's position to which the npx pos needs to be updated.
     * @param offset The offset to add to the given position
     * @param keepValid If true, will prevent the location from being within solid blocks by moving the npc upwards
     */
    public static void updateNpcPosToPlayer(ServerPlayer npc, Player player, Vector offset, boolean keepValid) {

        //Craft instance of player
        CraftPlayer craftPlayer = (CraftPlayer) player;
        //nsm instance of player
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        //get the connection of the main player
        //ServerGamePacketListenerImpl connection = serverPlayer.connection; Switched to broadcast

        //gets the offset location
        Location offsetLoc = player.getLocation().add(offset);
        //if keepValid is set to true

        if(keepValid) {
            //checking to see if location is valid
            offsetLoc = VectorUtils.getValidLocation(offsetLoc);
        }

        //creates a friendly byte buff with teleportation details
        //TODO - rework depreciated method isOnGround()
        ByteBuf byteBuf = buffer(0);
        FriendlyByteBuf tpPayload = new FriendlyByteBuf(byteBuf);
        float yaw = offsetLoc.getYaw();
        float pitch = offsetLoc.getPitch();
        tpPayload.writeVarInt(npc.getId());
        tpPayload.writeDouble(offsetLoc.getX());
        tpPayload.writeDouble(offsetLoc.getY());
        tpPayload.writeDouble(offsetLoc.getZ());
        tpPayload.writeByte((int) ((yaw%360)/360 * 255));
        tpPayload.writeByte((int) ((pitch%360)/360 * 255));
        tpPayload.writeBoolean(false);
        //sends a entity teleport packet
        //This packet is sent by the server when an entity moves more than 8 blocks.
        MyUtils.broadcastAll(new ClientboundTeleportEntityPacket(tpPayload));
        //sends a entity head look packet
        //While sending the Entity Look packet changes the vertical rotation of the head,
        // sending this packet appears to be necessary to rotate the head horizontally.
        MyUtils.broadcastAll(new ClientboundRotateHeadPacket(npc, (byte) ((yaw%360)/360 * 255)));
    }

    /**
     * Removes all cherry doubles for a player
     * @param p The player for whom to remove all cherry doubles
     */
    public static void clearCherryDoublesForPlayer(Player p) {

        String playerName = p.getName();
        //for every double existing, remove them
        //get the number of dupes
        int dupeNum = MyUtils.getPlayerDupesMap().get(playerName).size();
        for(int i = 0; i < dupeNum; i++ ) {
            MyUtils.killCherryDouble(p);
        }
    }

    /**
     * Removes a players Cherry Double from appearing in the tab list
     * @param p The players whose dupe needs to be removed
     */
    public static void removeDupeTabInfo(Player p) {
        //ServerGamePacketListenerImpl connection = ((CraftPlayer)p).getHandle().connection;
        ArrayList<CherryDouble> dupeList = MyUtils.getPlayerDupesMap().get(p.getName());

        for(CherryDouble dupe : dupeList) {
            //sends a packet that removes that double from the tablist
            MyUtils.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER,
                    dupe.getDuplicate()), true);
        }
    }

    /**
     * Kills a players latest cherry double
     * @param p The players whose cherry double needs to be killed
     */
    public static void killCherryDouble(Player p) {

        ArrayList<CherryDouble> pDupes = MyUtils.getPlayerDupesMap().get(p.getName());
        //pops an dupe id from the arraylist
        CherryDouble deadDupe = pDupes.get(pDupes.size()-1);
        pDupes.remove(pDupes.size()-1);
        //reduces the number of dupes in persitant data
        int dupeNum = p.getPersistentDataContainer()
                .get(MyNamespacedKeys.getDuplicateNumber(), PersistentDataType.INTEGER);
        p.getPersistentDataContainer()
                .set(MyNamespacedKeys.getDuplicateNumber(), PersistentDataType.INTEGER, dupeNum-1);

        //TODO - Make a util function that broadcasts packets to players
        //gets the connection
        //ServerGamePacketListenerImpl connection = ((CraftPlayer)p).getHandle().connection; Switched to broadcast

        int deadDupeID = deadDupe.getDuplicate().getId();

        /*//sends a packet that plays the death animation
        ByteBuf byteBuf = buffer(0);
        FriendlyByteBuf statusPayload = new FriendlyByteBuf(byteBuf);
        statusPayload.writeInt(deadDupeID);
        statusPayload.writeByte(3);
        //Entity statuses generally trigger an animation for an entity.
        //TODO - figure out why this packet does not work
        MyUtils.broadcastAll(new ClientboundEntityEventPacket(statusPayload));*/
        //Sent by the server when an entity is to be destroyed on the client.
        MyUtils.broadcastAll(new ClientboundRemoveEntitiesPacket(deadDupeID));

        //spawns some particles and sound
        Player originPlayer = deadDupe.getOriginPlayer();
        originPlayer.playSound(originPlayer.getLocation(), Sound.ENTITY_PUFFER_FISH_BLOW_OUT, 10F ,1F);
        originPlayer.spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, originPlayer.getLocation(), 50, 0, 0, 0, 0.1);

    }

    /**
     * Fetches the game profile of a plauer from player object
     * @param player Player to get profile from
     * @return The fetched game profile
     */
    public static GameProfile cloneGameProfile(Player player) {
        ServerPlayer p = ((CraftPlayer)player).getHandle();
        //makes a new gameprofile with properteies of the given players profile
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        gameProfile.getProperties().putAll(p.getGameProfile().getProperties());
        return gameProfile;
    }

    /**
     * Sends packets to every player on the server
     * @param packet the packet to be send
     */
    public static void broadcastAll(Packet<?> packet) {
        //gets all online players
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player p : onlinePlayers) {
            //gets the connection and sends packet
            ServerGamePacketListenerImpl connection = ((CraftPlayer)p).getHandle().connection;
            connection.send(packet);
        }
    }
    /**
     * Sends packets to every player on the server
     * @param packet the packet to be send
     * @param isVital Whether the packet needs to be sent to new player who might not have been online when the packet
     *                was first sent
     */
    public static void broadcastAll(Packet<?> packet, boolean isVital) {
        if(isVital){
            //TODO - Add this to the a packet hashmap with parameters
        }
        MyUtils.broadcastAll(packet);
    }
}
