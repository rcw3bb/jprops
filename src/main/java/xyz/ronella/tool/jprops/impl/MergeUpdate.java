package xyz.ronella.tool.jprops.impl;

import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.PropsMeta;

/**
 * The MergeUpdate functional interface is used to process the properties for updating.
 *
 * @author Ron Webb
 * @since 1.2.0
 */
@FunctionalInterface
public interface MergeUpdate {

    /**
     * The method that processes the record.
     *
     * @param key The key.
     * @param srcPropsMeta The source metadata value.
     * @param dstPropsMeta The destination metadata value.
     */
    void process(String key, PropsMeta srcPropsMeta, PropsMeta dstPropsMeta) throws JPropsException;
}
