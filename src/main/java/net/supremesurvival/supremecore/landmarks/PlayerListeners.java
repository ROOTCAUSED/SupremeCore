package net.supremesurvival.supremecore.landmarks;

import net.citizensnpcs.api.CitizensAPI;
import net.supremesurvival.supremecore.commonUtils.TitleUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

import static net.supremesurvival.supremecore.landmarks.LandmarkManager.landmarkList;

public class PlayerListeners implements Listener {
    private static final HashMap<UUID, Landmark> playersInLandmarks = new HashMap<>();
    public static HashMap<UUID, List<String>> landmarksDiscovered = new HashMap<>();

    @EventHandler
    public void join(PlayerJoinEvent event) {
        landmarksDiscovered.putIfAbsent(event.getPlayer().getUniqueId(), new ArrayList<>());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;

        // Ignore movement-less events.
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        // Ignore NPCs if Citizens is present.
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens") && CitizensAPI.getNPCRegistry().isNPC(player)) {
            return;
        }

        Location to = event.getTo();

        Landmark current = playersInLandmarks.get(player.getUniqueId());
        if (current != null) {
            if (!isInside(current, to)) {
                TitleUtility.sendPlayer("You have exited " + current.getTitle(), "", 5, 20, 5, player);
                playersInLandmarks.remove(player.getUniqueId());
            } else {
                return;
            }
        }

        for (Landmark landmark : landmarkList) {
            if (!player.getWorld().getName().equalsIgnoreCase(landmark.getWorldName())) {
                continue;
            }

            if (landmark.isDiscoveredAt(to.getBlockX(), to.getBlockY(), to.getBlockZ())) {
                if (!hasDiscovered(player, landmark.getID())) {
                    discoverLandmark(player, landmark);
                }
                playersInLandmarks.put(player.getUniqueId(), landmark);
                break;
            }
        }
    }

    private boolean isInside(Landmark landmark, Location loc) {
        return landmark.getRegion().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public void discoverLandmark(Player player, Landmark landmark) {
        List<String> landmarksPlayer = landmarksDiscovered.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (!landmarksPlayer.contains(landmark.getID())) {
            landmarksPlayer.add(landmark.getID());
            landmarksDiscovered.put(player.getUniqueId(), landmarksPlayer);
            TitleUtility.sendPlayer("Landmark Discovered", landmark.getTitle(), 10, 40, 10, player);
            if (!landmark.getAnnouncement().isBlank()) {
                player.sendMessage(landmark.getAnnouncement());
            }
        }
    }

    public boolean hasDiscovered(Player player, String landmarkID) {
        List<String> discovered = landmarksDiscovered.get(player.getUniqueId());
        return discovered != null && discovered.contains(landmarkID);
    }
}
