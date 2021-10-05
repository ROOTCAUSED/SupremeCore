package net.supremesurvival.supremecore.commonUtils.tomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class TomesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();
            if(args.length == 0){
                //Help/syntax info

            } else if(args.length == 1){
                if(args[0].equalsIgnoreCase("retrieve")){
                    Iterator tomesIterator = TomeManager.tomes.iterator();
                    while(tomesIterator.hasNext()){
                        Tome tome = (Tome)tomesIterator.next();
                        player.getInventory().addItem(tome.tome);
                    }
                }
            }

        }

        return false;
    }
}
