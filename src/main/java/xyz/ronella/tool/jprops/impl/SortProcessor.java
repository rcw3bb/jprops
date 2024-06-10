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
            final var props = argsMgr.getProps();
            final var metaGen = new MetaGenerator(props);
            try {
                final var unsortedKeys = metaGen.getKeysByLineType(LineType.VALUE_PAIR);
                final var sortedKeys = unsortedKeys.stream().sorted().toList();
                final var isSorted = unsortedKeys.equals(sortedKeys);

                if (!isSorted) {
                    final var tmpFile = FileMgr.createTmpFile(props);
                    gLOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());

                    try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                        for (final var key : sortedKeys) {
                            final var propsMeta = metaGen.getMetadata().get(key);
                            outputWriter(writer, key, propsMeta);
                        }
                    }
                    FileMgr.safeMove(argsMgr.getCommand(), tmpFile, props).ifPresent(___backupFile ->
                            gLOG.info("Back file created: %s", ___backupFile));
                }
                else {
                    gLOG.info("Properties file is already sorted.");
                }
            } catch (JPropsException | IOException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }

    private void sortView() {
        try(final var gLOG = LOG.groupLog("sortView")) {
            final var metaGen = new MetaGenerator(argsMgr.getProps());

            try {
                final var unsortedKeys = getKeys(metaGen);
                final var sortedKeys = unsortedKeys.stream().sorted().toList();
                final var isSorted = unsortedKeys.equals(sortedKeys);

                if (!isSorted) {
                    gLOG.info("--- Sorted Fields [BEGIN] ---");

                    for (final var key : sortedKeys) {
                        gLOG.info("\t%s", key);
                    }

                    gLOG.info("--- Sorted Fields [END] -----");
                }
                else {
                    gLOG.info("Nothing to do. Properties file is already sorted.");
                }

            } catch (JPropsException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }
}
