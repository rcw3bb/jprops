package xyz.ronella.util.jprops.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.util.jprops.Command;
import xyz.ronella.util.jprops.impl.MissingCommandException;

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
    public void dupList() throws MissingCommandException {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "--list").toArray(new String[] {}));
        assertTrue(argsMgr.shouldList());
    }

    @Test
    public void dupHelp() throws MissingCommandException {
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-h").toArray(new String[] {}));
        assertEquals(Command.DUPLICATE, argsMgr.getCommand());
    }
}
