package xyz.ronella.tool.jprops;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The TextWriter class is the class that writes text to a file.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class TextWriter {
    private TextWriter() {}

    public static void write(final File properties, final String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(properties, true))) {
            writer.write(text);
        }
    }
}
