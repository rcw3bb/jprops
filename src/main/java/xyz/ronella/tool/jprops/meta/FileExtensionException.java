package xyz.ronella.tool.jprops.meta;

import xyz.ronella.tool.jprops.JPropsException;

import java.io.Serial;

/**
 * The FileExtensionException class is the exception thrown when the file extension is invalid.
 * The file extension must be .properties.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class FileExtensionException extends JPropsException {
    @Serial
    private static final long serialVersionUID = -1309820883505910578L;

    /**
     * The constructor with a message.
     * @param message The message.
     */
    public FileExtensionException(final String message) {
        super(message);
    }
}
