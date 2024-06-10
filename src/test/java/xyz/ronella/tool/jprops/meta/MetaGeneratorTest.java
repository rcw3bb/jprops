package xyz.ronella.tool.jprops.meta;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.JPropsException;

import static org.junit.jupiter.api.Assertions.*;

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

}
