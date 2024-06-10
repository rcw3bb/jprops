package xyz.ronella.util.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.AbstractProcessor;
import xyz.ronella.util.jprops.JPropsException;
import xyz.ronella.util.jprops.meta.LineType;
import xyz.ronella.util.jprops.meta.MetaGenerator;
import xyz.ronella.util.jprops.meta.PropsMeta;
import xyz.ronella.util.jprops.support.ArgsMgr;
import xyz.ronella.util.jprops.support.FileMgr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The DuplicateProcessor class is the implementation of the Processor interface for the duplicate command.
 * This class extends the AbstractProcessor class.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class DuplicateProcessor extends AbstractProcessor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(DuplicateProcessor.class));

    /**
     * The constructor.
     * @param argsMgr The ArgsMgr instance.
     */
    public DuplicateProcessor(final ArgsMgr argsMgr) {
        super(argsMgr);
    }

    /**
     * The method that processes the command.
     */
    @Override
    public void processLogic() {
        if (argsMgr.isDedupe()) {
            dedupe();
        }
        else {
            lightWeightList();
        }
    }

    private void dedupe() {
        try(final var gLOG = LOG.groupLog("dedupe")) {
            final var props = argsMgr.getProps();
            final var metaGen = new MetaGenerator(props);
            try {
                final var shouldProcess = metaGen.getMetadata().values().stream()
                        .anyMatch(___value -> ___value.count() > 1);

                if (shouldProcess) {
                    final var tmpFile = FileMgr.createTmpFile(props);
                    gLOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());

                    try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                        metaGen.getMetadata().forEach((___key, ___value) -> outputWriter(writer, ___key, ___value));
                    }

                    FileMgr.safeMove(argsMgr.getCommand(), tmpFile, props).ifPresent(___backupFile ->
                            gLOG.info("Back file created: %s", ___backupFile));
                }
                else {
                    gLOG.info("No duplicate found.");
                }

            } catch (JPropsException | IOException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }

    /**
     * The method that writes the properties to the output.
     * @param writer The PrintWriter instance.
     * @param key The key.
     * @param value The PropsMeta instance.
     */
    protected void outputWriter(final PrintWriter writer, final String key, final PropsMeta value) {
        Optional.of(value.count())
                .filter(___count -> /* Has Duplicate */ ___count > 1)
                .ifPresent(___isDuplicated -> LOG.info("[%s] normalized to one instance only", key));

        super.outputWriter(writer, key, value);
    }

    private void lightWeightList() {
        try(final var gLOG = LOG.groupLog("lightWeightList")) {
            final var metaGen = new MetaGenerator(argsMgr.getProps());

            try {
                final List<Map.Entry<String, PropsMeta>> metadata = metaGen.getMetadata().entrySet().stream()
                        .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                        .filter(___entrySet -> ___entrySet.getValue().count() > 1)
                        .toList();

                var hasError = false;
                for (final var meta : metadata) {
                    hasError = true;
                    gLOG.error("[%s] field duplicated %d times.", meta.getKey(), meta.getValue().count());
                }

                if (!hasError) {
                    gLOG.info("No duplicate found.");
                }
            } catch (JPropsException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }
}
