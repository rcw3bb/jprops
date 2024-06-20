package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.TextWriter;
import xyz.ronella.tool.jprops.util.MissingCommandException;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class BrokenMLineProcessorTest {
    @Test
    public void bmLineProcessor() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "multiline.properties").toFile();
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p", propFile.getAbsolutePath()}));
        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testFix() throws IOException, MissingCommandException {
        final var props = new File("src\\test\\resources\\multiline-fields.properties");
        props.createNewFile();

        TextWriter.write(props, """
                mline1 = ml1 line1
                ml1 line2
                ml1 line3
                #Comment1
                 #Comment2
                mline2 = ml2 line1
                ml2 line2
                ml2 line3

                mline3 = ml3 line1
                ml3 line2
                ml3 line3
                mline4 = ml4 line1 \\
                ml4 line2 \\
                ml4 line3
                """);

        assertTrue(props.exists());
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p",
                props.getAbsolutePath(), "-fix"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testNothingToFix() throws IOException, MissingCommandException {
        final var props = new File("src\\test\\resources\\fixed-multiline-fields.properties");
        props.createNewFile();

        TextWriter.write(props, """
                mline1 = ml1 line1\\
                ml1 line2\\
                ml1 line3
                #Comment1
                   #Comment2
                mline2 = ml2 line1\\
                ml2 line2\\
                ml2 line3

                mline3 = ml3 line1\\
                ml3 line2\\
                ml3 line3
                mline4 = ml4 line1 \\
                ml4 line2 \\
                ml4 line3
                """);

        assertTrue(props.exists());
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p",
                props.getAbsolutePath(), "-fix"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }
}
