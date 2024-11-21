package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.trivial.decorator.TextFile;
import xyz.ronella.trivial.handy.OSType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class MergeProcessorTest {

    @Test
    public void mergeProperties() {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void mergeProperties2() {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source3.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void mergeNothingProperties() {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination2.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void mergeMoreFieldsOnDestinationProperties() {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination3.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testApply() throws IOException {
        final var dstProps = new File("src\\test\\resources\\dest-more-fields.properties");
        dstProps.createNewFile();
        final var textFile = new TextFile(dstProps);
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
                
                field9 = nine
                """);

        assertTrue(dstProps.exists());
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath(), "-apply"}));

        assertDoesNotThrow(processor::process);

        final var output = new TextFile(dstProps.getAbsolutePath());
        final var outputContent = output.getText();
        assertTrue(outputContent.contains("field9"));

        dstProps.delete();
        assertFalse(dstProps.exists());
    }

    @Test
    public void testNothingApply() throws IOException {
        final var dstProps = new File("src\\test\\resources\\equal-fields.properties");
        dstProps.createNewFile();
        final var textFile = new TextFile(dstProps);
        textFile.setText("""
                field5 = five
                field2 = two
                #comment1

                field1 = one
                field4 = Line1\\
                Line2
                field3 = three

                FIELD6 = six
                FIELD7 = SEVEN(7)
                FIEDL8 = eight
                """);

        assertTrue(dstProps.exists());
        final var srcProps = Paths.get(".", "src", "test", "resources", "source2.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath(), "-apply"}));

        assertDoesNotThrow(processor::process);
        dstProps.delete();
        assertFalse(dstProps.exists());
    }

    @Test
    public void testLinuxUTF16() {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source-linux-utf16.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination-linux-utf16.properties").toFile();
        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", dstProps.getAbsolutePath(), "-encoding", StandardCharsets.UTF_16LE.name()}));

        assertDoesNotThrow(processor::process);
    }

    @Test
    public void testApplyUTF16() throws IOException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "source-linux-utf16.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "destination-linux-utf16.properties").toFile();
        final var dstPropsTextFile = new TextFile(dstProps, StandardCharsets.UTF_16LE);
        final var props = Paths.get(".", "src", "test", "resources", "merge-linux-utf16.properties").toFile();
        props.createNewFile();
        final var textFile = new TextFile(props, StandardCharsets.UTF_16LE);

        textFile.setText(dstPropsTextFile.getText());

        assertTrue(props.exists());

        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", props.getAbsolutePath(), "-encoding", StandardCharsets.UTF_16LE.name(), "-apply"}));

        assertDoesNotThrow(processor::process);
        props.delete();
        assertFalse(props.exists());
    }

    private String readTextFile(final File file) {
        final var textContent = new StringBuilder();
        try (final var reader = new FileReader(file)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                textContent.append((char) ch);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textContent.toString();
    }

    @Test
    public void testApplyWindowsToLinux() throws IOException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "valid-windows.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "valid-linux.properties").toFile();
        final var dstPropsTextFile = new TextFile(dstProps, OSType.LINUX.getEOL());
        final var props = Paths.get(".", "src", "test", "resources", "merge-linux.properties").toFile();
        props.createNewFile();
        final var textFile = new TextFile(props, dstPropsTextFile.getEndOfLine());
        final var expectedContent = "field1 = one\nfield2 = two\nfield3 = line1\\\nline2\\\nline3\nfield4 = 4\nfield5 = five\n";

        textFile.setText(dstPropsTextFile.getText());

        assertTrue(props.exists());

        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", props.getAbsolutePath(), "-apply", "-os", "unix"}));

        assertDoesNotThrow(processor::process);
        final var propsContent = readTextFile(props);

        assertEquals(expectedContent, propsContent);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testApplyWindowsToUnknown() throws IOException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "valid-windows.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "valid-linux.properties").toFile();
        final var dstPropsTextFile = new TextFile(dstProps, OSType.LINUX.getEOL());
        final var props = Paths.get(".", "src", "test", "resources", "merge-linux.properties").toFile();
        props.createNewFile();
        final var textFile = new TextFile(props, dstPropsTextFile.getEndOfLine());
        final var expectedContent = "field1 = one\nfield2 = two\nfield3 = line1\\\nline2\\\nline3\nfield4 = 4\nfield5 = five\n";

        textFile.setText(dstPropsTextFile.getText());

        assertTrue(props.exists());

        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", props.getAbsolutePath(), "-apply", "-os", "unk"}));

        assertDoesNotThrow(processor::process);
        final var propsContent = readTextFile(props);

        assertEquals(expectedContent, propsContent);
        props.delete();
        assertFalse(props.exists());
    }

    @Test
    public void testApplyWindowsToLinuxProp() throws IOException {
        final var srcProps = Paths.get(".", "src", "test", "resources", "valid-windows.properties").toFile();
        final var dstProps = Paths.get(".", "src", "test", "resources", "valid-linux.properties").toFile();
        final var dstPropsTextFile = new TextFile(dstProps, OSType.LINUX.getEOL());
        final var props = Paths.get(".", "src", "test", "resources", "merge-linux.properties").toFile();
        props.createNewFile();
        final var textFile = new TextFile(props, dstPropsTextFile.getEndOfLine());
        final var expectedContent = "field1 = one\nfield2 = two\nfield3 = line1\\\nline2\\\nline3\nfield4 = 4\nfield5 = five\n";

        textFile.setText(dstPropsTextFile.getText());

        assertTrue(props.exists());

        final var processor = new MergeProcessor(ArgsMgr.build(new String[] {"merge", "-sp", srcProps.getAbsolutePath(),
                "-dp", props.getAbsolutePath(), "-apply"}));

        assertDoesNotThrow(processor::process);
        final var propsContent = readTextFile(props);

        assertEquals(expectedContent, propsContent);
        props.delete();
        assertFalse(props.exists());
    }

}
