package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;

import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.io.PrintWriter;
import java.util.List;

/**
 * The SortProcessor class is the implementation of the Processor interface for the sort command.
 * This class extends the AbstractProcessor class.
 * This class sorts the properties file.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class SortProcessor extends AbstractProcessor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(SortProcessor.class));

    /**
     * The constructor.
     *
     * @param argsMgr The ArgsMgr instance.
     */
    public SortProcessor(ArgsMgr argsMgr) {
        super(argsMgr);
    }

    @Override
    public boolean shouldProcess(final MetaGenerator metaGen) throws JPropsException {
        final var unsortedKeys = metaGen.getKeysByLineType(LineType.VALUE_PAIR);
        final var sortedKeys = getSortedKeys(metaGen);
        return !unsortedKeys.equals(sortedKeys);
    }

    @Override
    public boolean mustPersist(final MetaGenerator metaGen) {
        return argsMgr.isApply();
    }

    private List<String> getSortedKeys(final MetaGenerator metaGen) throws JPropsException {
        final var unsortedKeys = metaGen.getKeysByLineType(LineType.VALUE_PAIR);
        return unsortedKeys.stream().sorted().toList();
    }

    @Override
    public void viewLogic(final MetaGenerator metaGen) throws JPropsException {
        try(final var gLOG = LOG.groupLog("sortView")) {
            gLOG.info("--- Sorted Fields View [BEGIN] ---");
            final var sortedKeys = getSortedKeys(metaGen);
            sortedKeys.forEach(___key -> gLOG.info("\t%s", ___key));
            gLOG.info("--- Sorted Fields View [END] -----");
        }
    }

    @Override
    public void persistLogic(final PrintWriter writer, final MetaGenerator metaGen) throws JPropsException {
        try(final var gLOG = LOG.groupLog("applySort")) {
            gLOG.info("--- Applied Sorted Fields [BEGIN] ---");
            final var sortedKeys = getSortedKeys(metaGen);

            for (final var key : sortedKeys) {
                final var propsMeta = metaGen.getMetadata().get(key);

                outputWriter(writer, key, propsMeta);
                gLOG.info("\t%s", key);
            }

            gLOG.info("--- Applied Sorted Fields [END] -----");
        }
    }
}
