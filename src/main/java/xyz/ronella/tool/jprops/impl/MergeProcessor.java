package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;

import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.meta.PropsMeta;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

/**
 * The MergeProcessor class is the implementation of the Processor interface for the merge command.
 * This class extends the AbstractProcessor class.
 * This class merges the properties file.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class MergeProcessor extends AbstractProcessor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(MergeProcessor.class));

    /**
     * The constructor.
     *
     * @param argsMgr The ArgsMgr instance.
     */
    public MergeProcessor(ArgsMgr argsMgr) {
        super(argsMgr);
    }

    @Override
    protected File getProps() {
        final var dstProps = argsMgr.getDstProps();
        LOG.info("""
                        
                        ----------------------------------------
                        Working on merging 
                        \t%s 
                        to 
                        \t%s
                        ----------------------------------------""", argsMgr.getSrcProps().getAbsolutePath(), dstProps.getAbsolutePath());

        return dstProps;
    }

    @Override
    public boolean mustPersist(MetaGenerator dstMetaGen) throws JPropsException {
        return argsMgr.isApply();
    }

    private List<MergeProcessRecord> getAllRecords() throws JPropsException {
        final var targetOS = argsMgr.getTargetOS();
        final var srcMetaGen = new MetaGenerator(argsMgr.getSrcProps(), targetOS);
        final var dstMetaGen = new MetaGenerator(argsMgr.getDstProps(), targetOS);
        final var srcKeys = srcMetaGen.getKeysByLineType(LineType.VALUE_PAIR);
        final var dstKeys = dstMetaGen.getKeysByLineType(LineType.VALUE_PAIR);
        final var allKeys = new LinkedHashSet<String>();
        allKeys.addAll(srcKeys);
        allKeys.addAll(dstKeys);

        return allKeys.stream().map(___key -> new MergeProcessRecord(___key, srcMetaGen, dstMetaGen)).toList();
    }

    private boolean processRecords(final MergeConsumer passThruLogic,
                                   final MergeUpdate updateLogic,
                                   final MergeConsumer addLogic) throws JPropsException {
        boolean hasChange = false;
        final var records = getAllRecords();
        try {
            for (final var record : records) {
                final var key = record.key();
                final var srcMetaGen = record.srcMetaGen();
                final var dstMetaGen = record.dstMetaGen();
                final var srcPropMeta = Optional.ofNullable(srcMetaGen.getMetadata().get(key));
                if (srcPropMeta.isPresent()) {
                    final var dstPropsMeta = Optional.ofNullable(dstMetaGen.getMetadata().get(key));
                    executeLogics(key, srcPropMeta.get(), dstPropsMeta, passThruLogic, updateLogic, addLogic);
                }
            }
        }
        catch(MergeChangeException e) {
            hasChange = true;
        }
        return hasChange;
    }

    private boolean executeLogics(final String key,
                                  final PropsMeta srcPropMeta,
                                  final Optional<PropsMeta> dstPropsMeta,
                                  final MergeConsumer passThruLogic,
                                  final MergeUpdate updateLogic,
                                  final MergeConsumer addLogic) throws JPropsException {
        boolean hasChange = false;
        if (dstPropsMeta.isPresent()) {
            final var srcValue = srcPropMeta.currentValue();
            final var dstValue = dstPropsMeta.get().currentValue();
            if (!srcValue.equals(dstValue)) {
                hasChange = true;
                updateLogic.process(key, srcPropMeta, dstPropsMeta.get());
            } else {
                passThruLogic.accept(key, dstPropsMeta.get());
            }
        } else {
            hasChange = true;
            addLogic.accept(key, srcPropMeta);
        }
        return hasChange;
    }

    @Override
    public void persistLogic(PrintWriter writer, MetaGenerator metaGen) throws JPropsException {
        try(final var gLOG = LOG.groupLog("applyMerge")) {
            gLOG.info("--- Apply Merge [BEGIN] ---");
            processRecords(/*Pass through logic*/ (___key, ___propsMeta) -> outputWriter(writer, ___key, ___propsMeta),
                    /*Update logic*/ (___key, ___srcPropsMeta, ___dstPropsMeta) -> {
                        final var srcValue = ___srcPropsMeta.currentValue();
                        final var dstValue = ___dstPropsMeta.currentValue();
                        outputWriter(writer, ___key, ___srcPropsMeta);
                        gLOG.info("\t[%s] was updated from [%s] to [%s]", ___key, dstValue, srcValue);
                    },
                    /*Add logic*/ (___key, ___srcPropMeta) -> {
                        final var srcValue = ___srcPropMeta.currentValue();
                        outputWriter(writer, ___key, ___srcPropMeta);
                        gLOG.info("\t[%s] was added with the value [%s]", ___key, srcValue);
                    }
            );
            gLOG.info("--- Apply Merge [END] -----");
        }
    }

    @Override
    public void viewLogic(MetaGenerator metaGen) throws JPropsException {
        try(final var gLOG = LOG.groupLog("mergeView")) {
            gLOG.info("--- Merge View [BEGIN] ---");
            processRecords(/*Pass through logic*/ (___key, ___dstPropsMeta) -> {},
                    /*Update logic*/ (___key, ___srcPropsMeta, ___dstPropsMeta) -> {
                        final var srcValue = ___srcPropsMeta.currentValue();
                        final var dstValue = ___dstPropsMeta.currentValue();
                        gLOG.info("\t[%s] will be updated from [%s] to [%s]", ___key, dstValue, srcValue);
                    },
                    /*Add logic*/ (___key, ___srcPropsMeta) -> {
                        final var srcValue = ___srcPropsMeta.currentValue();
                        gLOG.info("\t[%s] will be added with the value [%s]", ___key, srcValue);
                    });
            gLOG.info("--- Merge View [END] -----");
        }
    }

    @Override
    public boolean shouldProcess(MetaGenerator metaGen) throws JPropsException {
        return processRecords(/*Pass through logic*/ (___key, ___dstPropsMeta) -> {},
                /*Update logic*/ (___key, ___srcPropsMeta, ___dstPropsMeta) -> {
                    throw new MergeChangeException();
                },
                /*Add logic*/ (___key, ___srcPropsMeta) -> {
                    throw new MergeChangeException();
                });
    }
}
