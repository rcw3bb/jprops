package xyz.ronella.tool.jprops;

import xyz.ronella.tool.jprops.impl.*;
import xyz.ronella.tool.jprops.util.ArgsMgr;

/**
 * The ProcessorFactory class is a factory class for creating a Processor instance.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class ProcessorFactory {

    private ProcessorFactory() {}

    /**
     * The createProcessor method creates a Processor instance based on the command.
     * @param argsMgr The ArgsMgr instance.
     * @return The Processor instance.
     */
    public static Processor createProcessor(final ArgsMgr argsMgr) throws JPropsException {
        return switch (argsMgr.getCommand()) {
            case DUPLICATE -> new DuplicateProcessor(argsMgr);
            case SORT -> new SortProcessor(argsMgr);
            case MERGE -> new MergeProcessor(argsMgr);
            case BMLINE -> new BrokenMLineProcessor(argsMgr);
            default -> new InvalidProcessor();
        };

    }

}
