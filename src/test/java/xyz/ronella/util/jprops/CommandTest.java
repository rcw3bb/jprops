package xyz.ronella.util.jprops;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CommandTest {

    @Test
    public void testValidCommand() {
        final var command = Command.of(Command.DUPLICATE.name());
        assertEquals(Command.DUPLICATE, command.get());
    }

    @Test
    public void testInvalidCommand() {
        final var command = Command.of("Random");
        assertEquals(Command.INVALID, command.get());
    }


}
