package xyz.ronella.util.jprops;

import xyz.ronella.util.jprops.util.ArgsMgr;

/**
 * The AbstractProcessor class provides a base implementation for the Processor interface.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public abstract class AbstractProcessor implements Processor {

    /**
     * The ArgsMgr instance.
     */
    protected transient final ArgsMgr argsMgr;

    /**
     * The constructor.
     * @param argsMgr The ArgsMgr instance.
     */
    public AbstractProcessor(final ArgsMgr argsMgr) {
        this.argsMgr = argsMgr;
    }

}
