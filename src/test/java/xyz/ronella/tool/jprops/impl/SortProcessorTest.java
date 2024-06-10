package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.MissingCommandException;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.nio.file.Paths;

public class SortProcessorTest {

    @Test
    public void unsortedProperties() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "unsorted.properties").toFile();
        final var processor = new SortProcessor(ArgsMgr.build(new String[] {"sort", "-p", propFile.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void sortedProperties() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "sorted.properties").toFile();
        final var processor = new SortProcessor(ArgsMgr.build(new String[] {"sort", "-p", propFile.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

}
