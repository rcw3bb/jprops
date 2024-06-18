package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.FileMgr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Function;

/**
 * The MergeProcessor class is the implementation of the Processor interface for the merge command.
 * This class extends the AbstractProcessor class.
 * This class merges the properties file.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class MergeProcessor  extends AbstractProcessor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(MergeProcessor.class));

    /**
     * The constructor.
     *
     * @param argsMgr The ArgsMgr instance.
     */
    public MergeProcessor(ArgsMgr argsMgr) {
        super(argsMgr);
    }

    /**
     * The method that processes the command.
     */
    @Override
    public void process() {
        LOG.info("""
                        
                        ----------------------------------------
                        Working on merging 
                        \t%s 
                        to 
                        \t%s
                        ----------------------------------------""", argsMgr.getSrcProps().getAbsolutePath(), argsMgr.getDstProps().getAbsolutePath());
        processLogic();
    }

    /**
     * The logic that processes the command.
     */
    @Override
    protected void processLogic() {
        if (argsMgr.isApply()) {
            applyMerge();
        }
        else {
            mergeView();
        }
    }

    private boolean mergeProcess(final LoggerPlus.GroupLogger gLOG,
                              final Function<MergeProcessRecord, Boolean> mergeLogic) throws JPropsException {

        final var targetOS = argsMgr.getTargetOS();
        final var srcMetaGen = new MetaGenerator(argsMgr.getSrcProps(), targetOS);
        final var dstMetaGen = new MetaGenerator(argsMgr.getDstProps(), targetOS);

        final var srcKeys = srcMetaGen.getKeysByLineType(LineType.VALUE_PAIR);
        final var dstKeys = dstMetaGen.getKeysByLineType(LineType.VALUE_PAIR);
        final var allKeys = new LinkedHashSet<String>();
        allKeys.addAll(srcKeys);
        allKeys.addAll(dstKeys);

        var hasChanges = false;
        gLOG.info("--- Merging Properties [BEGIN] ---");
        for (final var key : allKeys) {
            hasChanges |= mergeLogic.apply(new MergeProcessRecord(key, srcMetaGen, dstMetaGen));
        }
        if (!hasChanges) {
            gLOG.info("Nothing to merge.");
        }
        gLOG.info("--- Merging Properties [END] -----");

        return hasChanges;
    }

    private boolean mergeView() {
        try(final var gLOG = LOG.groupLog("mergeView")) {
            try {
                return mergeProcess(gLOG, ___processRecord -> {
                    try {
                        return mergeViewLogic(___processRecord.key(), ___processRecord.srcMetaGen(),
                                ___processRecord.dstMetaGen());
                    } catch (JPropsException jpe) {
                        throw new RuntimeException(jpe);
                    }
                });
            } catch (JPropsException | RuntimeException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
        return false;
    }

    private void applyMerge() {
        try(final var gLOG = LOG.groupLog("applyMerge")) {
            try {
                final var hasChange = mergeView();
                if (hasChange) {
                    gLOG.info("Applying mergeView findings.");
                    final var props = argsMgr.getDstProps();
                    final var tmpFile = FileMgr.createTmpFile(props);
                    gLOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());

                    try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                        mergeProcess(gLOG, ___processRecord -> {
                            try {
                                return applyMergeLogic(___processRecord.key(), ___processRecord.srcMetaGen(),
                                        ___processRecord.dstMetaGen(), writer);
                            } catch (JPropsException exception) {
                                throw new RuntimeException(exception);
                            }
                        });
                    }
                    FileMgr.safeMove(argsMgr.getCommand(), tmpFile, props).ifPresent(___backupFile ->
                            gLOG.info("Backup file created: %s", ___backupFile));
                }
            } catch (JPropsException | RuntimeException | IOException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }

    private boolean applyMergeLogic(final String key, final MetaGenerator srcMetaGen, final MetaGenerator dstMetaGen,
                                    final PrintWriter writer)
            throws JPropsException {

        try(final var gLOG = LOG.groupLog("applyMerge")) {
            final var srcPropMeta = srcMetaGen.getMetadata().get(key);
            final var srcValue = srcPropMeta.currentValue();
            final var dstPropsMeta = Optional.ofNullable(dstMetaGen.getMetadata().get(key));

            var hasChange = false;

            if (dstPropsMeta.isPresent()) {
                final var dstValue = dstPropsMeta.get().currentValue();
                if (!srcValue.equals(dstValue)) {
                    hasChange = true;
                    outputWriter(writer, key, srcPropMeta);
                    gLOG.info("\t[%s] was updated from [%s] to [%s]", key, dstValue, srcValue);
                }
                else {
                    outputWriter(writer, key, dstPropsMeta.get());
                }
            } else {
                hasChange = true;
                outputWriter(writer, key, srcPropMeta);
                gLOG.info("\t[%s] was added with the value [%s]", key, srcValue);
            }

            return hasChange;
        }
    }

    private boolean mergeViewLogic(final String key, final MetaGenerator srcMetaGen, final MetaGenerator dstMetaGen)
            throws JPropsException {

        try(final var gLOG = LOG.groupLog("mergeView")) {
            final var srcPropMeta = Optional.ofNullable(srcMetaGen.getMetadata().get(key));
            var hasChange = false;

            if (srcPropMeta.isPresent()) {
                final var srcValue = srcPropMeta.get().currentValue();
                final var dstPropsMeta = Optional.ofNullable(dstMetaGen.getMetadata().get(key));

                if (dstPropsMeta.isPresent()) {
                    final var dstValue = dstPropsMeta.get().currentValue();
                    if (!srcValue.equals(dstValue)) {
                        hasChange = true;
                        gLOG.info("\t[%s] will be updated from [%s] to [%s]", key, dstValue, srcValue);
                    }
                } else {
                    hasChange = true;
                    gLOG.info("\t[%s] will be added with the value [%s]", key, srcValue);
                }
            }

            return hasChange;
        }
    }
}
