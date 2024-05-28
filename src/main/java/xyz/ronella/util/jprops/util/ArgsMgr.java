package xyz.ronella.util.jprops.util;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Optional;

public class ArgsMgr {

    private String name;

    private String command;

    private String props;

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

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    private static void addNameOption(final Options options) {
        final var option = new Option("n", "name", true
                , "Name of a person.");
        option.setRequired(true);
        options.addOption(option);
    }

    private static void addPropOption(final Options options) {
        final var option = new Option("p", "prop", true
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
        final var appName = AppInfo.INSTANCE.getAppName();
        formatter.printHelp(appName, options);
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

        commandArg.ifPresent(argManager::setCommand);

        return newArgs;
    }

    public static ArgsMgr build(final String[] args) {
        final var argManager = new ArgsMgr();
        final var options = new Options();

        //addNameOption(options);
        //addGenericParamOption(options);
        addHelpOption(options);

        final var parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            final var newArgs = prepareArgs(argManager, args);

            cmd = parser.parse(options, newArgs);

            if (cmd.hasOption("help")) {
                helpInfo(argManager, options);
            } else {
                argManager.setName(cmd.getOptionValue("name"));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpInfo(argManager, options);
        }

        return argManager;
    }

}
