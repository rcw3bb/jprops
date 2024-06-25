package xyz.ronella.tool.jprops.meta;

import xyz.ronella.tool.jprops.JPropsException;

import java.io.Serial;

/**
 * This exception is thrown when the content of the properties file is invalid.
 *
 * @since 1.3.0
 * @author Ron Webb
 */
public class InvalidContentException extends JPropsException {
    @Serial
    private static final long serialVersionUID = 2463364737688944584L;

    /**
     * Constructor.
     * @param message The exception message.
     */
    public InvalidContentException(final String message) {
        super(message);
    }
}
