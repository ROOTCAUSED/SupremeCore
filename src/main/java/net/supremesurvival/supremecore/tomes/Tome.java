package net.supremesurvival.supremecore.tomes;

import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//This class will allow us to add lore tomes to the server. Lore tomes will be ingame Written_Books (by default can only
//be written by a player and have limited use) which are filled from a config.yml file.
//We can use this feature to add rich ingame lore via a library of books being added to the loot tables of the game.
public class Tome {
    final ItemStack tome = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta tomeMeta = (BookMeta)tome.getItemMeta();

    final static String handle = "Tome";
    String preamble;
    public Tome(String author, String title, String message, List<String> lore, String preamble){
        Logger.sendMessage("Creating Tome: " + title, Logger.LogType.INFO, handle);
        tomeMeta.setAuthor(author);
        tomeMeta.setTitle(title);
        this.preamble = preamble;
        addPages(getPages(message));
        Iterator newLoreIterator = lore.iterator();
        List<String> tempLore = new ArrayList<String>();
        if(tomeMeta.hasLore()){
            //getlore is deprecated, will need to replace with .lore, this returns a list of components not a list of strings so will need to investigate the implications.
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
        Logger.sendMessage("Tome: " + title + " created", Logger.LogType.INFO, handle);

    }
    public String getName(){
        return this.tomeMeta.getTitle();
    }
    public String coverPage(String preamble){

        return "\n\n" + tomeMeta.getTitle()+"\n\n"+preamble+"\n\n"+"By"+"\n\n"+tomeMeta.getAuthor();
    }
    //compiles a longer string into short 250 character strings, and then further splits the end of that string to ensure that
    //pages do not carry over (as that looks crap). New line characters mess up automatic formatting of book pages
    //Book pages can store roughly 250 characters, but new lines will throw that count off. May attempt to counter this
    //by checking for nl and for each found in a given page reducing the character count of that page by ~29-30. For now, removing all
    //new lines is an acceptable middleground.
    public List<String> compilePages(String message){
        //message = replaceNewLines(message);
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
        return message.replace("\n", "");
    }
    //Used to add all preprocessed pages to book entity
    public void addPages(List<String> pages){
        Iterator pageIterator = pages.iterator();
        tomeMeta.addPage(coverPage(preamble));
        while(pageIterator.hasNext()){
            tomeMeta.addPage((String)pageIterator.next());
        }
    }
    private static List<String> getPages(String rawText){
        rawText = "\n" + rawText;
        List<String> pages = new ArrayList<String>();

        List<String> lines = getLines(rawText);
        String pageText = "";
        for(int i = 1; i < lines.size(); i++){
            pageText += lines.get(i);
            if(i != 1 && i % 14 == 0){
                pages.add(pageText);
                pageText= "";
            }
        }
        if(!pageText.isEmpty())
            pages.add(pageText);
        return pages;
    }

    private static List<String> getLines(String rawText){
        final MinecraftFont font = new MinecraftFont();
        final int maxLineWidth = font.getWidth("LLLLLLLLLLLLLLLLLLL"); //should return a full line in minecraft font, 113 px
        List<String> lines = new ArrayList<>();
        try{
            for(String section : rawText.split("\n")){
                if(section.equals(""))
                    lines.add("\n");
                else {
                    String[] words = ChatColor.stripColor(section).split(" ");
                    String line = "";
                    for(int index = 0; index < words.length; index ++){
                        String word = words[index];
                        if(line.isEmpty()) {
                            line = word;
                            continue;
                        }
                        int spaces = 0;
                        if(font.getWidth(" ") == 2){
                            spaces = 1;
                            for(int i = 0; i < line.length(); ++i)
                                if(line.charAt(i) == ' ')
                                    spaces++;
                        }
                        if(font.getWidth(line + " " + word) + spaces > maxLineWidth) {
                            lines.add(line + '\n');
                            line = word;
                            continue;
                        }

                        line += " " + word;
                    }
                    if(!line.equals("")){
                        lines.add(line + "\n");
                    }
                }
            }
        }catch (IllegalArgumentException exception){
            lines.clear();
            Logger.sendMessage(exception.toString(), Logger.LogType.INFO,handle);
        }
        return lines;
    }


}
