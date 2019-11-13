package life;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class CompareTest {

    SingeThreadGame singeThreadGame;
    MultiThreadGame multiThreadGame1T;
    MultiThreadGame multiThreadGame4T;

    @Before
    public void setUp() throws Exception {

        int size = 1000;
        byte[] pane = new byte[size * size];

        Random rnd = new Random();
        int fillPercent = 30; // сколько % клеток заполнять

        for (int i = 0; i < pane.length; i++) {
            pane[i] = (byte) ((rnd.nextInt(100) < fillPercent) ? 1 : 0);
        }

        singeThreadGame = new SingeThreadGame(size);
        singeThreadGame.setPane(pane);

        multiThreadGame1T = new MultiThreadGame(size, 1);
        multiThreadGame1T.setPane(pane);

        multiThreadGame4T = new MultiThreadGame(size, 4);
        multiThreadGame4T.setPane(pane);

        singeThreadGame.write("start.dat");

    }

    @After
    public void tearDown() throws Exception {
        multiThreadGame1T.write("multiThreadGame.dat");
        singeThreadGame.write("singeThreadGame.dat");
    }

    @Test
    public void compareTime() {
        int steps = 1000;
        long startTime = 0;

        startTime = System.nanoTime();
        multiThreadGame1T.process(steps);
        long multiThreadGame1 = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        multiThreadGame4T.process(steps);
        long multiThreadGame4 = System.nanoTime() - startTime;

        startTime = System.nanoTime();
        singeThreadGame.process(steps);
        long singleTreadTime = System.nanoTime() - startTime;


        System.out.println("singleTreadTime   =" + singleTreadTime);
        System.out.println("multiThreadGame 1T=" + multiThreadGame1);
        System.out.println("multiThreadGame 4T=" + multiThreadGame4);

    }

}
