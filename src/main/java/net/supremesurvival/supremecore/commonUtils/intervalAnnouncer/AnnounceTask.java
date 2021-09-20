package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnnounceTask implements Runnable {
    private IntervalAnnouncer announcer;
    private final String msg;

    public AnnounceTask(IntervalAnnouncer instance, String msg) {
        this.announcer = instance;
        this.msg = msg;
    }

    public void run() {
        int l = IntervalAnnouncer.announcementLength;
        Iterator var3 = Bukkit.getServer().getOnlinePlayers().iterator();

        while(var3.hasNext()) {
            Player p = (Player)var3.next();
            try {
                (new PlayerMsgTask(p, this.msg, l)).runTaskTimer(this.announcer.plugin, 1L, 10L);
            } catch (IllegalStateException | IllegalArgumentException var5) {
                var5.printStackTrace();
            }
        }

    }
}
