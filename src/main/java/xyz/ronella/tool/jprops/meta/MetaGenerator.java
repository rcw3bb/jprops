package xyz.ronella.tool.jprops.meta;

import xyz.ronella.tool.jprops.JPropsException;
import xyz.ronella.trivial.decorator.FileNomen;
import xyz.ronella.trivial.decorator.StringBuilderAppender;
import xyz.ronella.trivial.decorator.TextFile;
import xyz.ronella.trivial.handy.OSType;
import xyz.ronella.trivial.handy.RegExMatcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MetaGenerator class is the class that generates the per line metadata of the properties file.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public class MetaGenerator {

    private final static String MULTILINE_DELIM="\\";
    private static final String MLINE_CONTINUE = "^\\s*((#.*)|)";
    private static final String COMMENT_LINE = "\\s*#.*";
    private static final String /*Non value pair key pattern*/ NON_VPK_PATTERN = "_LINE%d_";

    /**
     * The propsFile instance variable.
     */
    protected transient final File propsFile;

    /**
     * The propsMetadata instance variable.
     */
    protected transient final Map<String, PropsMeta> propsMetadata;

    /**
     * The osType instance variable.
     */
    protected transient final OSType osType;
    protected transient final Charset encoding;
    private transient final String valuePairPattern;
    private transient boolean notLoaded;

    /**
     * The constructor.
     * @param propsFile The properties file.
     * @param osType The operating system type.
     */
    public MetaGenerator(final File propsFile, final OSType osType, final Charset encoding) {
        this.propsFile = propsFile;
        this.propsMetadata = new LinkedHashMap<>();
        this.osType = Optional.ofNullable(osType).orElseGet(() -> OSType.of(new TextFile(propsFile).getEndOfLine()));
        this.valuePairPattern = "^(\\s*[a-zA-Z_].*?)=((.*(" + this.osType.getEOL().eol() + ")?)*?)$";
        this.notLoaded = true;
        this.encoding = encoding;
    }

    /**
     * The constructor.
     * @param propsFile The properties file.
     */
    public MetaGenerator(final File propsFile) {
        this(propsFile, OSType.of(new TextFile(propsFile).getEndOfLine()), StandardCharsets.UTF_8);
    }

    /**
     * The getMetadata method returns the per line metadata of the properties file.
     * @return The metadata of the properties file.
     * @throws JPropsException When an error occurs.
     */
    public Map<String, PropsMeta> getMetadata() throws JPropsException {
        if (notLoaded) {
            try (final var fileReader = new Scanner(propsFile, encoding)) {
                validatePropsFile();
                final var rawLine = new StringBuilderAppender(___sb -> ___sb.append(!___sb.isEmpty() ? osType.getEOL().eol() : ""));
                var lineNumber = 0;
                String lastKey = null;
                while (fileReader.hasNextLine()) {
                    ++lineNumber;
                    final var currentLine = fileReader.nextLine();
                    rawLine.append(currentLine);
                    final var matcher = RegExMatcher.find(valuePairPattern, rawLine.toString(), Pattern.MULTILINE);
                    lastKey = updateMetadata(lineNumber, matcher, rawLine, currentLine, lastKey).orElse(lastKey);
                    rawLine.clear();
                }
                if (lastKey != null) {
                    // Final status update if lastKey was not completed.
                    updateStatus(lastKey);
                }
                notLoaded = false;
            } catch (IOException ioe) {
                throw new PropertiesNotFoundException(ioe.getMessage());
            }
        }
        return propsMetadata;
    }

    private void validatePropsFile() throws JPropsException {
        final var extName = new FileNomen(propsFile).getExtension().orElse("___none___");
        if (!extName.equalsIgnoreCase("properties")) {
            throw new FileExtensionException(String.format("%s is not a valid extension name.", extName));
        }
    }

    /**
     * The updateMetadata method updates the metadata.
     * @param lineNumber The line number.
     * @param matcher The matcher.
     * @param rawLine The raw line.
     * @param currentLine The current line.
     * @param lastKey The last key.
     * @return The key.
     * @throws JPropsException When an error occurs.
     */
    protected Optional<String> updateMetadata(final int lineNumber,
                                              final Matcher matcher,
                                              final StringBuilderAppender rawLine,
                                              final String currentLine,
                                              final String lastKey) throws JPropsException {
        Optional<String> output = Optional.empty();
        if (matcher.matches()) {
            final var key = matcher.group(1);
            final var value = matcher.group(2);
            if (lastKey != null) {
                updateStatus(lastKey);
            }
            updateMetadata(lineNumber, key, value);
            output = Optional.of(key);
        } else if (!currentLine.matches(MLINE_CONTINUE)) {
            updateMetadata(lastKey, currentLine);
        }
        else {
            updateStatus(lastKey);
            updateMetadata(lineNumber, rawLine.toString());
        }
        return output;
    }

    /**
     * The updateStatus method updates the status.
     * @param lastKey The last key.
     */
    protected void updateStatus(final String lastKey) throws JPropsException {
        final var optLastKey = Optional.ofNullable(lastKey);
        if (optLastKey.isPresent()) {
            final var oldMetaData = propsMetadata.get(lastKey);
            if (!oldMetaData.isComplete()) {
                var newMetaData = oldMetaData.setComplete(true);
                final var optPrevValue = Optional.ofNullable(newMetaData.prevValue());
                propsMetadata.put(lastKey, newMetaData);
                if (optPrevValue.isPresent()) {
                    validateValue(lastKey);
                }
                newMetaData = newMetaData.syncPrevValue();
                propsMetadata.put(lastKey, newMetaData);
            }
        }
    }

    /**
     * The updateMetadata method updates the metadata.
     * @param lineNumber The line number.
     * @param key The key.
     * @param value The value.
     * @throws JPropsException When an error occurs.
     */
    protected void updateMetadata(final int lineNumber, final String key, final String value) throws JPropsException {
        final var oldMetadata = Optional.ofNullable(propsMetadata.get(key))
                .orElse(new PropsMeta(0, /*Current value*/ value, /*Previous value*/ null, osType, lineNumber,
                        LineType.VALUE_PAIR,
                        /*Current is not complete initially*/ false,
                        /*Not multiline by default*/ false,
                        /*Not broken multiline by default*/ false));

        final var newMetaData = oldMetadata.incrementCount().setCurrentValue(value);

        propsMetadata.put(key, newMetaData);
    }

    /**
     * The updateMetadata method updates the metadata.
     * @param key The key.
     * @param value The value.
     *
     * @since 1.1.0
     */
    protected void updateMetadata(final String key, final String value) {
        final var oldMetadata = propsMetadata.get(key);
        final var oldValue = oldMetadata.currentValue();
        final var newValue = new StringBuilderAppender(___sb -> ___sb.append(!___sb.isEmpty() ? osType.getEOL().eol() : ""));

        newValue.append(oldValue);
        newValue.append(value);

        var newMetadata = oldMetadata.setCurrentValue(newValue.toString()).setMultiLine(true);
        if (newMetadata.isMultiline() && !newMetadata.isBrokenMLine() && !oldValue.trim().endsWith(MULTILINE_DELIM)) {
            newMetadata = newMetadata.setBrokenMLine(true);
        }

        propsMetadata.put(key, newMetadata);
    }

    /**
     * The updateMetadata method updates the metadata.
     * @param lineNumber The line number.
     * @param text The text.
     */
    protected void updateMetadata(final int lineNumber, final String text) {
        final var key = String.format(NON_VPK_PATTERN, lineNumber);
        final var lineType = text.matches(COMMENT_LINE) ? LineType.COMMENT : LineType.TEXT;
        final var metadata = new PropsMeta(1, /*Current value*/ text, /*Previous value*/ null, osType,
                lineNumber, lineType, /*Current is complete*/ true, /*Not multiline by default*/ false,
                /*Not broken multiline by default*/ false);

        propsMetadata.put(key, metadata);
    }

    /**
     * The validateValue method validates the metadata.
     * @param key The key.
     * @throws JPropsException When an error occurs.
     */
    protected void validateValue(final String key)
            throws JPropsException {

        final var optPropsMeta = Optional.ofNullable(this.propsMetadata.get(key));
        if (optPropsMeta.isPresent()) {
            final var propsMeta = optPropsMeta.get();
            final var optPrevValue = Optional.ofNullable(propsMeta.prevValue());
            final var optCurrentValue = Optional.ofNullable(propsMeta.currentValue());

            if (propsMeta.isComplete()
                    && optPrevValue.isPresent() && optCurrentValue.isPresent()
                    && !optPrevValue.get().equals(optCurrentValue.get())) {

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
                        key, propsMeta.prevValue(), optCurrentValue.get());

                throw new ValueMismatchException(message);
            }
        }
    }

    /**
     * The getKeysByLineType method returns the keys by line type.
     * @param lineType The line type.
     * @return The keys.
     * @throws JPropsException When an error occurs.
     */
    public List<String> getKeysByLineType(final LineType lineType) throws JPropsException {
        return getMetadata().entrySet().stream()
                .filter(___entrySet -> ___entrySet.getValue().lineType() == lineType)
                .map(Map.Entry::getKey)
                .toList();
    }
}
