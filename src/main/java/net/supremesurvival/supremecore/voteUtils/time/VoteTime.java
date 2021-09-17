package net.supremesurvival.supremecore.voteUtils.time;

import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.*;

public class VoteTime {

    private Set<VotePair<World, UUID>> noVotes = new HashSet<>();
    private Set<VotePair<World, UUID>> yesVotes = new HashSet<>();
    private Set<UUID> yesVote = new HashSet();
    private Set<UUID> noVote = new HashSet();

    public int timeToVote;
    public int voteDelay;
    public List<VotePair<World, Boolean>> activeVotes = new ArrayList<>();
    public List<VotePair<World,Instant>> lastVotes = new ArrayList<>();
    public VotePair<World,Instant> lastVote;
    public VoteUtil voteUtil;
    public List<String> args = new ArrayList<String>();

    public void enable(){
        args.add("yes");args.add("no");
    }
    public void disable(){
        noVote.clear();
        yesVote.clear();

    }
    public boolean getActiveVote(World world){
        if(activeVotes !=null){
            while(activeVotes.iterator().hasNext()){
                if(activeVotes.iterator().next().getKey() == world){
                    return activeVotes.iterator().next().getValue();
                }
            }return false;
        }return false;
    }
    public void setActiveVote(World world, Boolean active){
        if(activeVotes != null){
            while(activeVotes.iterator().hasNext()){
                if(activeVotes.iterator().next().getKey() == world){
                    activeVotes.remove(activeVotes.iterator().next());
                    activeVotes.add(new VotePair<World, Boolean>(world, active));
                }
            }
        }
    }
    public Instant getLastVote(World world) {
        if(lastVotes != null){
            while(lastVotes.iterator().hasNext()){
                if(lastVotes.iterator().next().getKey().getName() == world.getName()){
                    return lastVotes.iterator().next().getValue();
            }
        }} return null;
    }
    public void setLastVote(World world, Instant instant){
        if(lastVotes != null){
            while(lastVotes.iterator().hasNext()) {
                if(lastVotes.iterator().next().getKey() == world){
                    lastVotes.remove(lastVotes.iterator().next());
            }
        }}
        lastVotes.add(new VotePair<World, Instant>(world,instant));
    }

    public List<String> getArgs(){
        return args;
    }
    public Set<UUID> getNoVotes(World world){
        noVote.clear();
        while(noVotes.iterator().hasNext()){
            if(noVotes.iterator().next().getKey() == world){
                noVote.add(noVotes.iterator().next().getValue());
            }
        }
        return noVote;
    }
    public Set<UUID>  getYesVotes(World world){
        yesVote.clear();
        while (yesVotes.iterator().hasNext()){
            if(noVotes.iterator().next().getKey() == world){
                yesVote.add(yesVotes.iterator().next().getValue());
            }
        }
        return yesVote;
    }
}
