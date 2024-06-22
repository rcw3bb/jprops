package xyz.ronella.tool.jprops;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MainTest {
    @Test
    public void nonExistentFile() {
        assertDoesNotThrow(() -> Main.main(new String[]{"duplicate", "-p", "non-existent-file.properties"}));
    }

    @Test
    public void invalidFileExtension() {
        final var propFile = Paths.get(".", "src", "test", "resources", "test-file.txt").toFile();
        assertDoesNotThrow(() -> Main.main(new String[]{"duplicate", "-p", propFile.getAbsolutePath()}));
    }
}
