package xyz.ronella.util.jprops;

import java.io.Serial;

public class JPropsException extends Exception {

    @Serial
    private static final long serialVersionUID = 7563631854981094263L;

    public JPropsException(final String message) {
        super(message);
    }

    public JPropsException(final Throwable throwable) {
        super(throwable);
    }
}
