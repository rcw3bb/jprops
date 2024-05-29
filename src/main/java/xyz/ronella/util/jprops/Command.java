package xyz.ronella.util.jprops;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum Command {
    DUPLICATE,
    SORT, //TODO: To be implemented.
    MERGE //TODO: To be implemented.
    ;

    public static Optional<Command> of(final String name) {
        return Arrays.stream(Command.values()).filter(___command -> {
            final var targetName = Optional.ofNullable(name).orElse("___INVALID___").trim()
                    .toUpperCase(Locale.getDefault());
            return ___command.name().toUpperCase(Locale.getDefault()).equals(targetName);
        }).findFirst();
    }
}
