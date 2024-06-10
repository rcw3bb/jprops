package xyz.ronella.util.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.util.jprops.util.ArgsMgr;
import xyz.ronella.util.jprops.util.MissingCommandException;

import java.nio.file.Paths;

public class DuplicateProcessorTest {

    @Test
    public void checkDuplicate() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p", propFile.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void checkNoDuplicate() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "valid.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p", propFile.getAbsolutePath()}));
        assertDoesNotThrow(processor::process);
    }

    @Test
    public void fileNotFound() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "nonexistent.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p", propFile.getAbsolutePath()}));
        assertDoesNotThrow(processor::process);
    }

}
