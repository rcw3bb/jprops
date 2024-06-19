package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.FileMgr;
import xyz.ronella.tool.jprops.util.MissingCommandException;
import xyz.ronella.trivial.decorator.TextFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    @Test
    public void mergeMoreFieldsOnDestinationApplyTest() throws IOException, MissingCommandException {
        final var dstProps = new File("src\\test\\resources\\dest-more-fields.properties");
        dstProps.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dstProps, true))) {
            writer.write("""
                    field5 = five
                    field2 = two
                    #comment1
                                        
                    field1 = one
                    field4 = four
                    field3 = line1\\
                    line2\\
                    line3
                                        
                    FIELD6 = six
                    FIELD7 = seven
                                        
                    field9 = nine
                    """);
        }

        assertTrue(dstProps.exists());
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath(), "-apply"}));

        assertDoesNotThrow(processor::process);
        dstProps.delete();
        assertFalse(dstProps.exists());
    }

}
