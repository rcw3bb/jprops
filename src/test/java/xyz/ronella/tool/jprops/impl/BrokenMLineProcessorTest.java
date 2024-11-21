package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.trivial.decorator.TextFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class BrokenMLineProcessorTest {
    @Test
    public void bmLineProcessor() {
        final var propFile = Paths.get(".", "src", "test", "resources", "multiline.properties").toFile();
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p", propFile.getAbsolutePath()}));
        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testFix() throws IOException {
        final var props = new File("src\\test\\resources\\multiline-fields.properties");
        props.createNewFile();
        final var textFile = new TextFile(props);
        textFile.setText("""
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
    public void testNothingToFix() throws IOException {
        final var props = new File("src\\test\\resources\\fixed-multiline-fields.properties");
        props.createNewFile();
        final var textFile = new TextFile(props);
        textFile.setText("""
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

    @Test
    public void testLinuxUTF16() {
        final var propFile = Paths.get(".", "src", "test", "resources", "multiline-linux-utf16.properties").toFile();
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p", propFile.getAbsolutePath(), "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testFixUTF16() throws IOException {
        final var utf16Props = new File("src\\test\\resources\\multiline-linux-utf16.properties");
        final var utf16TextFile = new TextFile(utf16Props, StandardCharsets.UTF_16LE);
        final var props = new File("src\\test\\resources\\multiline-fields-dummy.properties");
        props.createNewFile();
        final var textFile = new TextFile(props, StandardCharsets.UTF_16LE);

        textFile.setText(utf16TextFile.getText());

        assertTrue(props.exists());
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p",
                props.getAbsolutePath(), "-fix", "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }
}
