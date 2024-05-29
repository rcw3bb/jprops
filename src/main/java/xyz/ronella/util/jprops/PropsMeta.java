package xyz.ronella.util.jprops;

import xyz.ronella.trivial.handy.OSType;

public record PropsMeta(int count, String currentValue, String completedValue, boolean isComplete, boolean isInitial,
                        OSType osType) {
}
