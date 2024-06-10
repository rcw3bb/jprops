package xyz.ronella.util.jprops.util;

import org.apache.commons.cli.*;
import xyz.ronella.util.jprops.Command;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The ArgsMgr class is the class that manages the command line arguments.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class ArgsMgr {

    private Command command;

    private File props;

    private boolean dedupe;

    private transient boolean exit;

    private ArgsMgr() {
    }

    /**
     * The shouldExit method returns true if the application should exit.
     * @return True if the application should exit.
     */
    public boolean shouldExit() {
        return exit;
    }

    /**
     * The setShouldExit method sets the exit flag.
     * @param exit The exit flag.
     */
    public void setShouldExit(boolean exit) {
        this.exit = exit;
    }

    /**
     * The getProps method returns the properties file.
     * @return The properties file.
     */
    public File getProps() {
        return props;
    }

    /**
     * The setProps method sets the properties file.
     * @param props The properties file.
     */
    public void setProps(File props) {
        this.props = props;
    }

    /**
     * The getCommand method returns the command.
     * @return The command.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * The setCommand method sets the command.
     * @param command The command.
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * The isDedupe method returns true if the dedupe flag is set.
     * @return True if the dedupe flag is set.
     */
    public boolean isDedupe() {
        return dedupe;
    }

    /**
     * The setDedupe method sets the dedupe flag.
     * @param dedupe The dedupe flag.
     */
    public void setDedupe(boolean dedupe) {
        this.dedupe = dedupe;
    }

    private static void addPropOption(final Options options) {
        final var option = new Option("p", "properties", true
                , "The properties file.");
        option.setRequired(true);
        options.addOption(option);
    }

    private static void addHelpOption(final Options options) {
        final var option = new Option("h", "help", false
                , "Shows the help information.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addListOption(final Options options, final String description) {
        final var option = new Option("list", false
                , description);
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addDedupeOption(final Options options) {
        final var option = new Option("dedupe", false
                , "Remove duplication of fields.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addGenericParamOption(final Options options) {
        final var genericParam = new Option("D", true, "Generic Parameter");
        genericParam.setRequired(false);
        genericParam.setArgName("parameter=currentValue");
        genericParam.setArgs(2);
        genericParam.setValueSeparator('=');
        options.addOption(genericParam);
    }

    private static void helpInfo(final ArgsMgr argMgr, final Options options) {
        final var formatter = new HelpFormatter();
        final var command = argMgr.getCommand();
        final var appName = String.format("%s %s", AppInfo.INSTANCE.getAppName(), command.name().toLowerCase(Locale.ROOT));
        formatter.printHelp(appName, options);

        if (command == Command.HELP) {
            System.out.println("""
                    Commands available:
                    duplicate - The command for processing managing duplicate fields.
                    """);
        }

        argMgr.setShouldExit(true);
    }

    private static String[] prepareArgs(final ArgsMgr argManager, final String[] args) {

        final var commandPattern = "^\\s*[a-zA-Z_].*";

        final var commandArg = Optional.of(args)
                .filter(___args -> Arrays.stream(___args).findAny().isPresent())
                .map(___args -> /*Extract the first argument*/ ___args[0])
                .filter(___arg -> ___arg.trim().matches(commandPattern));

        final var newArgs = Optional.of(args)
                .filter(___args -> ___args.length > 1)
                .map(Arrays::asList)
                .map(___args -> ___args.subList(1, ___args.size()))
                .map(___args -> ___args.toArray(new String[] {}))
                .orElse(commandArg.isPresent() ? new String[] {} : args);

        commandArg.ifPresent(___command -> argManager.setCommand(Command.of(___command).get()));

        return newArgs;
    }

    /**
     * The build method creates an instance of the ArgsMgr.
     * @param args The command line arguments.
     * @return The ArgsMgr instance.
     * @throws MissingCommandException When the command is missing.
     */
    public static ArgsMgr build(final String[] args) throws MissingCommandException {
        final var argManager = new ArgsMgr();
        final var options = new Options();

        final var parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            final var newArgs = prepareArgs(argManager, args);
            initOptions(argManager, options);

            cmd = parser.parse(options, newArgs);
            if (Command.HELP == argManager.getCommand()) {
                helpInfo(argManager, options);
            } else {
                initFields(argManager.getCommand(), argManager, cmd);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpInfo(argManager, options);
        }

        return argManager;
    }

    private static void initOptions(final ArgsMgr argManager, final Options options) throws MissingCommandException {
        final var command = argManager.getCommand();

        if (command == null) {
            throw new MissingCommandException();
        }

        switch (command) {
            case DUPLICATE -> {
                addHelpOption(options);
                addPropOption(options);
                addDedupeOption(options);
            }
            case SORT -> {
                addHelpOption(options);
                addPropOption(options);
            }
            case MERGE -> {
                //TODO: To be implemented.
            }
            case MLINE -> {
                //TODO: To be implemented.
            }
        }
    }

    private static void initFields(final Command command, final ArgsMgr argManager, final CommandLine cmd) {
        switch (command) {
            case HELP -> {}
            case DUPLICATE -> {
                Optional.ofNullable(cmd.getOptionValue("properties"))
                        .ifPresent(___properties -> argManager.setProps(new File(___properties)));

                if (cmd.hasOption("dedupe")) {
                    argManager.setDedupe(true);
                }
            }
            case SORT -> {
                Optional.ofNullable(cmd.getOptionValue("properties"))
                        .ifPresent(___properties -> argManager.setProps(new File(___properties)));
            }
            case MERGE -> {
                //TODO: To be implemented.
            }
            case MLINE -> {
                //TODO: To be implemented.
            }
        }
    }

}
