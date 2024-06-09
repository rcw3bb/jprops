package xyz.ronella.util.jprops.util;

import xyz.ronella.util.jprops.JPropsException;
import java.io.Serial;

/**
 * The MissingCommandException class is the exception class for the JProps library.
 * This class extends the
 * {@link JPropsException} class.
 * This exception is thrown when the command is missing.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class MissingCommandException extends JPropsException {

    @Serial
    private static final long serialVersionUID = 5935256959856333485L;

    /**
     * The default constructor.
     */
    public MissingCommandException() {
        super();
    }
}
