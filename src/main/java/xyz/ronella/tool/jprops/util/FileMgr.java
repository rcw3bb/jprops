package xyz.ronella.tool.jprops.util;

import xyz.ronella.tool.jprops.Command;
import xyz.ronella.trivial.decorator.FileNomen;
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

        final var fileName = new FileNomen(file).getFilename().orElse("NONAME");
        final var suffix = ".properties";

        return File.createTempFile(fileName, suffix);
    }

    /**
     * The getBackupDir method returns the backup directory.
     * @return The backup directory.
     */
    private static File getBackupDir() {
        final var appData = System.getenv("LOCALAPPDATA");
        final var backupDir = new File(String.format("%s/%s/backup", appData, AppInfo.INSTANCE.getAppName()));

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        return backupDir;
    }

    /**
     * The moveToBackup method moves the file to the backup directory.
     * @param command The command.
     * @param file The file.
     * @return The backup file.
     * @throws IOException If an I/O error occurs.
     */
    protected static Optional<File> moveToBackup(final Command command, final File file) throws IOException {
        Require.objects(file);

        if (file.exists()) {
            final var backupDir = getBackupDir();
            final var fileNomen = new FileNomen(file);
            final var filename = fileNomen.getFilename().orElse("NONAME");
            final var extension = fileNomen.getExtension().orElse("properties");
            final var timeElement = String.valueOf(System.currentTimeMillis());
            final var backupFile = new File(backupDir, String.format("%s-%s-%s.%s", filename, command.getCode(),
                    timeElement, extension));
            Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
            return Optional.of(backupFile);
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

        Files.move(src.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);

        return backup;
    }

}