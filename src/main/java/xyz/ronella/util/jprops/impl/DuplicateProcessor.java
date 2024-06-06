package xyz.ronella.util.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.AbstractProcessor;
import xyz.ronella.util.jprops.JPropsException;
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
                gLOG.info("Processing %s", props.getAbsolutePath());

                final var tmpFile = FileMgr.createTmpFile(props);
                gLOG.info("Temp file created: %s", tmpFile.getAbsolutePath());

                try(final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                    metaGen.getMetadata().forEach((key, value) -> {
                        final var isDuplicated = value.count() > 1;

                        if (isDuplicated) {
                            gLOG.info("[%s] just added once", key);
                        }

                        writer.printf("%s=%s%s", key, value.currentValue(), value.osType().getEOL().eol());
                    });
                }

                FileMgr.safeMove(tmpFile, props).ifPresent(___backupFile ->
                        gLOG.info("Back file created: %s", ___backupFile));

            } catch (JPropsException | IOException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }

    private void lightWeightList() {
        try(final var gLOG = LOG.groupLog("lightWeightList")) {
            final var metaGen = new MetaGenerator(argsMgr.getProps());

            try {
                final List<Map.Entry<String, PropsMeta>> metadata = metaGen.getMetadata().entrySet().stream()
                        .filter(___entrySet -> ___entrySet.getValue().count() > 1)
                        .toList();

                var hasError = false;
                for (final var meta : metadata) {
                    hasError = true;
                    gLOG.error("[%s] field duplicated %d times.", meta.getKey(), meta.getValue().count());
                }

                if (!hasError) {
                    gLOG.info("No duplicates found.");
                }
            } catch (JPropsException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }
}
