package xyz.ronella.util.jprops.util;

import org.apache.commons.cli.*;
import xyz.ronella.util.jprops.Command;
import xyz.ronella.util.jprops.impl.MissingCommandException;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class ArgsMgr {

    private String name;

    private Command command;

    private File props;

    private boolean dedupe;

    public boolean shouldExit() {
        return exit;
    }

    public void setShouldExit(boolean exit) {
        this.exit = exit;
    }

    private transient boolean exit;

    private ArgsMgr() {
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public File getProps() {
        return props;
    }

    public void setProps(File props) {
        this.props = props;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean isDedupe() {
        return dedupe;
    }

    public void setDedupe(boolean dedupe) {
        this.dedupe = dedupe;
    }

    private static void addNameOption(final Options options) {
        final var option = new Option("n", "name", true
                , "Name of a person.");
        option.setRequired(true);
        options.addOption(option);
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

        final var commandArg = Optional.of(args)
                .filter(___args -> Arrays.stream(___args).findAny().isPresent())
                .map(___args -> /*Extract the first argument*/ ___args[0])
                .filter(___arg -> ___arg.trim().matches("^\\s*[a-zA-Z_].*"));

        final var newArgs = Optional.of(args)
                .filter(___args -> ___args.length > 1)
                .map(Arrays::asList)
                .map(___args -> ___args.subList(1, ___args.size()))
                .map(___args -> ___args.toArray(new String[] {}))
                .orElse(commandArg.isPresent() ? new String[] {} : args);

        commandArg.ifPresent(___command -> argManager.setCommand(Command.of(___command).get()));

        return newArgs;
    }

    public static ArgsMgr build(final String[] args) throws MissingCommandException {
        final var argManager = new ArgsMgr();
        final var options = new Options();

        //addNameOption(options);
        //addGenericParamOption(options);
        //addHelpOption(options);

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

    protected static void initOptions(final ArgsMgr argManager, final Options options) throws MissingCommandException {
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
        }
    }

    protected static void initFields(final Command command, final ArgsMgr argManager, final CommandLine cmd) {
        switch (command) {
            case HELP -> {}
            case DUPLICATE -> {
                Optional.ofNullable(cmd.getOptionValue("properties"))
                        .ifPresent(___properties -> argManager.setProps(new File(___properties)));

                if (cmd.hasOption("dedupe")) {
                    argManager.setDedupe(true);
                }
            }
            case SORT -> {}
            case MERGE -> {}
            default -> {}
        }
    }

}
