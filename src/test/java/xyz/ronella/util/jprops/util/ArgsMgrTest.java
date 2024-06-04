package xyz.ronella.util.jprops.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.util.jprops.Command;

import java.io.File;
import java.util.List;


public class ArgsMgrTest {

    @Test
    public void helpCommand() {
        final var argsMgr = ArgsMgr.build(List.of(Command.HELP.name()).toArray(new String[] {}));
        assertEquals(Command.HELP, argsMgr.getCommand());
    }

    @Test
    public void dupCommand() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props).toArray(new String[] {}));
        assertEquals(new File(props), argsMgr.getProps());
    }

    @Test
    public void dupList() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "--list").toArray(new String[] {}));
        assertTrue(argsMgr.shouldList());
    }

    @Test
    public void dupHelp() {
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-h").toArray(new String[] {}));
        assertEquals(Command.DUPLICATE, argsMgr.getCommand());
    }
}
