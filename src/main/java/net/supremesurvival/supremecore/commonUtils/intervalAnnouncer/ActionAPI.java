package net.supremesurvival.supremecore.commonUtils.intervalAnnouncer;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ActionAPI {
    public ActionAPI() {
    }

    public static void sendPlayerAnnouncement(Player p, String msg) {
        IntervalAnnouncer.sendAnnouncement(p, msg);
    }

    public static void sendServerAnnouncement(String msg) {
        Iterator var2 = Bukkit.getOnlinePlayers().iterator();

        while(var2.hasNext()) {
            Player p = (Player)var2.next();
            if (IntervalAnnouncer.minechat != null && !IntervalAnnouncer.minechat.contains(p.getName())) {
                IntervalAnnouncer.sendAnnouncement(p, msg);
            }
        }

    }

    public static void sendTimedPlayerAnnouncement(Plugin plugin, Player p, String msg, int seconds) {
        try {
            (new PlayerMsgTask(p, msg, seconds)).runTaskTimer(plugin, 1L, 10L);
        } catch (IllegalStateException | IllegalArgumentException var5) {
            var5.printStackTrace();
        }

    }
}