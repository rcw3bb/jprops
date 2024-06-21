package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;

import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.meta.PropsMeta;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * The BrokenMLineProcessor class is the implementation of the Processor interface for the broken_mline command.
 * This class extends the AbstractProcessor class.
 * This class fixes the broken multi-line properties.
 *
 * @author Ron Webb
 * @since 1.1.0
 */
public class BrokenMLineProcessor extends AbstractProcessor {

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
    public boolean shouldProcess(MetaGenerator metaGen) throws JPropsException {
        final var metaData = metaGen.getMetadata();
        return !metaData.values().stream()
                .filter(___value -> ___value.lineType() == LineType.VALUE_PAIR)
                .filter(PropsMeta::isBrokenMLine)
                .toList().isEmpty();
    }

    @Override
    public boolean mustPersist(MetaGenerator metaGen) throws JPropsException {
        return argsMgr.isFix();
    }

    @Override
    public void viewLogic(MetaGenerator metaGen) throws JPropsException {
        try (final var gLOG = LOG.groupLog("bmLineView")) {
            final var metaData = metaGen.getMetadata();
            final List<Map.Entry<String, PropsMeta>> metadata = metaData.entrySet().stream()
                    .filter(___entrySet -> ___entrySet.getValue().lineType() == LineType.VALUE_PAIR)
                    .filter(___entrySet -> ___entrySet.getValue().isBrokenMLine())
                    .toList();

            gLOG.info("--- Broken Multiline Fields [BEGIN] ---");
            for (final var meta : metadata) {
                gLOG.info("""
                                                    
                                --[KEY]----------------
                                [%s]
                                --[VALUE]--------------
                                %s
                                -----------------------
                                """, meta.getKey(),
                        meta.getValue().currentValue());
            }
            gLOG.info("--- Broken Multiline Fields [END] ------");
        }
    }

    @Override
    public void persistLogic(PrintWriter writer, MetaGenerator metaGen) throws JPropsException {
        try (final var gLOG = LOG.groupLog("bmLineFix")) {
            final var metaData = metaGen.getMetadata();
            gLOG.info("--- Fixing Broken Multiline Fields [BEGIN] ---");
            metaData.forEach((___key, ___value) -> writer(gLOG, ___key, ___value, writer));
            gLOG.info("--- Fixing Broken Multiline Fields [END] -----");
        }
    }

    private void writer(LoggerPlus.GroupLogger gLOG, final String key, final PropsMeta value, final PrintWriter writer) {
        PropsMeta proxyValue = value;
        if (value.isMultiline() && value.isBrokenMLine()) {
            proxyValue = value.fixBrokenMLine();

            gLOG.info("""
                    
                    --[KEY]----------------
                    [%s]
                    --[FROM]---------------
                    %s
                    --[TO]-----------------
                    %s
                    """, key, value.currentValue(), proxyValue.currentValue());
        }
        outputWriter(writer, key, proxyValue);
    }
}
