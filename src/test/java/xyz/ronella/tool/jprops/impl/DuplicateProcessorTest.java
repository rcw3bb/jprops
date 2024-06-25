package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.MissingCommandException;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.trivial.decorator.TextFile;

import java.io.File;
import java.io.IOException;
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
    public void testDedupe() throws IOException, MissingCommandException {
        final var props = new File("src\\test\\resources\\duplicate-fields.properties");
        props.createNewFile();
        final var textFile = new TextFile(props);

        textFile.setText("""
                field1 = one
                field2 = two
                #comment1

                field1 = one
                field2 = two
                field3 = line1\\
                line2\\
                line3

                   #Comment2

                field4 = four
                field3 = line1\\
                line2\\
                line3
                field5 = five
                """);

        assertTrue(props.exists());
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p",
                props.getAbsolutePath(), "-dedupe"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testNothingToDedupe() throws IOException, MissingCommandException {
        final var props = new File("src\\test\\resources\\duplicate-fields.properties");
        props.createNewFile();
        final var textFile = new TextFile(props);
        textFile.setText("""
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
                """);

        assertTrue(props.exists());
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p",
                props.getAbsolutePath(), "-dedupe"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }
}
