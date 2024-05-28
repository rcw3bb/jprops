package xyz.ronella.util.jprops;

import xyz.ronella.trivial.decorator.Mutable;
import xyz.ronella.trivial.decorator.StringBuilderAppender;
import xyz.ronella.trivial.handy.OSType;
import xyz.ronella.trivial.handy.RegExMatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class MetaGenerator {

    protected transient final File propsFile;
    protected transient final Map<String, PropsMeta> propsMetadata;

    private final static String MULTILINE_DELIM=".*\\\\\\s*$";
    public MetaGenerator(final File propsFile) {
        this.propsFile = propsFile;
        this.propsMetadata = new LinkedHashMap<>();
    }

    protected boolean isValueComplete(final String value) {
        return !value.matches(MULTILINE_DELIM);
    }

    public Map<String, PropsMeta> getMetadata() {
        try(final var fileReader = new Scanner(propsFile)) {
            final var lastKey = new Mutable<String>(null);
            while(fileReader.hasNextLine()) {
                final var rawLine = fileReader.nextLine();
                final var validationError = new Mutable<Throwable>(null);
                RegExMatcher.find("^(\\s*[a-zA-Z_].*?)=(.*)$", rawLine, ___matcher -> {
                    final var key = ___matcher.group(1);
                    final var value = ___matcher.group(2);
                    final var isComplete = isValueComplete(value);
                    try {
                        lastKey.set(updateMetadata(key, value, isComplete));
                    }
                    catch (ValueMismatchException vme) {
                        validationError.set(vme);
                    }
                });

                if (Optional.ofNullable(validationError.get()).isPresent()) {
                    throw new JPropsException(validationError.get());
                }

                assembleMultiLine(lastKey.get(), rawLine);
            }
        } catch (FileNotFoundException e) {
            throw new JPropsException(e);
        }

        return propsMetadata;
    }

    protected String updateMetadata(final String key, final String value, boolean isComplete) {
        final var oldMetadata = Optional.ofNullable(propsMetadata.get(key))
                .orElse(new PropsMeta(0, value, value, isComplete, true));
        validateValue(key, isComplete, oldMetadata, value);
        final var newMetaData = new PropsMeta(oldMetadata.count() + 1, oldMetadata.currentValue(),
                oldMetadata.completedValue(), isComplete, oldMetadata.isInitial());

        propsMetadata.put(key, newMetaData);
        return key;
    }

    protected void validateValue(final String key, boolean isComplete, final PropsMeta metadata, final String value) {
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

    private void assembleMultiLine(final String key, final String value) {
        Optional.ofNullable(key).ifPresent(___key -> {
            final var oldMetadata = propsMetadata.get(___key);
            final var wasNotCompleted = !oldMetadata.isComplete();
            final var wasNotInitial = !oldMetadata.isInitial();
            final var isComplete = isValueComplete(value);
            final var newValue = new StringBuilderAppender(oldMetadata.currentValue(),
                    ___sb -> ___sb.append(!___sb.isEmpty() ? OSType.Linux.getEOL().eol() : ""));
            newValue.append(value);
            if (wasNotCompleted) {
                if (wasNotInitial) {
                    final var newMetadata = new PropsMeta(oldMetadata.count(), newValue.toString()
                            , isComplete ? newValue.toString() : null, isComplete, false);
                    validateValue(___key, isComplete, oldMetadata, newValue.toString());
                    propsMetadata.put(___key, newMetadata);
                } else {
                    final var newMetadata = new PropsMeta(oldMetadata.count(),
                            oldMetadata.currentValue(), oldMetadata.currentValue(), isComplete, false);
                    propsMetadata.put(___key, newMetadata);
                }
            }
        });
    }
}
