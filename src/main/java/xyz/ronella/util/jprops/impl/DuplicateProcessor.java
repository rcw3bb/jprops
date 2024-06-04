package xyz.ronella.util.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.AbstractProcessor;
import xyz.ronella.util.jprops.JPropsException;
import xyz.ronella.util.jprops.meta.MetaGenerator;
import xyz.ronella.util.jprops.meta.PropsMeta;
import xyz.ronella.util.jprops.util.ArgsMgr;

import java.util.List;
import java.util.Map;

public class DuplicateProcessor extends AbstractProcessor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(DuplicateProcessor.class));

    public DuplicateProcessor(final ArgsMgr argsMgr) {
        super(argsMgr);
    }

    @Override
    public void process() {
        try(final var gLOG = LOG.groupLog("process")) {
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
