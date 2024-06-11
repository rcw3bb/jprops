package xyz.ronella.tool.jprops.impl;

import xyz.ronella.tool.jprops.meta.MetaGenerator;

import java.io.PrintWriter;

/**
 * The MergeProcessRecord class is a record class for the merge process.
 * This class is used to store the key, source meta generator, destination meta generator, and the writer.
 *
 * @since 1.0.0
 */
public record MergeProcessRecord(String key, MetaGenerator srcMetaGen, MetaGenerator dstMetaGen, PrintWriter writer) {
}
