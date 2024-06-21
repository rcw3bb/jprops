package xyz.ronella.tool.jprops.impl;

import xyz.ronella.tool.jprops.JPropsException;

import java.io.Serial;

/**
 * Thrown when there's a change in the properties.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class MergeChangeException extends JPropsException {
    @Serial
    private static final long serialVersionUID = -7712620659262340767L;
}
