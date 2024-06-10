package xyz.ronella.tool.jprops.impl;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.AbstractProcessor;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.util.Optional;

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
        mergeView();
    }

    private void mergeView() {
        try(final var gLOG = LOG.groupLog("mergeView")) {
            final var srcMetaGen = new MetaGenerator(argsMgr.getSrcProps());
            final var dstMetaGen = new MetaGenerator(argsMgr.getDstProps());
            try {
                final var srcKeys = srcMetaGen.getKeysByLineType(LineType.VALUE_PAIR);
                var hasChanges = false;
                gLOG.info("--- Merging Properties [BEGIN] ---");
                for (final var key : srcKeys) {
                    hasChanges |= mergeViewLogic(key, srcMetaGen, dstMetaGen);
                }
                if (!hasChanges) {
                    gLOG.info("Nothing to merge.");
                }
                gLOG.info("--- Merging Properties [END] -----");
            } catch (JPropsException e) {
                gLOG.error(LOG.getStackTraceAsString(e));
            }
        }
    }

    private boolean mergeViewLogic(final String key, final MetaGenerator srcMetaGen, final MetaGenerator dstMetaGen)
            throws JPropsException {

        try(final var gLOG = LOG.groupLog("mergeView")) {
            final var srcValue = srcMetaGen.getMetadata().get(key).currentValue();
            final var dstPropsMeta = Optional.ofNullable(dstMetaGen.getMetadata().get(key));
            var hasChange = false;

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

            return hasChange;
        }
    }
}
