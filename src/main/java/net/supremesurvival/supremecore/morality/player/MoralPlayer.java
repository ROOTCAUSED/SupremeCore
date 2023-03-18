package net.supremesurvival.supremecore.morality.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoralPlayer {
    final UUID uuid;
    Integer morality;
    MoralStanding moralStanding;

    public MoralPlayer(UUID uuid, int morality){
        this.uuid = uuid;
        this.morality = morality;

    }
    public Player getPlayer(){
        return Bukkit.getPlayer(this.uuid);
    }
    public int getMorality(){
        return this.morality;
    }

    public void setMorality(Integer morality){
        this.morality = morality;
    }

    public void addMorality(Integer morality){
        this.morality = this.morality + morality;
    }

    public void reduceMorality(Integer morality){
        this.morality = this.morality -morality;
    }
    public MoralStanding getStanding(){
        return moralStanding;
    }

    public void updateMoralStanding(MoralStanding standing){
        this.moralStanding = standing;
    }
    public enum MoralStanding{
        DIVINE,
        SAINT,
        PURE,
        HEROIC,
        RIGHTEOUS,
        VIRTUOUS,
        NOBLE,
        HONOURABLE,
        LAWFUL,
        RESPECTABLE,
        NEUTRAL,
        CHAOTIC,
        CORRUPT,
        DISHONOURABLE,
        INFAMOUS,
        RUTHLESS,
        VILLAINOUS,
        MALEVOLENT,
        WRETCHED,
        DIABOLICAL,
        DEMONIC

    }

}

