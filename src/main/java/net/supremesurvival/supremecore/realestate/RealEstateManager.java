package net.supremesurvival.supremecore.realestate;

import net.supremesurvival.supremecore.commonUtils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Reflection-based Towny listing adapter so we can iterate quickly across Towny API versions.
 */
public class RealEstateManager {

    private static final long CACHE_MS = 20_000L;
    private static final String HANDLE = "RealEstate";

    private List<RealEstateListing> cachedListings = new ArrayList<>();
    private long lastRefresh = 0L;

    public List<RealEstateListing> getListings(String townFilter) {
        if (System.currentTimeMillis() - lastRefresh > CACHE_MS || cachedListings.isEmpty()) {
            cachedListings = fetchTownyListings();
            lastRefresh = System.currentTimeMillis();
        }

        if (townFilter == null || townFilter.isBlank()) {
            return cachedListings;
        }

        String needle = townFilter.toLowerCase(Locale.ROOT);
        List<RealEstateListing> out = new ArrayList<>();
        for (RealEstateListing listing : cachedListings) {
            if (listing.townName().toLowerCase(Locale.ROOT).contains(needle)) {
                out.add(listing);
            }
        }
        return out;
    }

    public RealEstateListing getListingById(int id) {
        for (RealEstateListing listing : getListings(null)) {
            if (listing.id() == id) return listing;
        }
        return null;
    }

    public Location resolveTeleportLocation(RealEstateListing listing) {
        World world = Bukkit.getWorld(listing.worldName());
        if (world == null) return null;

        int x = listing.centerX();
        int z = listing.centerZ();
        int y = world.getHighestBlockYAt(x, z) + 1;
        return new Location(world, x + 0.5, y, z + 0.5);
    }

    @SuppressWarnings("unchecked")
    private List<RealEstateListing> fetchTownyListings() {
        try {
            Class<?> universeClass = Class.forName("com.palmergames.bukkit.towny.TownyUniverse");
            Object universe = invokeStatic(universeClass, "getInstance");
            List<Object> towns = (List<Object>) invoke(universe, "getTowns");

            List<RealEstateListing> out = new ArrayList<>();
            int id = 1;
            for (Object town : towns) {
                String townName = String.valueOf(invoke(town, "getName"));
                List<Object> townBlocks = (List<Object>) invoke(town, "getTownBlocks");
                for (Object townBlock : townBlocks) {
                    boolean forSale = asBoolean(invoke(townBlock, "isForSale"));
                    if (!forSale) continue;

                    Double price = asDouble(invokeSafe(townBlock, "getPlotPrice"));
                    if (price == null) {
                        price = asDouble(invokeSafe(townBlock, "getPrice"));
                    }
                    if (price == null) price = 0.0;

                    Object worldCoord = invokeSafe(townBlock, "getWorldCoord");
                    String worldName = "world";
                    int blockX = 0;
                    int blockZ = 0;

                    if (worldCoord != null) {
                        String wn = asString(invokeSafe(worldCoord, "getWorldName"));
                        if (wn != null) worldName = wn;

                        Integer x = asInt(invokeSafe(worldCoord, "getX"));
                        Integer z = asInt(invokeSafe(worldCoord, "getZ"));
                        if (x != null && z != null) {
                            // Towny Coord units are 16x16 by default
                            blockX = x * 16 + 8;
                            blockZ = z * 16 + 8;
                        }
                    }

                    if (blockX == 0 && blockZ == 0) {
                        // Try alternate method names across versions
                        Object coord = invokeSafe(townBlock, "getCoord");
                        if (coord != null) {
                            Integer x = asInt(invokeSafe(coord, "getX"));
                            Integer z = asInt(invokeSafe(coord, "getZ"));
                            if (x != null && z != null) {
                                blockX = x * 16 + 8;
                                blockZ = z * 16 + 8;
                            }
                        }
                    }

                    out.add(new RealEstateListing(id++, townName, worldName, blockX, blockZ, price));
                }
            }

            out.sort(Comparator.comparingDouble(RealEstateListing::price));
            return out;
        } catch (ReflectiveOperationException ex) {
            Logger.sendMessage("Towny reflection lookup failed: " + ex.getMessage(), Logger.LogType.WARN, HANDLE);
            return Collections.emptyList();
        }
    }

    private static Object invoke(Object target, String method) throws Exception {
        Method m = target.getClass().getMethod(method);
        return m.invoke(target);
    }

    private static Object invokeStatic(Class<?> target, String method) throws Exception {
        Method m = target.getMethod(method);
        return m.invoke(null);
    }

    private static Object invokeSafe(Object target, String method) {
        if (target == null) return null;
        try {
            Method m = target.getClass().getMethod(method);
            return m.invoke(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static boolean asBoolean(Object o) {
        return o instanceof Boolean b && b;
    }

    private static Double asDouble(Object o) {
        if (o instanceof Number n) return n.doubleValue();
        return null;
    }

    private static Integer asInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        return null;
    }

    private static String asString(Object o) {
        if (o == null) return null;
        return String.valueOf(o);
    }
}
