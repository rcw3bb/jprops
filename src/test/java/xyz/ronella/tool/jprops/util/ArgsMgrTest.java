package xyz.ronella.tool.jprops.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.Command;

import java.io.File;
import java.util.List;


public class ArgsMgrTest {

    @Test
    public void helpCommand() throws MissingCommandException {
        final var argsMgr = ArgsMgr.build(List.of(Command.HELP.name()).toArray(new String[] {}));
        assertEquals(Command.HELP, argsMgr.getCommand());
    }

    @Test
    public void dupCommand() throws MissingCommandException {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props).toArray(new String[] {}));
        assertEquals(new File(props), argsMgr.getProps());
    }

    @Test
    public void dedupe() throws MissingCommandException {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "-dedupe").toArray(new String[] {}));
        assertTrue(argsMgr.isDedupe());
    }

    @Test
    public void dupHelp() throws MissingCommandException {
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-h").toArray(new String[] {}));
        assertEquals(Command.DUPLICATE, argsMgr.getCommand());
    }
}
