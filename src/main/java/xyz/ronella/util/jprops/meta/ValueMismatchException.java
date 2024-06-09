package xyz.ronella.util.jprops.meta;

import xyz.ronella.util.jprops.JPropsException;

import java.io.Serial;

/**
 * The ValueMismatchException class is the exception class for the JProps library.
 * This class extends the
 * {@link JPropsException} class.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class ValueMismatchException extends JPropsException {

    @Serial
    private static final long serialVersionUID = 4473850575583583014L;

    /**
     * The constructor with a message.
     * @param message The message.
     */
    public ValueMismatchException(String message) {
        super(message);
    }
}
