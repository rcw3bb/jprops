package xyz.ronella.tool.jprops.impl;

import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.Processor;
import xyz.ronella.tool.jprops.util.InvalidCommandException;

/**
 * The InvalidProcessor class is a Processor implementation that does nothing.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
public class InvalidProcessor implements Processor {

    @Override
    public void process() throws JPropsException {
        throw new InvalidCommandException("Invalid command. Try running \"jprops help\" to know the available commands.");
    }
}
