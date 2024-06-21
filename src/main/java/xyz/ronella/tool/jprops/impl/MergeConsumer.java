package xyz.ronella.tool.jprops.impl;

import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.PropsMeta;

/**
 * The MergeConsumer functional interface is used to process the records.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
@FunctionalInterface
public interface MergeConsumer {

    /**
     * The method that processes the record.
     *
     * @param key The key.
     * @param propsMeta The metadata value.
     */
    void accept(String key, PropsMeta propsMeta) throws JPropsException;
}
