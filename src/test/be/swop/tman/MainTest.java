package be.swop.tman;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {
    private Main main;
    @Before
    public void setUp() throws Exception {
        main = new Main();
    }

    @Test
    public void testMain() throws Exception {
        assertEquals("Hello",main.sayHello());
    }
}