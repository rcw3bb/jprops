package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.MissingCommandException;

import java.nio.file.Paths;

public class MergeProcessorTest {

    @Test
    public void mergeProperties() throws MissingCommandException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void mergeNothingProperties() throws MissingCommandException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination2.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void mergeMoreFieldsOnDestinationProperties() throws MissingCommandException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination3.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

}
