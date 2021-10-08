package net.supremesurvival.supremecore.commonUtils.landmarks;

import net.supremesurvival.supremecore.commonUtils.ConfigUtility;

import java.util.HashMap;
import java.util.UUID;

public class LandmarkManager {
    private static HashMap<UUID, Landmark> moralManagerList;


    public static void enable(){
        ConfigUtility.getModuleConfig("Landmarks");
    }

}
