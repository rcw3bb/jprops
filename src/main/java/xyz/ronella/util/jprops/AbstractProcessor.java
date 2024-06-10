package xyz.ronella.util.jprops;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.util.jprops.meta.LineType;
import xyz.ronella.util.jprops.meta.PropsMeta;
import xyz.ronella.util.jprops.support.ArgsMgr;

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
        processLogic();
    }

    /**
     * The logic that processes the command.
     */
    protected abstract void processLogic();

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
