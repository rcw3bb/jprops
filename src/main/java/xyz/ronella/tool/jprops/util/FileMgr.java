package xyz.ronella.tool.jprops.util;

import xyz.ronella.tool.jprops.Command;
import xyz.ronella.trivial.decorator.FileNomen;
import xyz.ronella.trivial.handy.OSType;
import xyz.ronella.trivial.handy.Require;
import xyz.ronella.trivial.handy.RequireObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * The FileMgr class is the class that manages the file operations.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class FileMgr {
    private FileMgr() {}

    /**
     * The createTmpFile method creates a temporary file.
     * @param file The file.
     * @return The temporary file.
     * @throws IOException If an I/O error occurs.
     */
    public static File createTmpFile(final File file) throws IOException {
        Require.objects(file);

        var fileName = new FileNomen(file).getFilename().orElse("NONAME");
        final var suffix = ".properties";

        final var minNumChars = 3;
        if (fileName.length() < minNumChars) {
            fileName = String.format("%" + minNumChars + "s", fileName).replace(' ', '_');
        }

        return File.createTempFile(fileName, suffix);
    }

    /**
     * The getBackupDir method returns the backup directory.
     * @return The backup directory.
     */
    private static Optional<File> getBackupDir() {
        final var appData = OSType.identify().getAppDataDir();
        return appData.map(___appData -> {
            final var backupDir = new File(String.format("%s/%s/backup", ___appData, AppInfo.INSTANCE.getAppName()));

            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            return backupDir;
        });
    }

    /**
     * The getConfDir method returns the configuration directory.
     * @return The configuration directory.
     *
     * @since 1.2.0
     */
    public static Optional<File> getConfDir() {
        final var appData = Optional.of(System.getProperty("user.dir"));
        return appData.map(___appData -> {
            final var confDir = new File(String.format("%s/conf", ___appData));

            if (!confDir.exists()) {
                confDir.mkdirs();
            }

            return confDir;
        });
    }

    /**
     * The moveToBackup method moves the file to the backup directory.
     * @param command The command.
     * @param file The file.
     * @return The backup file.
     * @throws IOException If an I/O error occurs.
     */
     static Optional<File> moveToBackup(final Command command, final File file) throws IOException {
        Require.objects(file);

        if (file.exists()) {
            final var backupDir = getBackupDir();
            if (backupDir.isPresent()) {
                final var fileNomen = new FileNomen(file);
                final var filename = fileNomen.getFilename().orElse("NONAME");
                final var extension = fileNomen.getExtension().orElse("properties");
                final var timeElement = String.valueOf(System.currentTimeMillis());
                final var backupFile = new File(backupDir.get(), String.format("%s-%s-%s.%s", filename, command.getCode(),
                        timeElement, extension));
                Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
                return Optional.of(backupFile);
            }
        }

        return Optional.empty();
    }

    /**
     * The safeMove method moves the source file to the destination file.
     * @param command The command.
     * @param src The source file.
     * @param dest The destination file.
     * @return The backup file.
     * @throws IOException If an I/O error occurs.
     */
    public static Optional<File> safeMove(final Command command, final File src, final File dest) throws IOException {
        Require.objects(
                new RequireObject(src, "Source is required"),
                new RequireObject(dest, "Destination is required")
        );

        final var backup = moveToBackup(command, dest);

        if (backup.isPresent()) {
            Files.move(src.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);
        }
        else {
            throw new IOException("Failed to create backup file");
        }

        return backup;
    }

}
