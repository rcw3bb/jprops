package xyz.ronella.util.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.AbstractProcessor;
import xyz.ronella.util.jprops.JPropsException;
import xyz.ronella.util.jprops.meta.LineType;
import xyz.ronella.util.jprops.meta.MetaGenerator;
import xyz.ronella.util.jprops.util.ArgsMgr;

import java.util.List;
import java.util.Map;

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

    /**
     * The method that processes the command.
     */
    @Override
    public void process() {
        LOG.info("Processing %s", argsMgr.getProps().getAbsolutePath());
        sortView();
    }

    private List<String> getKeys(final MetaGenerator metaGen) throws JPropsException {
        return metaGen.getMetadata().entrySet().stream()
                .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                .map(Map.Entry::getKey)
                .toList();
    }

    private void sortView() {
        try(final var gLOG = LOG.groupLog("sortView")) {
            final var metaGen = new MetaGenerator(argsMgr.getProps());

            try {
                final var unsortedKeys = getKeys(metaGen);
                final var sortedKeys = unsortedKeys.stream().sorted().toList();
                final var isSorted = unsortedKeys.equals(sortedKeys);

                if (!isSorted) {
                    gLOG.info("--- Sorted Properties [BEGIN] ---");

                    for (final var key : sortedKeys) {
                        final var propsMeta = metaGen.getMetadata().get(key);
                        gLOG.info("\t%s=%s", key, propsMeta.currentValue());
                    }

                    gLOG.info("--- Sorted Properties [END] -----");
                }
                else {
                    gLOG.info("Properties file is already sorted.");
                }

            } catch (JPropsException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }
}
