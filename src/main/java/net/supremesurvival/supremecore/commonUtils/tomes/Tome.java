package net.supremesurvival.supremecore.commonUtils.tomes;

import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//This class will allow us to add lore tomes to the server. Lore tomes will be ingame Written_Books (by default can only
//be written by a player and have limited use) which are filled from a config.yml file.
//We can use this feature to add rich ingame lore via a library of books being added to the loot tables of the game.
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
    //compiles a longer string into short 250 character strings, and then further splits the end of that string to ensure that
    //pages do not carry over (as that looks crap). New line characters mess up automatic formatting of book pages
    //Book pages can store roughly 250 characters, but new lines will throw that count off. May attempt to counter this
    //by checking for nl and for each found in a given page reducing the character count of that page by ~29-30. For now, removing all
    //new lines is an acceptable middleground.
    public List<String> compilePages(String message){
        Logger.sendMessage(message, Logger.LogType.INFO, "Tomes");
        message = replaceNewLines(message);
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
    //hacky fix but it works. Will revisit this
    public String replaceNewLines(String message){
        String tmpString = message.replace("\n", "");
        return tmpString;
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
