package xyz.ronella.tool.jprops.meta;

import xyz.ronella.tool.jprops.JPropsException;

import java.io.Serial;

/**
 * The PropertiesNotFoundException class is the exception class for the JProps library.
 * This class extends the
 * {@link JPropsException} class.
 * This exception is thrown when the properties file is not found.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class PropertiesNotFoundException extends JPropsException {
    @Serial
    private static final long serialVersionUID = 2227221020494073325L;

    /**
     * The default constructor.
     * @param message The throwable.
     */
    public PropertiesNotFoundException(String message) {
        super(message);
    }
}
