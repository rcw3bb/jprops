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
import java.util.regex.Pattern;

public class MetaGenerator {

    private final static String MULTILINE_DELIM="^.*\\\\\\s*$";
    protected transient final File propsFile;
    protected transient final Map<String, PropsMeta> propsMetadata;
    protected transient final OSType osType;
    private transient final String valuePairPattern;
    private transient boolean notLoaded;

    public MetaGenerator(final File propsFile, final OSType osType) {
        this.propsFile = propsFile;
        this.propsMetadata = new LinkedHashMap<>();
        this.osType = osType;
        this.valuePairPattern = "^(\\s*[a-zA-Z_].*?)=((.*(" + osType.getEOL().eol() + ")?)*?)$";
        this.notLoaded = true;
    }

    public MetaGenerator(final File propsFile) {
        this(propsFile, OSType.identify());
    }

    public Map<String, PropsMeta> getMetadata() throws JPropsException {
        if (notLoaded) {
            try (final var fileReader = new Scanner(propsFile)) {
                final var rawLine = new StringBuilderAppender(___sb -> ___sb.append(!___sb.isEmpty() ? osType.getEOL().eol() : ""));
                var lineNumber = 0;
                while (fileReader.hasNextLine()) {
                    ++lineNumber;
                    final var currentLine = fileReader.nextLine();
                    rawLine.append(currentLine);
                    if (currentLine.matches(MULTILINE_DELIM)) {
                        continue;
                    }

                    final var matcher = RegExMatcher.find(valuePairPattern, rawLine.toString(), Pattern.MULTILINE);
                    if (matcher.matches()) {
                        final var key = matcher.group(1);
                        final var value = matcher.group(2);
                        updateMetadata(lineNumber, key, value);
                    } else {
                        updateMetadata(lineNumber, rawLine.toString());
                    }
                    rawLine.clear();
                }
                notLoaded = false;
            } catch (FileNotFoundException e) {
                throw new JPropsException(e);
            }
        }
        return propsMetadata;
    }

    protected void updateMetadata(final int lineNumber, final String key, final String value) throws JPropsException {
        final var oldMetadata = Optional.ofNullable(propsMetadata.get(key))
                .orElse(new PropsMeta(0, value, value, osType, lineNumber, LineType.VALUE_PAIR));

        validateValue(key, oldMetadata, value);

        final var newMetaData = new PropsMeta(oldMetadata.count() + 1, oldMetadata.currentValue(),
                oldMetadata.prevValue(), osType, oldMetadata.lineNumber(), oldMetadata.lineType());

        propsMetadata.put(key, newMetaData);
    }


    protected void updateMetadata(final int lineNumber, final String text) {
        final var key = String.format("_LINE%d_", lineNumber);
        final var lineType = text.matches("\\s*#.*") ? LineType.COMMENT : LineType.TEXT;
        final var metadata = new PropsMeta(1, text, null, osType, lineNumber, lineType);

        propsMetadata.put(key, metadata);
    }

    protected void validateValue(final String key, final PropsMeta metadata, final String value)
            throws JPropsException {

        final var optPrevValue = Optional.ofNullable(metadata.prevValue());

        if (optPrevValue.isPresent() && !optPrevValue.get().equals(value)) {
            final var message = String.format(
                            """
                            -------------------------------------------------
                            %s has different previous and current value
                            ---[Previously Value]----------------------------
                            %s
                            ---[Current Value]-------------------------------
                            %s
                            -------------------------------------------------
                            """,
                    key, metadata.prevValue(), value);

            throw new ValueMismatchException(message);
        }
    }
}
