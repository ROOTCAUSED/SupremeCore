package net.supremesurvival.supremecore.commonUtils.morality.player;

import org.bukkit.entity.Player;

public class MoralPlayer {
    Player player;
    Integer morality;
    MoralStanding moralStanding;

    public MoralPlayer(Player player, int morality, MoralStanding moralStanding){
        this.player = player;
        this.morality = morality;
        this.moralStanding = moralStanding;

    }
    public Player getPlayer(){
        return this.player;
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
        NEUTRAL,
        EVIL,
        GOOD,
        DARK,
    }
}

