package xyz.ronella.util.jprops.support;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.trivial.handy.PathFinder;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * The AppInfo class is a class that holds the application information.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class AppInfo {

    private static final LoggerPlus LOGGER_PLUS = new LoggerPlus(LoggerFactory.getLogger(AppInfo.class));
    private static final String CONFIG_FILE = "app-info.properties";
    private transient ResourceBundle prop = null;

    /**
     * The instance of the AppInfo.
     */
    public static final AppInfo INSTANCE = new AppInfo();

    private AppInfo() {
        try {
            final var appInfoFile = PathFinder.getBuilder(CONFIG_FILE)
                    .setFallbackToClassloader(true)
                    .build();

            final var appInfoIStream = appInfoFile.getInputStream();

            if (appInfoIStream.isPresent()) {
                this.prop = new PropertyResourceBundle(appInfoIStream.get());
            }

        } catch (IOException exp) {
            LOGGER_PLUS.error(LOGGER_PLUS.getStackTraceAsString(exp));
            throw new RuntimeException(exp);
        }
    }

    /**
     * Gets the application version.
     * @return The application version.
     */
    public String getAppVersion() {
        return prop.getString("app.version");
    }

    /**
     * Gets the application name.
     * @return The application name.
     */
    public String getAppName() {
        return prop.getString("app.name");
    }

    /**
     * Gets the application description.
     * @return The application description.
     */
    public String getBuildDate() {
        return prop.getString("build.date");
    }

}
