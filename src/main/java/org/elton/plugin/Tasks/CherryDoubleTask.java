package org.elton.plugin.Tasks;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.elton.plugin.MyUtils.MyUtils;

/**
 * Bukkit Runnable that handles the behaviour of Cherry Doubles
 */
public class CherryDoubleTask extends BukkitRunnable implements Listener {

    ServerPlayer duplicate;
    Player originPlayer;
    Vector offset;
    boolean keepValid;

    public CherryDoubleTask(ServerPlayer duplicate, Player originPlayer, Vector offset, boolean keepValid) {
        this.duplicate = duplicate;
        this.originPlayer = originPlayer;
        this.offset = offset;
        this.keepValid = keepValid;

    }

    @Override
    public void run() {
        MyUtils.updateNpcPosToPlayer(duplicate, originPlayer, offset, true);

        //if the origin player is not online, cancel this event
        if(!originPlayer.isOnline()) {
            this.cancel();
            MyUtils.clearCherryDoublesForPlayer(originPlayer);
            //TODO - broadcast this dupe leaving for other players
        }

    }
}
