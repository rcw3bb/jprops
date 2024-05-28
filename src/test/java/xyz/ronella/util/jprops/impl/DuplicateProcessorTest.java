package xyz.ronella.util.jprops.impl;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class DuplicateProcessorTest {

    @Test
    public void checkDuplicate() {
        final var propFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var processor = new DuplicateProcessor();
        processor.process(propFile);
    }

}
