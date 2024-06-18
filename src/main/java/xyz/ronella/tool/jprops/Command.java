package xyz.ronella.tool.jprops;

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
    HELP("hlp"),

    /**
     * The DUPLICATE command.
     */
    DUPLICATE("dup"),

    /**
     * The SORT command.
     */
    SORT("srt"),

    /**
     * The MERGE command.
     */
    MERGE("mrg"),

    /**
     * The BMLINE (i.e. Broken Multiline) command.
     */
    BMLINE("bml");

    private String code;

    Command(final String code) {
        this.code = code;
    }

    /**
     * The getCode method returns the code of the command.
     * @return The code of the command.
     */
    public String getCode() {
        return code;
    }

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
