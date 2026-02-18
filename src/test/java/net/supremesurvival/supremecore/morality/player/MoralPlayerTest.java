package net.supremesurvival.supremecore.morality.player;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoralPlayerTest {

    @Test
    void constructorAndMutatorsWork() {
        UUID id = UUID.randomUUID();
        MoralPlayer player = new MoralPlayer(id, 10, "NEUTRAL", 100L);

        assertEquals(id, player.getUuid());
        assertEquals(10, player.getMorality());
        assertEquals("NEUTRAL", player.getStanding());
        assertEquals(100L, player.getLastUpdatedEpochSeconds());

        player.setMorality(-25);
        player.setStanding("CORRUPT");
        player.setLastUpdatedEpochSeconds(250L);

        assertEquals(-25, player.getMorality());
        assertEquals("CORRUPT", player.getStanding());
        assertEquals(250L, player.getLastUpdatedEpochSeconds());
    }

    @Test
    void backwardsCompatibleConstructorDefaultsStandingAndTimestamp() {
        UUID id = UUID.randomUUID();
        MoralPlayer player = new MoralPlayer(id, 42);

        assertEquals(id, player.getUuid());
        assertEquals(42, player.getMorality());
        assertEquals("NEUTRAL", player.getStanding());
    }
}
