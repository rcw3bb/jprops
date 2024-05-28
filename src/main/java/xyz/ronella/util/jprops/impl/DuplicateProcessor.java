package xyz.ronella.util.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.MetaGenerator;
import xyz.ronella.util.jprops.Processor;

import java.io.File;

public class DuplicateProcessor implements Processor {

    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(DuplicateProcessor.class));

    @Override
    public void process(final File props) {
        try(final var gLOG = LOG.groupLog("process")) {
            final var metaGen = new MetaGenerator(props);
            final var metadata = metaGen.getMetadata().entrySet().stream()
                    .filter(___entrySet -> ___entrySet.getValue().count() > 1)
                    .toList();
            for (final var meta : metadata) {
                gLOG.warn("[%s] field duplicated %d times.", meta.getKey(), meta.getValue().count());
            }
        }
    }
}
