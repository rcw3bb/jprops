package xyz.ronella.util.jprops;

import xyz.ronella.util.jprops.util.ArgsMgr;

public abstract class AbstractProcessor implements Processor {
    protected transient final ArgsMgr argsMgr;
    public AbstractProcessor(final ArgsMgr argsMgr) {
        this.argsMgr = argsMgr;
    }

}
