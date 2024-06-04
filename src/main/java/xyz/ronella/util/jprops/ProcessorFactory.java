package xyz.ronella.util.jprops;

import xyz.ronella.util.jprops.impl.DuplicateProcessor;
import xyz.ronella.util.jprops.util.ArgsMgr;

final public class ProcessorFactory {

    private ProcessorFactory() {}

    public static Processor createProcessor(final ArgsMgr argsMgr) {
        return switch (argsMgr.getCommand()) {
            case DUPLICATE ->  new DuplicateProcessor(argsMgr);
            default -> throw new IllegalStateException("Unexpected command: " + argsMgr.getCommand());
        };

    }

}
