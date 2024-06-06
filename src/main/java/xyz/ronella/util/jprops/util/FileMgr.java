package xyz.ronella.util.jprops.util;

import xyz.ronella.trivial.handy.Require;
import xyz.ronella.trivial.handy.RequireObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

final public class FileMgr {
    private FileMgr() {}

    public static String getFilename(final File file) {
        Require.objects(file);

        final var fileName = file.getName();
        final var dotIndex = fileName.lastIndexOf(".");

        if (/*Has filename and extension*/ dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        else if (/*Has filename only*/ dotIndex == -1) {
            return fileName;
        }

        return /*Has extension only*/ "NONAME";
    }

    public static String getExtension(final File file) {
        Require.objects(file);

        final var fileName = file.getName();
        final var dotIndex = fileName.lastIndexOf(".");

        if (/*Has filename and extension*/ dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        else if (/*Has filename only*/ dotIndex == -1) {
            return "NOEXT";
        }

        return /*Has extension only*/ fileName.replace(".","");
    }

    public static File createTmpFile(final File file) throws IOException {
        Require.objects(file);

        final var fileName = getFilename(file);
        final var suffix = ".properties";

        return File.createTempFile(fileName, suffix);
    }

    private static File getBackupDir() {
        final var appData = System.getenv("LOCALAPPDATA");
        final var backupDir = new File(String.format("%s/%s/backup", appData, AppInfo.INSTANCE.getAppName()));

        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        return backupDir;
    }

    protected static Optional<File> moveToBackup(final File file) throws IOException {
        Require.objects(file);

        if (file.exists()) {
            final var backupDir = getBackupDir();
            final var filename = getFilename(file);
            final var extension = getExtension(file);
            final var timeElement = String.valueOf(System.currentTimeMillis());
            final var backupFile = new File(backupDir, String.format("%s-%s.%s", filename, timeElement, extension));
            Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
            return Optional.of(backupFile);
        }

        return Optional.empty();
    }

    public static Optional<File> safeMove(final File src, final File dest) throws IOException {
        Require.objects(
                new RequireObject(src, "Source is required"),
                new RequireObject(dest, "Destination is required")
        );

        final var backup = moveToBackup(dest);

        Files.move(src.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);

        return backup;
    }

}
