package xyz.ronella.util.jprops.meta;

import xyz.ronella.trivial.handy.OSType;

/**
 * The PropsMeta class is a class that holds the metadata of the properties file.
 * @param count The count of the line.
 * @param currentValue The current value of the line.
 * @param prevValue The previous value of the line.
 * @param osType The operating system type.
 * @param lineNumber The line number.
 * @param lineType The line type.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public record PropsMeta(int count, String currentValue, String prevValue, OSType osType, int lineNumber, LineType lineType) {
}
