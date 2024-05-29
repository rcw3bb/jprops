package xyz.ronella.util.jprops.meta;

import org.junit.jupiter.api.Test;
import xyz.ronella.util.jprops.JPropsException;
import xyz.ronella.util.jprops.meta.MetaGenerator;
import xyz.ronella.util.jprops.meta.PropsMeta;
import xyz.ronella.util.jprops.meta.ValueMismatchException;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

public class MetaGeneratorTest {

    @Test
    public void validateKeyOrder() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = new String[] {"field1 ", "field2 ", "field3 ", "field4 ", "field5 "};
        final var actual = metaGen.getMetadata().keySet().toArray(new String[] {});
        assertArrayEquals(expected, actual);
    }

    @Test
    public void validateCount() throws JPropsException {
        final var propsFile = Paths.get(".", "src", "test", "resources", "duplicate.properties").toFile();
        final var metaGen = new MetaGenerator(propsFile);
        final var expected = new Integer[] {2, 2, 2, 1, 1};
        final var actual = metaGen.getMetadata().values().stream()
                .map(PropsMeta::count).toList().toArray(new Integer[] {});
        assertArrayEquals(expected, actual);
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
        assertThrowsExactly(JPropsException.class, metaGen::getMetadata);
    }

}
