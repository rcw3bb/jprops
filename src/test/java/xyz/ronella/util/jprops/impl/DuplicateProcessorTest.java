package xyz.ronella.util.jprops.impl;

import org.junit.jupiter.api.Test;
import xyz.ronella.util.jprops.JPropsException;
import xyz.ronella.util.jprops.meta.MetaGenerator;
import xyz.ronella.util.jprops.util.ArgsMgr;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class DuplicateProcessorTest {

    @Test
    public void checkDuplicate() {
        final var propFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {}));
        processor.process(propFile);
    }

    @Test
    public void checkNoDuplicate() {
        final var propFile = Paths.get(".", "src", "test", "resources", "valid.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {}));
        processor.process(propFile);
    }

    @Test
    public void fileNotFound() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "nonexistent.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {}));
        processor.process(propsFile);
    }

}
