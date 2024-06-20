package xyz.ronella.tool.jprops;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.tool.jprops.meta.LineType;
import xyz.ronella.tool.jprops.meta.MetaGenerator;
import xyz.ronella.tool.jprops.meta.PropsMeta;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.tool.jprops.util.FileMgr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * The AbstractProcessor class provides a base implementation for the Processor interface.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public abstract class AbstractProcessor implements Processor {
    private final static LoggerPlus LOG = new LoggerPlus(LoggerFactory.getLogger(AbstractProcessor.class));

    /**
     * The ArgsMgr instance.
     */
    protected transient final ArgsMgr argsMgr;

    /**
     * The constructor.
     * @param argsMgr The ArgsMgr instance.
     */
    public AbstractProcessor(final ArgsMgr argsMgr) {
        this.argsMgr = argsMgr;
    }

    /**
     * The method that processes the command.
     */
    @Override
    public void process() {
        LOG.info("Working on %s", argsMgr.getProps().getAbsolutePath());

        final var metaGen = new MetaGenerator(argsMgr.getProps(), argsMgr.getTargetOS());

        processLogic(); //TODO: Must be deleted.

        try {
            if (shouldProcess(metaGen)) {
                if (mustPersist(metaGen)) {
                    final File tmpFile = FileMgr.createTmpFile(argsMgr.getProps());
                    LOG.debug("Temp file created: %s", tmpFile.getAbsolutePath());
                    try (final var writer = new PrintWriter(new FileWriter(tmpFile))) {
                        persistLogic(writer, metaGen);
                    }
                    FileMgr.safeMove(argsMgr.getCommand(), tmpFile, argsMgr.getProps()).ifPresent(___backupFile ->
                            LOG.info("Backup file created: %s", ___backupFile));
                } else {
                    viewLogic(metaGen);
                }
            }
            else {
                LOG.info("Nothing to process.");
            }
        }
        catch (JPropsException | IOException jpe) {
            LOG.error(LOG.getStackTraceAsString(jpe));
        }
    }

    /**
     * The method that writes the properties to the output.
     * @param metaGen The MetaGenerator instance.
     * @return true if the update to the properties must be persisted.
     *
     * @since 1.2.0
     */
    public boolean mustPersist(final MetaGenerator metaGen) throws JPropsException {
        return false;
    }

    /**
     * The method that persist the properties update.
     * @param metaGen The MetaGenerator instance.
     * @since 1.2.0
     */
    public void persistLogic(final PrintWriter writer, final MetaGenerator metaGen) throws JPropsException {

    }

    /**
     * The method that view analyzed by the processor.
     * @param metaGen The MetaGenerator instance.
     * @since 1.2.0
     */
    public void viewLogic(final MetaGenerator metaGen) throws JPropsException {

    }

    /**
     * The method that determines if the command should be processed.
     * @param metaGen The MetaGenerator instance.
     * @return true if the command should be processed.
     */
    public boolean shouldProcess(final MetaGenerator metaGen) throws JPropsException {
        return false;
    }

    /**
     * The logic that processes the command.
     */
    protected void processLogic() {
        //TODO: To be removed.
    }

    /**
     * The method that writes the properties to the output.
     * @param writer The PrintWriter instance.
     * @param key The key.
     * @param value The PropsMeta instance.
     */
    protected void outputWriter(final PrintWriter writer, final String key, final PropsMeta value) {
        final var eol = value.osType().getEOL().eol();
        final var currentValue = value.currentValue();

        Optional.of(value.lineType())
                .filter(___lineType -> ___lineType == LineType.VALUE_PAIR)
                .map(___isValuePair -> /* Value pair format */ "%s=%s%s")
                .ifPresentOrElse(
                        ___format -> writer.printf(___format, key, currentValue, eol),
                        ()-> writer.printf(/* Generic format */ "%s%s", currentValue, eol)
                );
    }


}
