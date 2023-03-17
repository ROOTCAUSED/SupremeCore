package net.supremesurvival.supremecore.commandUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
//needs work
public class SupremeTabs implements TabCompleter {
    List<String> arguments = new ArrayList<String>();

    public SupremeTabs(List cmdargs){
        arguments = cmdargs;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (arguments.isEmpty()){
            arguments.add("sun");arguments.add("rain");arguments.add("storm");
        }
        List<String> results = new ArrayList<String>();
        if(args.length == 1){
            for(String a : arguments) {
                if(a.toLowerCase().startsWith(args[0].toLowerCase()))
                    results.add(a);
            }
            return results;
        }

        return null;
    }
}
