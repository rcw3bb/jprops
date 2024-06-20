package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;

import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.meta.PropsMeta;
import xyz.ronella.tool.jprops.util.ArgsMgr;

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

    @Override
    public boolean mustPersist(final MetaGenerator metaGen) throws JPropsException {
        return argsMgr.isDedupe();
    }

    @Override
    public void persistLogic(final PrintWriter writer, final MetaGenerator metaGen) throws JPropsException {
        metaGen.getMetadata().forEach((___key, ___value) -> outputWriter(writer, ___key, ___value));
    }

    @Override
    public void viewLogic(final MetaGenerator metaGen) throws JPropsException {
        try(final var gLOG = LOG.groupLog("duplicateView")) {
            final var metaData = metaGen.getMetadata();
            final List<Map.Entry<String, PropsMeta>> metadata = metaData.entrySet().stream()
                    .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                    .filter(___entrySet -> ___entrySet.getValue().count() > 1)
                    .toList();

            for (final var meta : metadata) {
                gLOG.error("[%s] field duplicated %d times.", meta.getKey(), meta.getValue().count());
            }
        }
    }

    @Override
    public boolean shouldProcess(final MetaGenerator metaGen) throws JPropsException {
        final var metaData = metaGen.getMetadata();
        return metaData.values().stream()
                .filter(___value -> ___value.lineType() == LineType.VALUE_PAIR)
                .anyMatch(___value -> ___value.count() > 1);
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

}
