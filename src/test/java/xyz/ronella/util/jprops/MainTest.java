package xyz.ronella.util.jprops;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void helloWorld() {
        var main = new Main();
        assertEquals("Hello World", main.hello("World"));
    }

}
