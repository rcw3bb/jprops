package xyz.ronella.util.jprops;

import java.io.Serial;

public class ValueMismatchException extends JPropsException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ValueMismatchException(String message) {
        super(message);
    }
}
