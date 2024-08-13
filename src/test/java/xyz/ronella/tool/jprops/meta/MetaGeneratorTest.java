package xyz.ronella.tool.jprops.meta;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.trivial.handy.EndOfLine;
import xyz.ronella.trivial.handy.OSType;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

public class MetaGeneratorTest {

    @Test
    public void validateKeyOrder() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = new String[] {"field1 ", "field2 ", "field3 ", "field4 ", "field5 "};
        final var actual = metaGen.getMetadata().entrySet().stream()
                .filter(___entrySet -> ___entrySet.getValue().lineType()==LineType.VALUE_PAIR)
                .map(Map.Entry::getKey)
                .toArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void validateFieldValueCount() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = new Integer[] {2, 2, 2, 1, 1};
        final var actual = metaGen.getMetadata().values().stream()
                .filter(___metadata -> ___metadata.lineType() == LineType.VALUE_PAIR)
                .map(PropsMeta::count).toList().toArray(new Integer[] {});
        assertArrayEquals(expected, actual);
    }

    @Test
    public void validateCommentCount() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = 2;
        final var actual = metaGen.getMetadata().values().stream()
                .filter(___metadata -> ___metadata.lineType() == LineType.COMMENT)
                .count();

        assertEquals(expected, actual);
    }

    @Test
    public void validateTextCount() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = 3;
        final var actual = metaGen.getMetadata().values().stream()
                .filter(___metadata -> ___metadata.lineType() == LineType.TEXT)
                .count();

        assertEquals(expected, actual);
    }

    @Test
    public void multiLines() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "multiline.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = 4;
        final var metadata = metaGen.getMetadata();
        final var actual = metadata.values().stream()
                .filter(___metadata -> ___metadata.lineType() == LineType.VALUE_PAIR)
                .count();
        final var l4PropsMeta = metadata.get("mline4 ");
        final var l4LineEnding = l4PropsMeta.osType().getEOL().eol();
        final var l4ExpectedValue = String.format(" ml4 line1 \\%sml4 line2 \\%sml4 line3", l4LineEnding, l4LineEnding);

        assertEquals(expected, actual);
        assertEquals(l4ExpectedValue, l4PropsMeta.currentValue());
    }

    @Test
    public void multiLineField() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "multiline.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var metadata = metaGen.getMetadata();
        final var everyThingMustBeMultiline = metadata.values().stream()
                .filter(___metadata -> ___metadata.lineType() == LineType.VALUE_PAIR)
                .map(PropsMeta::isMultiline).reduce(true, (___accl, ___item) -> ___accl && ___item);

        assertTrue(everyThingMustBeMultiline);
    }

    @Test
    public void multiLineFix() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "multiline-single.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var metadata = metaGen.getMetadata();
        final var optPropsMeta = metadata.values().stream().findFirst();

        if (optPropsMeta.isPresent()) {
            final var propsMeta = optPropsMeta.get();
            assertTrue(propsMeta.isBrokenMLine());

            final var lineEnding = propsMeta.osType().getEOL().eol();
            final var fixedPropsMeta = propsMeta.fixBrokenMLine();
            final var expected = String.format(" ml1 line1\\%sml1 line2\\%sml1 line3", lineEnding, lineEnding);
            assertEquals(expected, fixedPropsMeta.currentValue());
        }
        else {
            fail("Expected one entry.");
        }
    }

    @Test
    public void emptyField() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "empty-field.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var metadata = metaGen.getMetadata();
        final var optPropsMeta = metadata.values().stream().findFirst();

        if (optPropsMeta.isPresent()) {
            final var propsMeta = optPropsMeta.get();
            assertFalse(propsMeta.isMultiline());
            assertEquals(LineType.VALUE_PAIR, propsMeta.lineType());

            final var fixedPropsMeta = propsMeta.fixBrokenMLine();
            final var expected = "";
            assertEquals(expected, fixedPropsMeta.currentValue());
        }
        else {
            fail("Expected one entry.");
        }
    }

    @Test
    public void brokenMultiline() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "multiline.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var metadata = metaGen.getMetadata();
        final var brokenMultiline = metadata.entrySet().stream()
                .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                .filter(___entrySet -> ___entrySet.getValue().isBrokenMLine())
                .map(Map.Entry::getKey).toList();

        assertEquals("mline1 ,mline2 ,mline3 ", String.join(",", brokenMultiline));
    }

    @Test
    public void singleLineMismatch() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "single-line-mismatch.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertThrowsExactly(ValueMismatchException.class, metaGen::getMetadata);
    }

    @Test
    public void multiLineMismatch() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "multi-line-mismatch.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertThrowsExactly(ValueMismatchException.class, metaGen::getMetadata);
    }

    @Test
    public void mixMismatch() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "mix-mismatch.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertThrowsExactly(ValueMismatchException.class, metaGen::getMetadata);
    }

    @Test
    public void mixMismatch2() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "mix-mismatch2.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertThrowsExactly(ValueMismatchException.class, metaGen::getMetadata);
    }

    @Test
    public void fileNotFound() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "nonexistent.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertThrowsExactly(PropertiesNotFoundException.class, metaGen::getMetadata);
    }

    @Test
    public void startWithNonValuePair() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "valid-2.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertDoesNotThrow(metaGen::getMetadata);
    }

    @Test
    public void startWithInvalidProps() {
        final var propsFile = Paths.get(".", "src", "test", "resources", "invalid.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertThrows(InvalidContentException.class, metaGen::getMetadata);
    }

    @Test
    public void startWithValidLinux() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "valid-linux.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        assertDoesNotThrow(metaGen::getMetadata);
        assertTrue(metaGen.getMetadata().values().stream().map(PropsMeta::osType)
                .map(___osType -> ___osType == OSType.Linux)
                .reduce(true, (___aggr, ___item) -> ___aggr && ___item));
    }

    @Test
    public void startWithValidLinuxToWindows() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "valid-linux.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile, OSType.Windows, StandardCharsets.UTF_8);
        assertDoesNotThrow(metaGen::getMetadata);
        assertTrue(metaGen.getMetadata().values().stream().map(PropsMeta::osType)
                .map(___osType -> ___osType == OSType.Windows)
                .reduce(true, (___aggr, ___item) -> ___aggr && ___item));
    }

}
