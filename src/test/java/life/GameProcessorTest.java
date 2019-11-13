package life;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameProcessorTest {

    GameProcessor processor = new GameProcessor(3);

    @Before
    public void setUp() throws Exception {
        byte[] testPane = {0, 1, 0, 0, 0, 1, 1, 0, 0};
/*
      . * . . *
      . 0 1 0 .
      * 0 0 1 .
      . 1 0 0 *
      . . * . .
 */
        processor.setPane(testPane);
    }

    @After
    public void tearDown() throws Exception {
        //processor.write("test.dat");
    }

    @Test
    public void getCell() {
        assertEquals(1, processor.getCell(1, 0));
        assertEquals(0, processor.getCell(1, 2));
    }

    @Test
    public void setCell() {
        assertEquals(0, processor.getCell(1, 1));
        processor.setCell(1, 1, 1);
        processor.nextStep();
        assertEquals(1, processor.getCell(1, 1));
    }

    @Test
    public void getSideCount() {
        assertEquals(3, processor.getSideCount(0, 0));
        assertEquals(3, processor.getSideCount(1, 1));
        assertEquals(3, processor.getSideCount(1, 2));
    }

    @Test
    public void evaluateCell() {
        assertEquals(0, processor.getCell(1, 1));
        processor.evaluateCell(1, 1);
        processor.nextStep();
        assertEquals(1, processor.getCell(1, 1));
    }
}