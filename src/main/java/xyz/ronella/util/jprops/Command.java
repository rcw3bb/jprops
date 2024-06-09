package xyz.ronella.util.jprops;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The Command enum provides the list of available commands.
 * This enum also provides a method to convert a string command to its equivalent enum.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
public enum Command {

    /**
     * The HELP command.
     */
    HELP,

    /**
     * The DUPLICATE command.
     */
    DUPLICATE,

    /**
     * The SORT command.
     */
    SORT, //TODO: To be implemented.

    /**
     * The MERGE command.
     */
    MERGE, //TODO: To be implemented.

    /**
     * The MLINE (i.e. Multiline) command.
     */
    MLINE //TODO: To be implemented.
    ;

    /**
     * Converts a string command to its equivalent enum.
     * @param command The string command.
     * @return The equivalent enum.
     */
    public static Optional<Command> of(final String command) {
        return Arrays.stream(Command.values()).filter(___command -> {
            final var targetName = Optional.ofNullable(command).orElse("___INVALID___").trim()
                    .toUpperCase(Locale.getDefault());
            return ___command.name().toUpperCase(Locale.getDefault()).equals(targetName);
        }).findFirst();
    }
}
