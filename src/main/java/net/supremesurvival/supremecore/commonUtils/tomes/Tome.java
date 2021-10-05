package net.supremesurvival.supremecore.commonUtils.tomes;

import com.mysql.cj.log.Log;
import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tome {
    ItemStack tome = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta tomeMeta = (BookMeta)tome.getItemMeta();
    String preamble;
    public Tome(String author, String title, String message, List<String> lore, String preamble){
        Logger.sendMessage("Creating Tome: " + title, Logger.LogType.INFO, "Tomes");
        tomeMeta.setAuthor(author);
        tomeMeta.setTitle(title);
        this.preamble = preamble;
        addPages(compilePages(message));
        Iterator newLoreIterator = lore.iterator();
        List<String> tempLore = new ArrayList<String>();
        if(tomeMeta.hasLore()){
            tempLore = tomeMeta.getLore();
            while(newLoreIterator.hasNext()){
                String formatted = ChatColor.translateAlternateColorCodes('&',(String)newLoreIterator.next());
                tempLore.add(formatted);
            }
            tomeMeta.setLore(tempLore);
        }else{
            while(newLoreIterator.hasNext()){
                String formatted = ChatColor.translateAlternateColorCodes('&',(String)newLoreIterator.next());
                tempLore.add(formatted);
                tomeMeta.setLore(tempLore);
            }
        }
        tome.setItemMeta(tomeMeta);
        Logger.sendMessage("Tome: " + title + " created", Logger.LogType.INFO, "Tomes");

    }
    public String getName(){
        return this.tomeMeta.getTitle();
    }
    public String coverPage(String preamble){
        String page = "\n\n" + tomeMeta.getTitle()+"\n\n"+preamble+"\n\n"+"By"+"\n\n"+tomeMeta.getAuthor();

        return page;
    }
    public List<String> compilePages(String message){
        Logger.sendMessage(message, Logger.LogType.INFO, "Tomes");
        List<String> pages = new ArrayList<>();
        if(message.length() < 250){
            pages.add(message);
            return pages;
        }
        int position = 0;
        int position2 = 0;
        int arrayLength = 250;
        int stringLength = message.length();
        while(position + arrayLength <stringLength){
            String tmp = message.substring(position, position + arrayLength);
            String tmp2 = message.substring(position, position+ tmp.lastIndexOf(" "));
            pages.add(tmp2);
            position += tmp.lastIndexOf(" ");
        } pages.add(message.substring(position, stringLength));

        return pages;
    }
    public void replaceNewLines(String message){

    }
    //Used to add all preprocessed pages to book entity
    public void addPages(List<String> pages){
        Iterator pageIterator = pages.iterator();
        tomeMeta.addPage(coverPage(preamble));
        while(pageIterator.hasNext()){
            tomeMeta.addPage((String)pageIterator.next());
        }
    }


}
