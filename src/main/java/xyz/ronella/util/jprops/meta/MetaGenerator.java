package xyz.ronella.util.jprops.meta;

import xyz.ronella.trivial.decorator.StringBuilderAppender;
import xyz.ronella.trivial.handy.OSType;
import xyz.ronella.trivial.handy.RegExMatcher;
import xyz.ronella.util.jprops.JPropsException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class MetaGenerator {

    private final static String MULTILINE_DELIM=".*\\\\\\s*$";
    private final static String VALUE_PAIR="^(\\s*[a-zA-Z_].*?)=(.*)$";
    protected transient final File propsFile;
    protected transient final Map<String, PropsMeta> propsMetadata;
    protected transient final OSType osType;
    public MetaGenerator(final File propsFile, final OSType osType) {
        this.propsFile = propsFile;
        this.propsMetadata = new LinkedHashMap<>();
        this.osType = osType;
    }

    public MetaGenerator(final File propsFile) {
        this(propsFile, OSType.identify());
    }

    protected boolean isValueComplete(final String value) {
        return !value.matches(MULTILINE_DELIM);
    }

    public Map<String, PropsMeta> getMetadata() throws JPropsException {
        try(final var fileReader = new Scanner(propsFile)) {
            Optional<String> key = Optional.empty();

            while(fileReader.hasNextLine()) {
                final var rawLine = fileReader.nextLine();
                final var matcher = RegExMatcher.find(VALUE_PAIR, rawLine);

                if (matcher.matches()) {
                    key = Optional.of(matcher.group(1));
                    final var value = matcher.group(2);
                    final var isComplete = isValueComplete(value);

                    updateMetadata(key.get(), value, isComplete);
                }

                if (key.isPresent()) {
                    assembleMultiLine(key.get(), rawLine);
                }
            }
        } catch (FileNotFoundException e) {
            throw new JPropsException(e);
        }

        return propsMetadata;
    }

    protected void updateMetadata(final String key, final String value, boolean isComplete) throws JPropsException {
        final var oldMetadata = Optional.ofNullable(propsMetadata.get(key))
                .orElse(new PropsMeta(0, value, value, isComplete, true, osType));

        validateValue(key, isComplete, oldMetadata, value);

        final var newMetaData = new PropsMeta(oldMetadata.count() + 1, oldMetadata.currentValue(),
                oldMetadata.completedValue(), isComplete, oldMetadata.isInitial(), osType);

        propsMetadata.put(key, newMetaData);
    }

    protected void validateValue(final String key, boolean isComplete, final PropsMeta metadata, final String value)
            throws JPropsException {

        final var optCompletedValue = Optional.ofNullable(metadata.completedValue());

        if (isComplete && optCompletedValue.isPresent() && !optCompletedValue.get().equals(value)) {
            final var message = String.format(
                            """
                            -------------------------------------------------
                            %s has different completedValue and currentValue
                            ---[Previously Completed Value]------------------
                            %s
                            ---[Current Value]-------------------------------
                            %s
                            -------------------------------------------------
                            """,
                    key, metadata.completedValue(), value);

            throw new ValueMismatchException(message);
        }
    }

    protected void assembleMultiLine(final String key, final String value) throws JPropsException {
        if (key != null) {
            final var oldMetadata = propsMetadata.get(key);
            final var wasNotCompleted = !oldMetadata.isComplete();
            final var wasNotInitial = !oldMetadata.isInitial();
            final var isComplete = isValueComplete(value);

            if (wasNotCompleted) {
                if (wasNotInitial) {
                    final var newValue = new StringBuilderAppender(oldMetadata.currentValue(),
                            ___sb -> ___sb.append(!___sb.isEmpty() ? osType.getEOL().eol() : ""));
                    newValue.append(value);

                    final var newMetadata = new PropsMeta(oldMetadata.count(), newValue.toString()
                            , isComplete ? newValue.toString() : null, isComplete, false, osType);
                    validateValue(key, isComplete, oldMetadata, newValue.toString());
                    propsMetadata.put(key, newMetadata);
                } else {
                    final var newMetadata = new PropsMeta(oldMetadata.count(),
                            oldMetadata.currentValue(), oldMetadata.currentValue(), isComplete, false, osType);
                    propsMetadata.put(key, newMetadata);
                }
            }
        }
    }
}
