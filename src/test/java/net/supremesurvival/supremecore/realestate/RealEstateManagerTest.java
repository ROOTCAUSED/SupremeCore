package net.supremesurvival.supremecore.realestate;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RealEstateManagerTest {

    @Test
    void filtersListingsByTownNameCaseInsensitive() throws Exception {
        RealEstateManager manager = new RealEstateManager();
        List<RealEstateListing> listings = new ArrayList<>();
        listings.add(new RealEstateListing(1, "Riverton", "world", 10, 10, 100.0));
        listings.add(new RealEstateListing(2, "Darkvale", "world", 20, 20, 50.0));

        setPrivateField(manager, "cachedListings", listings);
        setPrivateField(manager, "lastRefresh", System.currentTimeMillis());

        List<RealEstateListing> filtered = manager.getListings("RIVER");

        assertEquals(1, filtered.size());
        assertEquals(1, filtered.get(0).id());
    }

    @Test
    void returnsListingByIdFromCache() throws Exception {
        RealEstateManager manager = new RealEstateManager();
        List<RealEstateListing> listings = new ArrayList<>();
        listings.add(new RealEstateListing(3, "Stonehelm", "world", 5, 5, 250.0));

        setPrivateField(manager, "cachedListings", listings);
        setPrivateField(manager, "lastRefresh", System.currentTimeMillis());

        RealEstateListing found = manager.getListingById(3);
        RealEstateListing missing = manager.getListingById(99);

        assertNotNull(found);
        assertEquals("Stonehelm", found.townName());
        assertNull(missing);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
