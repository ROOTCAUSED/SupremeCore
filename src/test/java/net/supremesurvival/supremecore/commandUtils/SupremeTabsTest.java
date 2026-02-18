package net.supremesurvival.supremecore.commandUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SupremeTabsTest {

    @Test
    void tabCompletesFromProvidedArguments() {
        SupremeTabs tabs = new SupremeTabs(Arrays.asList("status", "start", "stop"));
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        List<String> result = tabs.onTabComplete(sender, command, "test", new String[]{"st"});

        assertNotNull(result);
        assertTrue(result.contains("status"));
        assertTrue(result.contains("start"));
        assertTrue(result.contains("stop"));
    }

    @Test
    void returnsNullForSecondArgAndBeyond() {
        SupremeTabs tabs = new SupremeTabs(Arrays.asList("one", "two"));
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        List<String> result = tabs.onTabComplete(sender, command, "test", new String[]{"one", "two"});

        assertNull(result);
    }
}
