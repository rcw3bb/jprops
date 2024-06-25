package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.MissingCommandException;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.trivial.decorator.TextFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @Test
    public void testApply() throws IOException, MissingCommandException {
        final var props = new File("src\\test\\resources\\unsorted-fields.properties");
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
        final var processor = new SortProcessor(ArgsMgr.build(new String[] {"sort", "-p",
                props.getAbsolutePath(), "-apply"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testNothingToApply() throws IOException, MissingCommandException {
        final var props = new File("src\\test\\resources\\sorted-fields.properties");
        props.createNewFile();
        final var textFile = new TextFile(props);
        textFile.setText("""
                FIELD6 = six
                FIELD7 = seven
                field1 = one
                field2 = two
                field3 = line1\\
                line2\\
                line3
                field4 = four
                field5 = five
                """);

        assertTrue(props.exists());
        final var processor = new SortProcessor(ArgsMgr.build(new String[] {"sort", "-p",
                props.getAbsolutePath(), "-apply"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testLinuxUTF16() throws IOException, MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "unsorted-linux-utf16.properties").toFile();
        final var processor = new SortProcessor(ArgsMgr.build(new String[] {"sort", "-p", propFile.getAbsolutePath(), "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testApplyUTF16() throws IOException, MissingCommandException {
        final var utf16Props = new File("src\\test\\resources\\unsorted-linux-utf16.properties");
        final var utf16TextFile = new TextFile(utf16Props, StandardCharsets.UTF_16LE);
        final var props = new File("src\\test\\resources\\unsorted-fields-dummy.properties");
        props.createNewFile();
        final var textFile = new TextFile(props, StandardCharsets.UTF_16LE);

        textFile.setText(utf16TextFile.getText());

        assertTrue(props.exists());
        final var processor = new SortProcessor(ArgsMgr.build(new String[] {"sort", "-p",
                props.getAbsolutePath(), "-apply", "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }
}
