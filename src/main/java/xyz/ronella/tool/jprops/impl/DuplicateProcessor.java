package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.meta.PropsMeta;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.FileMgr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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
            duplicateView();
        }
    }

    private void dedupe() {
        try(final var gLOG = LOG.groupLog("dedupe")) {
            try {
                duplicateProcess(gLOG, ___metaData -> {
                    try {
                        final File tmpFile = FileMgr.createTmpFile(argsMgr.getProps());
                        gLOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());

                        try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                            ___metaData.forEach((___key, ___value) -> outputWriter(writer, ___key, ___value));
                        } catch (IOException e) {
                            gLOG.error(LOG.getStackTraceAsString(e));
                        }

                        FileMgr.safeMove(argsMgr.getCommand(), tmpFile, argsMgr.getProps()).ifPresent(___backupFile ->
                                gLOG.info("Back file created: %s", ___backupFile));
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                });
            }
            catch (RuntimeException exception) {
                gLOG.error(LOG.getStackTraceAsString(exception));
            }
        }
    }

    private void duplicateProcess(final LoggerPlus.GroupLogger gLOG, final Consumer<Map<String, PropsMeta>> duplicateLogic) {
        final var metaGen = new MetaGenerator(argsMgr.getProps(), argsMgr.getTargetOS());

        try {
            final var metaData = metaGen.getMetadata();
            final var shouldProcess = metaData.values().stream()
                    .filter(___value -> ___value.lineType() == LineType.VALUE_PAIR)
                    .anyMatch(___value -> ___value.count() > 1);

            if (shouldProcess) {
                duplicateLogic.accept(metaData);
            }
            else {
                gLOG.info("No duplicate found.");
            }

        } catch (JPropsException exception) {
            gLOG.error(LOG.getStackTraceAsString(exception));
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

    private void duplicateView() {
        try(final var gLOG = LOG.groupLog("duplicateView")) {
            duplicateProcess(gLOG, ___metaData -> {
                final List<Map.Entry<String, PropsMeta>> metadata = ___metaData.entrySet().stream()
                        .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                        .filter(___entrySet -> ___entrySet.getValue().count() > 1)
                        .toList();

                for (final var meta : metadata) {
                    gLOG.error("[%s] field duplicated %d times.", meta.getKey(), meta.getValue().count());
                }
            });
        }
    }
}
