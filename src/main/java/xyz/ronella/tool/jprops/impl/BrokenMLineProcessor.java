package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.meta.PropsMeta;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.FileMgr;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The BrokenMLineProcessor class is the implementation of the Processor interface for the broken_mline command.
 * This class extends the AbstractProcessor class.
 * This class fixes the broken multi-line properties.
 *
 * @author Ron Webb
 * @since 1.1.0
 */
public class BrokenMLineProcessor  extends AbstractProcessor {

    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(BrokenMLineProcessor.class));

    /**
     * The constructor.
     *
     * @param argsMgr The ArgsMgr instance.
     */
    public BrokenMLineProcessor(ArgsMgr argsMgr) {
        super(argsMgr);
    }

    @Override
    protected void processLogic() {
        if (argsMgr.isFix()) {
            bmLineFix();
        }
        else {
            bmLineView();
        }
    }

    private void bmLineProcess(final LoggerPlus.GroupLogger gLOG, final Consumer<Map<String, PropsMeta>> mbLineLogic) {
        final var metaGen = new MetaGenerator(argsMgr.getProps(), argsMgr.getTargetOS());

        try {
            final var metaData = metaGen.getMetadata();
            final var shouldProcess = !metaData.values().stream()
                    .filter(___value -> ___value.lineType() == LineType.VALUE_PAIR)
                    .filter(PropsMeta::isBrokenMLine)
                    .toList().isEmpty();

            if (shouldProcess) {
                mbLineLogic.accept(metaData);
            }
            else {
                gLOG.info("No broken multiline found.");
            }

        } catch (JPropsException exception) {
            gLOG.error(LOG.getStackTraceAsString(exception));
        }
    }

    private void bmLineView() {
        try(final var gLOG = LOG.groupLog("bmLineView")) {
            bmLineProcess(gLOG, ___metaData -> {
                final List<Map.Entry<String, PropsMeta>> metadata = ___metaData.entrySet().stream()
                        .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                        .filter(___entrySet -> ___entrySet.getValue().isBrokenMLine())
                        .toList();

                gLOG.info("--- Broken Multiline Fields [BEGIN] ---");
                for (final var meta : metadata) {
                    gLOG.info("""
                            
                            -----------------------
                            [%s]
                            -----------------------
                            %s
                            -----------------------
                            """, meta.getKey(),
                            meta.getValue().currentValue());
                }
                gLOG.info("--- Broken Multiline Fields [END] ------");
            });
        }
    }

    private void bmLineFix() {
        try (final var gLOG = LOG.groupLog("bmLineFix")) {
            try {
                bmLineProcess(gLOG, ___metaData -> {
                    try {
                        final File tmpFile = FileMgr.createTmpFile(argsMgr.getProps());
                        gLOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());

                        try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                            ___metaData.forEach((___key, ___value) -> {
                                writer(___key, ___value, writer);
                            });

                        } catch (IOException e) {
                            gLOG.error(LOG.getStackTraceAsString(e));
                        }

                        FileMgr.safeMove(argsMgr.getCommand(), tmpFile, argsMgr.getProps()).ifPresent(___backupFile ->
                                gLOG.info("Backup file created: %s", ___backupFile));
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

    private void writer(final String key, final PropsMeta value, final PrintWriter writer) {
        PropsMeta proxyValue = value;
        if (value.isMultiline() && value.isBrokenMLine()) {
            proxyValue = value.fixBrokenMLine();
        }
        outputWriter(writer, key, proxyValue);
    }
}
