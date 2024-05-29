package xyz.ronella.util.jprops.meta;

import xyz.ronella.util.jprops.JPropsException;

import java.io.Serial;

public class ValueMismatchException extends JPropsException {

    @Serial
    private static final long serialVersionUID = 4473850575583583014L;

    public ValueMismatchException(String message) {
        super(message);
    }
}
