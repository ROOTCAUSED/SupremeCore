package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import org.bukkit.Bukkit;

import java.util.Random;

public class IntervalTask implements Runnable {
    private IntervalAnnouncer announcer;

    public IntervalTask(IntervalAnnouncer instance) {
        this.announcer = instance;
    }

    public void run() {
        if (IntervalAnnouncer.announcements != null && !IntervalAnnouncer.announcements.isEmpty()) {
            String send = "";
            if (IntervalAnnouncer.random) {
                int s = (new Random()).nextInt(IntervalAnnouncer.announcements.size());
                if (IntervalAnnouncer.rLast == s) {
                    s = (new Random()).nextInt(IntervalAnnouncer.announcements.size());
                }

                IntervalAnnouncer.rLast = s;
                send = (String)IntervalAnnouncer.announcements.get(s);
            } else if (IntervalAnnouncer.count < IntervalAnnouncer.announcements.size()) {
                ++IntervalAnnouncer.count;
                send = (String)IntervalAnnouncer.announcements.get(IntervalAnnouncer.count - 1);
            } else {
                IntervalAnnouncer.count = 0;
                send = (String)IntervalAnnouncer.announcements.get(0);
            }

            Bukkit.getServer().getScheduler().runTask(this.announcer.plugin, new AnnounceTask(this.announcer, send));
        }
    }
}