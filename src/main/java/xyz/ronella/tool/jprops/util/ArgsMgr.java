package xyz.ronella.tool.jprops.util;

import org.apache.commons.cli.*;
import xyz.ronella.tool.jprops.Command;
import xyz.ronella.trivial.handy.OSType;

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
    private File srcProps;
    private File dstProps;
    private boolean dedupe;
    private boolean apply;
    private boolean fix;
    private transient boolean exit;
    private OSType targetOS;
    private ArgsMgr() {}

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
     * The getSrcProps method returns the source properties file.
     * @return The source properties file.
     */
    public File getSrcProps() {
        return srcProps;
    }

    /**
     * The setSrcProps method sets the source properties file.
     * @param srcProps The source properties file.
     */
    public void setSrcProps(File srcProps) {
        this.srcProps = srcProps;
    }

    /**
     * The getDstProps method returns the destination properties file.
     * @return The destination properties file.
     */
    public File getDstProps() {
        return dstProps;
    }

    /**
     * The setDstProps method sets the destination properties file.
     * @param dstProps The destination properties file.
     */
    public void setDstProps(File dstProps) {
        this.dstProps = dstProps;
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

    /**
     * The isFix method returns true if the fix flag is set.
     * @return True if the fix flag is set.
     */
    public boolean isFix() {
        return fix;
    }

    /**
     * The setFix method sets the fix flag.
     * @param fix The fix flag.
     */
    public void setFix(boolean fix) {
        this.fix = fix;
    }

    /**
     * The isApply method returns true if the apply flag is set.
     * @return True if the apply flag is set.
     */
    public boolean isApply() {
        return apply;
    }

    /**
     * The setApply method sets the apply flag.
     * @param apply The apply flag.
     */
    public void setApply(boolean apply) {
        this.apply = apply;
    }

    /**
     * The getTargetOS method returns the target operating system.
     * @return The target operating system.
     */
    public OSType getTargetOS() {
        return targetOS;
    }

    /**
     * The setTargetOS method sets the target operating system.
     * @param targetOS The target operating system.
     */
    public void setTargetOS(OSType targetOS) {
        this.targetOS = targetOS;
    }

    private static void addPropOption(final Options options) {
        final var option = new Option("p", "properties", true
                , "The properties file.");
        option.setRequired(true);
        options.addOption(option);
    }

    private static void addSrcPropOption(final Options options) {
        final var option = new Option("sp", "source", true
                , "The source properties file.");
        option.setRequired(true);
        options.addOption(option);
    }

    private static void addDstPropOption(final Options options) {
        final var option = new Option("dp", "destination", true
                , "The destination properties file.");
        option.setRequired(true);
        options.addOption(option);
    }

    private static void addHelpOption(final Options options) {
        final var option = new Option("h", "help", false
                , "Shows the help information.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addDedupeOption(final Options options) {
        final var option = new Option("dedupe", false
                , "Remove duplication of fields.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addFixOption(final Options options, final String description) {
        final var option = new Option("fix", false, description);
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addApplyOption(final Options options, final String description) {
        final var option = new Option("apply", false, description);
        option.setRequired(false);
        options.addOption(option);
    }

    private static void addTargetOSOption(final Options options) {
        final var option = new Option("os", "target-os", true
                , "The target OS (i.e. aix, linux, mac, solaris, unix, or windows) to which the line ending will be based on. Default is the current OS.");
        option.setRequired(false);
        options.addOption(option);
    }

    private static void helpInfo(final ArgsMgr argMgr, final Options options) {
        final var formatter = new HelpFormatter();

        final var optCommand = Optional.ofNullable(argMgr.getCommand());
        final var commands = """
                        Commands available:
                        duplicate - The command for managing duplicate fields.
                        help - The command for showing this help information.
                        merge - The command for merging properties file.
                        sort - The command for sorting fields fields.
                        bmline - The command for show broken multiline fields.
                        """;
        if (optCommand.isPresent()) {
            final var command = optCommand.get();

            final var appName = String.format("%s %s", AppInfo.INSTANCE.getAppName(), command.name().toLowerCase(Locale.ROOT));
            formatter.printHelp(appName, options);

            if (command == Command.HELP) {
                System.out.println(commands);
            }
        }
        else {
            System.out.println(commands);
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

        commandArg.ifPresent(___command -> argManager.setCommand(Command.of(___command).orElse(Command.INVALID)));

        return newArgs;
    }

    /**
     * The build method creates an instance of the ArgsMgr.
     * @param args The command line arguments.
     * @return The ArgsMgr instance.
     */
    public static ArgsMgr build(final String[] args) {
        final var argManager = new ArgsMgr();
        final var options = new Options();

        final var parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            final var newArgs = prepareArgs(argManager, args);
            initOptions(argManager, options);

            final var command = argManager.getCommand();
            if (Command.INVALID != command) {
                cmd = parser.parse(options, newArgs);
                if (Command.HELP == command) {
                    helpInfo(argManager, options);
                } else {
                    initFields(argManager.getCommand(), argManager, cmd);
                }
            }
        } catch (ParseException exception) {
            System.out.println(exception.getMessage());
            helpInfo(argManager, options);
        } catch (MissingCommandException mce) {
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
            case DUPLICATE -> initDupOptions(options);
            case SORT -> initSortOptions(options);
            case MERGE -> initMergeOptions(options);
            case BMLINE -> initBMLineOptions(options);
        }
    }

    private static void initDupOptions(final Options options) {
        addHelpOption(options);
        addPropOption(options);
        addDedupeOption(options);
        addTargetOSOption(options);
    }

    private static void initSortOptions(final Options options) {
        addHelpOption(options);
        addPropOption(options);
        addApplyOption(options, "Apply the sorting to the properties file.");
        addTargetOSOption(options);
    }

    private static void initMergeOptions(final Options options) {
        addHelpOption(options);
        addSrcPropOption(options);
        addDstPropOption(options);
        addApplyOption(options, "Apply the merging to the properties file.");
        addTargetOSOption(options);
    }

    private static void initBMLineOptions(final Options options) {
        addHelpOption(options);
        addPropOption(options);
        addTargetOSOption(options);
        addFixOption(options, "Fix properties with broken multiline.");
    }

    private static void initPropsField(final ArgsMgr argManager, final CommandLine cmd) {
        Optional.ofNullable(cmd.getOptionValue("properties"))
                .ifPresent(___properties -> argManager.setProps(new File(___properties)));
    }

    private static void initApplyField(final ArgsMgr argManager, final CommandLine cmd) {
        if (cmd.hasOption("apply")) {
            argManager.setApply(true);
        }
    }

    private static void initTargetOSField(final ArgsMgr argManager, final CommandLine cmd) {
        Optional.ofNullable(cmd.getOptionValue("target-os")).flatMap(___os -> Arrays.stream(OSType.values())
                        .filter(___osType -> ___osType.name().equalsIgnoreCase(___os)).findFirst())
                .ifPresentOrElse(argManager::setTargetOS, ()-> argManager.setTargetOS(OSType.identify()));
    }

    private static void initFixField(final ArgsMgr argManager, final CommandLine cmd) {
        if (cmd.hasOption("fix")) {
            argManager.setFix(true);
        }
    }

    private static void initFields(final Command command, final ArgsMgr argManager, final CommandLine cmd) {
        switch (command) {
            case HELP -> {}
            case DUPLICATE -> initDupFields(argManager, cmd);
            case SORT -> initSortFields(argManager, cmd);
            case MERGE -> initMergeFields(argManager, cmd);
            case BMLINE -> initBMLineFields(argManager, cmd);
        }
    }

    private static void initDupFields(final ArgsMgr argManager, final CommandLine cmd) {
        initPropsField(argManager, cmd);
        initTargetOSField(argManager, cmd);

        if (cmd.hasOption("dedupe")) {
            argManager.setDedupe(true);
        }
    }

    private static void initSortFields(final ArgsMgr argManager, final CommandLine cmd) {
        initPropsField(argManager, cmd);
        initApplyField(argManager, cmd);
        initTargetOSField(argManager, cmd);
    }

    private static void initMergeFields(final ArgsMgr argManager, final CommandLine cmd) {
        Optional.ofNullable(cmd.getOptionValue("source"))
                .ifPresent(___properties -> argManager.setSrcProps(new File(___properties)));
        Optional.ofNullable(cmd.getOptionValue("destination"))
                .ifPresent(___properties -> argManager.setDstProps(new File(___properties)));

        initApplyField(argManager, cmd);
        initTargetOSField(argManager, cmd);
    }

    private static void initBMLineFields(final ArgsMgr argManager, final CommandLine cmd) {
        initPropsField(argManager, cmd);
        initFixField(argManager, cmd);
        initTargetOSField(argManager, cmd);
    }

}
