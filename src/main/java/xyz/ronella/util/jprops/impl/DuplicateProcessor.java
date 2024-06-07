package xyz.ronella.util.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.AbstractProcessor;
import xyz.ronella.util.jprops.JPropsException;
import xyz.ronella.util.jprops.meta.LineType;
import xyz.ronella.util.jprops.meta.MetaGenerator;
import xyz.ronella.util.jprops.meta.PropsMeta;
import xyz.ronella.util.jprops.util.ArgsMgr;
import xyz.ronella.util.jprops.util.FileMgr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class DuplicateProcessor extends AbstractProcessor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(DuplicateProcessor.class));

    public DuplicateProcessor(final ArgsMgr argsMgr) {
        super(argsMgr);
    }

    @Override
    public void process() {
        final var isDedupe = argsMgr.isDedupe();
        if (isDedupe) {
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
                    gLOG.info("Processing %s", props.getAbsolutePath());

                    final var tmpFile = FileMgr.createTmpFile(props);
                    gLOG.info("Temp file created: %s", tmpFile.getAbsolutePath());

                    try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                        metaGen.getMetadata().forEach((___key, ___value) -> outputWriter(writer, ___key, ___value));
                    }

                    FileMgr.safeMove(tmpFile, props).ifPresent(___backupFile ->
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

    private void outputWriter(final PrintWriter writer, final String key, final PropsMeta value) {
        if (value.lineType() == LineType.VALUE_PAIR) {

            final var isDuplicated = value.count() > 1;
            if (isDuplicated) {
                LOG.info("[%s] normalized to one instance only", key);
            }

            writer.printf("%s=%s%s", key, value.currentValue(), value.osType().getEOL().eol());
        }
        else {
            writer.printf("%s%s", value.currentValue(), value.osType().getEOL().eol());
        }
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
