package net.supremesurvival.supremecore.landmarks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import java.util.Iterator;
import java.util.List;

import static net.supremesurvival.supremecore.landmarks.PlayerListeners.landmarksDiscovered;

public class LandmarkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args.length == 0) {
                //Help/syntax info

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    Iterator landmarkIterator = LandmarkManager.landmarkList.iterator();
                    while(landmarkIterator.hasNext()){
                        Landmark landmark = (Landmark)landmarkIterator.next();
                        player.sendMessage(landmark.getTitle());
                    }
                }
                //command to allow player to list their discovered landmarks
                if (args[0].equalsIgnoreCase("discovered")){
                    List landmarkList = landmarksDiscovered.get(player.getUniqueId());
                    player.sendMessage(landmarkList.toString());
                }
            }
        }

        return false;
    }

}