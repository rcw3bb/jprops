package xyz.ronella.tool.jprops.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.Command;

import java.io.File;
import java.io.IOException;

public class FileMgrTest {

    @Test
    public void createTmpFile() throws IOException {
        final var testFile = new File("src\\test\\resources\\test-file.txt");
        final var tmpFile = FileMgr.createTmpFile(testFile);
        assertTrue(tmpFile.exists());
        System.out.println("Tempfile: " + tmpFile.getAbsolutePath());
        tmpFile.delete();
        assertFalse(tmpFile.exists());
    }

    @Test
    public void filenameWithExt() {
        final var filename = FileMgr.getFilename(new File("Test.ext"));
        assertEquals("Test", filename);
    }

    @Test
    public void filenameWithoutExt() {
        final var filename = FileMgr.getFilename(new File("Test"));
        assertEquals("Test", filename);
    }

    @Test
    public void extOnly() {
        final var filename = FileMgr.getFilename(new File(".ext"));
        assertEquals("NONAME", filename);
    }

    @Test
    public void extensionWithExt() {
        final var filename = FileMgr.getExtension(new File("Test.ext"));
        assertEquals("ext", filename);
    }

    @Test
    public void extensionWithoutExt() {
        final var filename = FileMgr.getExtension(new File("Test"));
        assertEquals("NOEXT", filename);
    }

    @Test
    public void extensionOnly() {
        final var filename = FileMgr.getExtension(new File(".ext"));
        assertEquals("ext", filename);
    }

    @Test
    public void moveToBackupFileExist() throws IOException {
        final var file = new File("src\\test\\resources\\file-backup.txt");
        file.createNewFile();

        final var backup = FileMgr.moveToBackup(Command.DUPLICATE, file).get();
        assertTrue(backup.exists());
        System.out.println("Backup: " + backup.getAbsolutePath());
        backup.delete();
        assertFalse(backup.exists());
    }

    @Test
    public void moveToBackupNoFile() throws IOException {
        final var file = new File("src\\test\\resources\\dummy.txt");
        final var backup = FileMgr.moveToBackup(Command.DUPLICATE, file);
        assertTrue(backup.isEmpty());
    }

    @Test
    public void safeMoveAFile() throws IOException {
        final var srcFile = new File("src\\test\\resources\\safe-move.txt");
        srcFile.createNewFile();

        final var destFile = new File("src\\test\\resources\\safe-dest.txt");
        final var backupFile = FileMgr.safeMove(Command.DUPLICATE, srcFile, destFile).get();

        assertTrue(backupFile.exists());
        assertFalse(srcFile.exists());
        assertTrue(destFile.exists());

        backupFile.delete();
        assertFalse(backupFile.exists());

        srcFile.delete();
        assertFalse(srcFile.exists());
    }

}
