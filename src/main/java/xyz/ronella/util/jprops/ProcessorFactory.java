package xyz.ronella.util.jprops;

import xyz.ronella.util.jprops.impl.DuplicateProcessor;
import xyz.ronella.util.jprops.impl.MergeProcessor;
import xyz.ronella.util.jprops.impl.SortProcessor;
import xyz.ronella.util.jprops.util.ArgsMgr;

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
    public static Processor createProcessor(final ArgsMgr argsMgr) {
        return switch (argsMgr.getCommand()) {
            case DUPLICATE -> new DuplicateProcessor(argsMgr);
            case SORT -> new SortProcessor(argsMgr);
            case MERGE -> new MergeProcessor(argsMgr);
            case MLINE -> {
                //TODO: To be implemented.
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected command: " + argsMgr.getCommand());
        };

    }

}
