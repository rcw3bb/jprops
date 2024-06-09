package xyz.ronella.util.jprops.util;

import xyz.ronella.trivial.handy.Require;
import xyz.ronella.trivial.handy.RequireObject;
import xyz.ronella.util.jprops.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The FileMgr class is the class that manages the file operations.
 *
 * @author Ron Webb
 * @since 1.0.0
 */
final public class FileMgr {
    private FileMgr() {}

    /**
     * The getFilename method returns the filename without the extension.
     * @param file The file.
     * @return The filename without the extension.
     */
    public static String getFilename(final File file) {
        Require.objects(file);

        final var fileName = file.getName();
        final var dotIndex = fileName.lastIndexOf(".");

        final Supplier<String> noDotLogic = () -> Optional.of(dotIndex)
                .filter(___dotIndex -> /* Absence of dot in the filename */ ___dotIndex == -1)
                .map(___dotIndex -> fileName)
                .orElse("NONAME");

        return Optional.of(dotIndex).filter(___dotIndex -> ___dotIndex > 0)
                .map(___dotIndex -> fileName.substring(0, ___dotIndex))
                .orElseGet(noDotLogic);
    }

    /**
     * The getExtension method returns the extension of the file.
     * @param file The file.
     * @return The extension of the file.
     */
    public static String getExtension(final File file) {
        Require.objects(file);

        final var fileName = file.getName();
        final var dotIndex = fileName.lastIndexOf(".");

        final Supplier<String> noDotLogic = () -> Optional.of(dotIndex)
                .filter(___dotIndex -> ___dotIndex == -1)
                .map(___dotIndex -> "NOEXT")
                .orElse(fileName.replace(".", ""));

        return Optional.of(dotIndex).filter(___dotIndex -> ___dotIndex > 0)
                .map(___dotIndex -> fileName.substring(___dotIndex + 1))
                .orElseGet(noDotLogic);
    }

    /**
     * The createTmpFile method creates a temporary file.
     * @param file The file.
     * @return The temporary file.
     * @throws IOException If an I/O error occurs.
     */
    public static File createTmpFile(final File file) throws IOException {
        Require.objects(file);

        final var fileName = getFilename(file);
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
            final var filename = getFilename(file);
            final var extension = getExtension(file);
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
