package net.supremesurvival.supremecore.voteUtils.time;

import net.supremesurvival.supremecore.SupremeCore;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.*;

public class VoteTime {

    private Set<UUID>noVote = new HashSet<>();
    private Set<UUID>yesVote = new HashSet<>();

    public int timeToVote;
    public boolean isVoteActive;
    public int voteDelay;
    public Instant lastVote;
    public VoteUtil voteUtil;
    public List<String> args = new ArrayList<String>();
    public World voteWorld;

    public void enable(){
        args.add("yes");args.add("no");
    }
    public void disable(){
        noVote.clear();
        yesVote.clear();

    }
    public List<String> getArgs(){
        return args;
    }
    public Set<UUID> getNoVote(){
        return noVote;
    }
    public Set<UUID> getYesVote(){
        return yesVote;
    }
}
