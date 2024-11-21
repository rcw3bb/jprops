package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.trivial.decorator.TextFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class DuplicateProcessorTest {

    @Test
    public void checkDuplicate()  {
        final var propFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p", propFile.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void checkNoDuplicate() {
        final var propFile = Paths.get(".", "src", "test", "resources", "valid.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p", propFile.getAbsolutePath()}));
        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testDedupe() throws IOException {
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
    public void testDedupeUTF16() throws IOException {
        final var utf16Props = new File("src\\test\\resources\\duplicate-linux-utf16.properties");
        final var utf16TextFile = new TextFile(utf16Props, StandardCharsets.UTF_16LE);
        final var props = new File("src\\test\\resources\\duplicate-fields-dummy.properties");
        props.createNewFile();
        final var textFile = new TextFile(props, StandardCharsets.UTF_16LE);

        textFile.setText(utf16TextFile.getText());

        assertTrue(props.exists());
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p",
                props.getAbsolutePath(), "-dedupe", "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testNothingToDedupe() throws IOException {
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

    @Test
    public void testLinuxUTF16() {
        final var propFile = Paths.get(".", "src", "test", "resources", "duplicate-linux-utf16.properties").toFile();
        final var processor = new DuplicateProcessor(ArgsMgr.build(new String[] {"duplicate", "-p", propFile.getAbsolutePath(), "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
    }
}
