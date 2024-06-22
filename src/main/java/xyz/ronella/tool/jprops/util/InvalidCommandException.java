package xyz.ronella.tool.jprops.util;

import xyz.ronella.tool.jprops.JPropsException;

import java.io.Serial;

/**
 * The InvalidCommandException class is the exception class for the JProps library.
 * This class extends the
 * {@link JPropsException} class.
 * This exception is thrown when the command is invalid.
 *
 * @since 1.2.0
 */
public class InvalidCommandException extends JPropsException {
    @Serial
    private static final long serialVersionUID = 6182898472662355776L;

    /**
     * The constructor.
     * @param message The exception message.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
