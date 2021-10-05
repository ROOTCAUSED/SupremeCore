package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;


import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMsgTask extends BukkitRunnable {
    private int s;
    private final String msg;
    private final Player p;
    private final String name;
    private boolean first;

    public PlayerMsgTask(Player player, String msg, int length) {
        this.msg = msg;
        this.p = player;
        this.s = length;
        this.first = true;
        this.name = player.getName();
    }

    public void run() {
        if (IntervalAnnouncer.sending == null) {
            IntervalAnnouncer.sending = new HashMap();
        }

        if (this.p == null) {
            if (this.name != null && IntervalAnnouncer.sending.containsKey(this.name)) {
                IntervalAnnouncer.sending.remove(this.name);
            }

            this.cancel();
        } else if (IntervalAnnouncer.minechat != null && IntervalAnnouncer.minechat.contains(this.name)) {
            this.cancel();
        } else {
            if (this.first) {
                this.first = false;
                if (IntervalAnnouncer.sending.containsKey(this.name) && IntervalAnnouncer.sending.get(this.name) != null) {
                    ((PlayerMsgTask)IntervalAnnouncer.sending.get(this.name)).cancel();
                }

                IntervalAnnouncer.sending.put(this.name, this);
            }

            if (this.s > 0) {
                IntervalAnnouncer.sendAnnouncement(this.p, this.msg);
                --this.s;
            } else {
                if (IntervalAnnouncer.sending.containsKey(this.name)) {
                    IntervalAnnouncer.sending.remove(this.name);
                }

                this.cancel();
            }

        }
    }
}

