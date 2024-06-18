package xyz.ronella.tool.jprops.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import xyz.ronella.tool.jprops.util.MissingCommandException;
import xyz.ronella.tool.jprops.util.ArgsMgr;

import java.nio.file.Paths;

public class BrokenMLineProcessorTest {
    @Test
    public void bmLineProcessor() throws MissingCommandException {
        final var propFile = Paths.get(".", "src", "test", "resources", "multiline.properties").toFile();
        final var processor = new BrokenMLineProcessor(ArgsMgr.build(new String[] {"bmline", "-p", propFile.getAbsolutePath()}));
        assertDoesNotThrow(processor::process);
    }


}
