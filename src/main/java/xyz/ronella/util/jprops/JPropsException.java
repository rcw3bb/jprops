package xyz.ronella.util.jprops;

import java.io.Serial;

public class JPropsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public JPropsException(final String message) {
        super(message);
    }

    public JPropsException(final Throwable throwable) {
        super(throwable);
    }
}
