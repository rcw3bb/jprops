package xyz.ronella.util.jprops.meta;

import xyz.ronella.trivial.handy.OSType;

public record PropsMeta(int count, String currentValue, String prevValue, OSType osType, int lineNumber, LineType lineType) {
}
