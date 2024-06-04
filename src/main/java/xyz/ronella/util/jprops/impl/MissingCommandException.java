package xyz.ronella.util.jprops.impl;

import xyz.ronella.util.jprops.JPropsException;

import java.io.Serial;

public class MissingCommandException extends JPropsException {

    @Serial
    private static final long serialVersionUID = 5935256959856333485L;

    public MissingCommandException() {
        super();
    }
}
