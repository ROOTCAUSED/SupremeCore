package net.supremesurvival.supremecore.artefacts;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ArtefactsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();
            if(args.length == 0){
                //Help/syntax info

            } else if(args.length == 1){
                if(args[0].equalsIgnoreCase("retrieve")){
                    if(player.hasPermission("artefacts.retrieve")){
                        Iterator artefactIterator= ArtefactManager.artefacts.iterator();
                        while(artefactIterator.hasNext()){
                            Artefact artefact = (Artefact) artefactIterator.next();
                            player.getInventory().addItem(artefact.item);
                        }
                    }
                }else if(args[0].equalsIgnoreCase("add")){
                    if(player.hasPermission("artefacts.add")){
                        if(player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK){

                        }                    }
                    //Command to take written book from hand and add to tomemanager list.
                }
            }

        }

        return false;
    }
}

