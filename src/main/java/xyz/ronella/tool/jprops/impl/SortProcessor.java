package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.FileMgr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    public void processLogic() {
        if (argsMgr.isApply()) {
            applySort();
        }
        else {
            sortView();
        }
    }

    private List<String> getKeys(final MetaGenerator metaGen) throws JPropsException {
        return metaGen.getMetadata().entrySet().stream()
                .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                .map(Map.Entry::getKey)
                .toList();
    }

    private void applySort() {
        try(final var gLOG = LOG.groupLog("applySort")) {
            try {
                final var isSorted = sortView();
                if (!isSorted) {
                    gLOG.info("Applying sortView findings.");
                    final var props = argsMgr.getProps();
                    final var tmpFile = FileMgr.createTmpFile(props);
                    gLOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());

                    try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                        sortProcess(gLOG, ___processRecord -> {
                            try {
                                applySortLogic(___processRecord.key(), ___processRecord.metaGen(), writer);
                            } catch (JPropsException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    FileMgr.safeMove(argsMgr.getCommand(), tmpFile, props).ifPresent(___backupFile ->
                            gLOG.info("Back file created: %s", ___backupFile));
                }
            }
            catch (IOException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }

    private void applySortLogic(final String key, final MetaGenerator metaGen, final PrintWriter writer)
            throws JPropsException {
        try(final var gLOG = LOG.groupLog("applySort")) {
            final var propsMeta = metaGen.getMetadata().get(key);
            outputWriter(writer, key, propsMeta);
            gLOG.info("\t%s", key);
        }
    }

    private boolean sortProcess(final LoggerPlus.GroupLogger gLOG, final Consumer<SortProcessRecord> sortLogic) {
        final var metaGen = new MetaGenerator(argsMgr.getProps());

        try {
            final var unsortedKeys = getKeys(metaGen);
            final var sortedKeys = unsortedKeys.stream().sorted().toList();
            final var isSorted = unsortedKeys.equals(sortedKeys);

            if (!isSorted) {
                gLOG.info("--- Sorted Fields [BEGIN] ---");

                for (final var key : sortedKeys) {
                    sortLogic.accept(new SortProcessRecord(key, metaGen));
                }

                gLOG.info("--- Sorted Fields [END] -----");
            } else {
                gLOG.info("Nothing to do. Properties file is already sorted.");
            }

            return isSorted;
        } catch (JPropsException e) {
            gLOG.error(LOG.getStackTraceAsString(e));
        }

        return false;
    }

    private boolean sortView() {
        try(final var gLOG = LOG.groupLog("sortView")) {
            return sortProcess(LOG.groupLog("sortView"), ___processRecord ->
                    gLOG.info("\t%s", ___processRecord.key()));
        }
    }
}
