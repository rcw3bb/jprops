package xyz.ronella.tool.jprops;

import xyz.ronella.logging.LoggerPlus;
import org.slf4j.LoggerFactory;
import xyz.ronella.tool.jprops.util.AppInfo;
import xyz.ronella.tool.jprops.util.ArgsMgr;
import xyz.ronella.trivial.handy.PathFinder;

/**
 * The Main class is the entry point of the application.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class Main {

    static {
        final var userDir = "user.dir";
        final var confDir = System.getProperty(userDir) + "/conf";
        final var logPath = PathFinder.getBuilder("logback.xml")
                .addSysProps(userDir)
                .addPaths(confDir, "..", "../conf")
                .build();
        final var optLogFile = logPath.getFile();

        if (optLogFile.isPresent()) {
            final var logSysProp = "logback.configurationFile";
            final var logFile = optLogFile.get().getAbsolutePath();
            System.out.printf("%s: %s%n", logSysProp, logFile);
            System.setProperty(logSysProp, logFile);
        }
    }

    private final static LoggerPlus LOGGER_PLUS = new LoggerPlus(LoggerFactory.getLogger(Main.class));

    /**
     * The main method.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try (var mLOG = LOGGER_PLUS.groupLog("main")) {
            final var appInfo = AppInfo.INSTANCE;
            final var header = String.format("%s v%s (%s)"
                    , appInfo.getAppName()
                    , appInfo.getAppVersion()
                    , appInfo.getBuildDate()
            );
            mLOG.info(header);
            mLOG.info("Working Directory: %s%n", System.getProperty("user.dir"));

            final var argsMgr = ArgsMgr.build(args);

            if (argsMgr.shouldExit()) {
                return;
            }

            final var processor = ProcessorFactory.createProcessor(argsMgr);
            processor.process();
        } catch (JPropsException jpe) {
            LOGGER_PLUS.error(jpe.getMessage());
        }
    }

}
