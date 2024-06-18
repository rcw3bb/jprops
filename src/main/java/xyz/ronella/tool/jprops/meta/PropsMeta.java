package xyz.ronella.tool.jprops.meta;

import xyz.ronella.trivial.decorator.StringBuilderAppender;
import xyz.ronella.trivial.handy.OSType;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * The PropsMeta class is a class that holds the metadata of the properties file.
 * @param count The count of the line.
 * @param currentValue The current value of the line.
 * @param prevValue The previous value of the line.
 * @param osType The operating system type.
 * @param lineNumber The line number.
 * @param lineType The line type.
 * @param isComplete The isComplete property.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public record PropsMeta(int count, String currentValue, String prevValue, OSType osType,
                        int lineNumber, LineType lineType, boolean isComplete, boolean isMultiline, boolean isBrokenMLine) {

    /**
     * Set the isPrevComplete property.
     * @param isComplete The isPrevComplete property.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta setComplete(boolean isComplete) {
        return new PropsMeta(this.count, this.currentValue, this.prevValue, this.osType,
                    this.lineNumber, this.lineType, isComplete, this.isMultiline, this.isBrokenMLine);
    }

    /**
     * Increment the count property.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta incrementCount() {
        return new PropsMeta(this.count + 1, this.currentValue, this.prevValue, this.osType,
                this.lineNumber, this.lineType, /*Current field becomes incomplete*/ false, this.isMultiline,
                this.isBrokenMLine);
    }

    /**
     * Set the currentValue property.
     * @param value The current value.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta setCurrentValue(final String value) {
        return new PropsMeta(this.count, value, this.prevValue, this.osType, this.lineNumber, this.lineType,
                /*Current field becomes incomplete*/ false, this.isMultiline, isBrokenMLine);
    }

    /**
     * Sync the prevValue property with the currentValue property.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta syncPrevValue() {
        return new PropsMeta(this.count, this.currentValue, this.currentValue, this.osType, this.lineNumber,
                this.lineType, this.isComplete, this.isMultiline, isBrokenMLine);
    }

    /**
     * Set the isMultiline property.
     * @param isMultiline The isMultiline property.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta setMultiLine(boolean isMultiline) {
        return new PropsMeta(this.count, this.currentValue, this.prevValue, this.osType,
                this.lineNumber, this.lineType, this.isComplete, isMultiline, isBrokenMLine);
    }

    /**
     * Set the isBrokenMLine property.
     * @param isBrokenMLine The isBrokenMLine property.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta setBrokenMLine(boolean isBrokenMLine) {
        return new PropsMeta(this.count, this.currentValue, this.prevValue, this.osType,
                this.lineNumber, this.lineType, this.isComplete, this.isMultiline, isBrokenMLine);
    }

    /**
     * Fix the broken multi-line.
     * @return The PropsMeta instance.
     *
     * @since 1.1.0
     */
    public PropsMeta fixBrokenMLine() {
        PropsMeta output = this;
        if (isMultiline && isBrokenMLine) {
            try(final var scanner = new Scanner(this.currentValue)) {
                final var sb = new StringBuilderAppender(
                        ___sb -> ___sb.append(!___sb.isEmpty() ? osType.getEOL().eol() : "")
                );
                final var mlineVals = new ArrayList<String>();
                while(scanner.hasNextLine()) {
                    final var line = scanner.nextLine();
                    mlineVals.add(line);
                }

                final var mlineSize = mlineVals.size();
                for (var i = 0; i < mlineSize; i++) {
                    final var line = mlineVals.get(i);
                    if (line.trim().endsWith("\\") || i == (mlineSize - 1)) {
                        sb.append(line);
                    }
                    else {
                        sb.append(String.format("%s\\", line));
                    }
                }

                output = output.setCurrentValue(sb.toString());
            }
        }
        return output;
    }
}
