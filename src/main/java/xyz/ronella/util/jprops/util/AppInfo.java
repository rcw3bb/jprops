package xyz.ronella.util.jprops.util;

import org.slf4j.LoggerFactory;
import xyz.ronella.logging.LoggerPlus;
import xyz.ronella.trivial.handy.PathFinder;

import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

final public class AppInfo {

    private static final LoggerPlus LOGGER_PLUS = new LoggerPlus(LoggerFactory.getLogger(AppInfo.class));
    private static final String CONFIG_FILE = "app-info.properties";
    private transient ResourceBundle prop = null;

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

    public String getAppVersion() {
        return prop.getString("app.version");
    }

    public String getAppName() {
        return prop.getString("app.name");
    }

    public String getBuildDate() {
        return prop.getString("build.date");
    }

}
