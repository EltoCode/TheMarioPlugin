package org.elton.plugin.Entities;

import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.elton.plugin.MyUtils.MyUtils;
import org.elton.plugin.Tasks.CherryDoubleTask;

import java.util.ArrayList;

/**
 * Class to manage Cherry Doubles
 */
public class CherryDouble {

    //The player this double belongs to
    Player originPlayer;
    //The fake Server player that acts as a double
    ServerPlayer duplicate;


    /**
     * Performs all the steps required to spawn a cherry double on client side
     * @param originPlayer The player this cherry double belongs to
     * @param offset The offset to keep this double at
     */
    public CherryDouble(Player originPlayer, Vector offset) {
        //gets required objects

        //sets the origin of this cherry double
        this.originPlayer = originPlayer;
        //Craft instance of player
        CraftPlayer craftPlayer = (CraftPlayer) originPlayer;
        //nsm instance of player
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        //nms instance of the server
        MinecraftServer minecraftServer = serverPlayer.getServer();
        ServerLevel serverLevel = serverPlayer.getLevel();

        //gets the name of the player and makes it into a name for the double
        String dupeName = originPlayer.getName();

        //The nms instance of a server player
        duplicate = new ServerPlayer(minecraftServer, serverLevel, MyUtils.cloneGameProfile(originPlayer));
        duplicate.setPos( //sets the position
                originPlayer.getLocation().getX(),
                originPlayer.getLocation().getY(),
                originPlayer.getLocation().getZ());

        //get the connection of the main player
        //ServerGamePacketListenerImpl connection = serverPlayer.connection; //Switched to broadcast
        //send a Player Info packet over the connection
        //Sent by the server to update the user list (<tab> in the client).
        MyUtils.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, duplicate), true);
        //sends a new Spawn Player packet over the connection
        //This packet is sent by the server when a player comes into visible range, not when a player joins.
        MyUtils.broadcastAll(new ClientboundAddPlayerPacket(duplicate), true);

        //doing it this way removes the player from tab list while keeping skins
        new BukkitRunnable() {
            @Override
            public void run() {
                MyUtils.removeDupeTabInfo(originPlayer);
            }
        }.runTaskLater(MyUtils.getPlugin(), 0L);

        new CherryDoubleTask(duplicate,originPlayer, offset,true)
                .runTaskTimer(MyUtils.getPlugin(), 0L, 1L);
    }

    /**
     * Spawns a cherry double while safely performing needed function
     * @param p The player to spawn this double for
     * @param offset The offset to keep this double at
     * @return The instance of CherryDouble created
     */
    public static CherryDouble spawnCherryDouble(Player p, Vector offset) {
        //summons a cherry double
        CherryDouble cherryDouble = new CherryDouble(p, offset);
        ArrayList<CherryDouble> aL = MyUtils.getPlayerDupesMap().get(p.getName());
        aL.add(cherryDouble);
        return cherryDouble;
    }

    /**
     * Gets the origin player of this cherry double
     * @return The origin player
     */
    public Player getOriginPlayer() {
        return originPlayer;
    }

    /**
     * Gets the serverplayer that acts as the duplicate npc of this cherry double
     * @return the serverplayer that acts as the duplicate npc
     */
    public ServerPlayer getDuplicate() {
        return duplicate;
    }

}
