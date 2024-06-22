package xyz.ronella.tool.jprops;

import java.io.Serial;

/**
 * The JPropsException class is the exception class for the JProps library.
 * This class extends the
 * {@link Exception} class.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class JPropsException extends Exception {

    @Serial
    private static final long serialVersionUID = 7563631854981094263L;

    /**
     * The default constructor.
     */
    public JPropsException() {
        super();
    }

    /**
     * The constructor with a message.
     * @param message The message.
     */
    public JPropsException(final String message) {
        super(message);
    }
}
