package xyz.ronella.tool.jprops;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MainTest {
    @Test
    public void duplicateNonExistentFile() {
        assertDoesNotThrow(() -> Main.main(new String[]{"duplicate", "-p", "non-existent-file.properties"}));
    }

    @Test
    public void sortNonExistentFile() {
        assertDoesNotThrow(() -> Main.main(new String[]{"sort", "-p", "non-existent-file.properties"}));
    }

    @Test
    public void bmlineNonExistentFile() {
        assertDoesNotThrow(() -> Main.main(new String[]{"bmline", "-p", "non-existent-file.properties"}));
    }

    @Test
    public void mergeNonExistentFile() {
        assertDoesNotThrow(() -> Main.main(new String[]{"merge", "-sp", "non-existent-file.properties", "-dp", "non-existent-file.properties"}));
    }

    @Test
    public void invalidCommand() {
        assertDoesNotThrow(() -> Main.main(new String[]{"invalid", "-p", "non-existent-file.properties"}));
    }

    @Test
    public void helpCommand() {
        assertDoesNotThrow(() -> Main.main(new String[]{"help"}));
    }

    @Test
    public void invalidFileExtension() {
        final var propFile = Paths.get(".", "src", "test", "resources", "test-file.txt").toFile();
        assertDoesNotThrow(() -> Main.main(new String[]{"duplicate", "-p", propFile.getAbsolutePath()}));
    }
}
