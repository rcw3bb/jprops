package xyz.ronella.tool.jprops.impl;

import xyz.ronella.tool.jprops.meta.MetaGenerator;

/**
 * The SortProcessRecord class is a record class for the sort process.
 * This class is used to store the key and the meta generator.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public record SortProcessRecord(String key, MetaGenerator metaGen) {
}
