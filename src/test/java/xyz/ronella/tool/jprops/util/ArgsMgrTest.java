package xyz.ronella.tool.jprops.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.Command;
import xyz.ronella.trivial.handy.OSType;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
    public void dedupe() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "-dedupe").toArray(new String[] {}));
        assertTrue(argsMgr.isDedupe());
    }

    @Test
    public void dupHelp() {
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-h").toArray(new String[] {}));
        assertEquals(Command.DUPLICATE, argsMgr.getCommand());
    }

    @Test
    public void linuxOS() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "-os", "linux").toArray(new String[] {}));
        assertEquals(OSType.LINUX, argsMgr.getTargetOS());
    }

    @Test
    public void invalidOS() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "-os", "invalid").toArray(new String[] {}));
        assertNull(argsMgr.getTargetOS());
    }

    @Test
    public void validEncoding() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "-encoding", "utf-8").toArray(new String[] {}));
        assertEquals(StandardCharsets.UTF_8, argsMgr.getEncoding());
    }

    @Test
    public void invalidEncoding() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "-p", props, "-encoding", "1234").toArray(new String[] {}));
        assertNull(argsMgr.getEncoding());
    }

    @Test
    public void extraParams() {
        final var props = "non-existent.properties";
        final var argsMgr = ArgsMgr.build(List.of(Command.DUPLICATE.name(), "extra", "-p", props).toArray(new String[] {}));
        assertNull(argsMgr.getProps());
    }

}
