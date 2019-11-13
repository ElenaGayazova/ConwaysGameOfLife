package life;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadGame extends GameProcessor {


    CyclicBarrier barrier; // используем чтобы size*size потоков пересчитывающих ячейки ожидали в конце каждого шага
    CountDownLatch locker;  // используем чтобы главный поток ждал завершения пересчета всех шагов
    boolean done = false;
    int threadsCount = 1;

    /**
     * обновляет данные для следующего шага
     */
    class NextStep implements Runnable {
        int steps;

        public NextStep(int steps) {
            this.steps = steps;
        }

        @Override
        public void run() {
//            System.out.println("NextStep: осталось "+(steps-1));
            nextStep();
            steps--;
            if (steps <= 0) {
                done = true;        // устанавливаем признак завершения работы
                locker.countDown(); // освобождаем от ожидания главный поток
            }
        }
    }

    /**
     * рассчитывает заданную клетку
     */
    class Evaluator implements Runnable {
        int col, row, count;

        /**
         * вычисляем count клеток начиная с col,row
         *
         * @param col
         * @param row
         * @param count
         */
        public Evaluator(int col, int row, int count) {
            this.col = col;
            this.row = row;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                while (!done) {
//                    System.out.println("Evaluator: "+col+","+row);
                    int _col = col;
                    int _row = row;
                    int size = getSize();
                    for (int i = 0; i < count; i++) {
                        evaluateCell(_col, _row);
                        // вычисляем координаты следующей клетки
                        if (_col == size - 1) { // последняя колонка
                            _col = 0;
                            _row++;
                            if (_row >= size) break; // достигнут конец поля - заканчиваем работу
                        } else {
                            _col++;
                        }
                    }
                    barrier.await(); // ждем когда все потоки посчитают свои клетки и NextStep начнет следующий шаг
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }
    }

    public MultiThreadGame() {
    }

    public MultiThreadGame(int size, int count) {
        super(size);
        threadsCount = count;
    }


    /**
     * выполняет step шагов
     *
     * @param steps
     */
    public void process(int steps) {
        int size = getSize();
        int len = size * size;
        locker = new CountDownLatch(1);
        // после того как size*size потоков пересчитают свои клетки выполняется NextStep
        barrier = new CyclicBarrier(threadsCount, new NextStep(steps));
        // запускаем поток для каждой клетки
        int portion = ((len - 1) / threadsCount) + 1; //
        int pos = 0;
        for (int i = 0; i < threadsCount; i++) {
            int _col = pos % size;
            int _row = pos / size;
            int _cnt = (pos + portion >= len) ? (len - pos) : portion;
            //System.out.println(""+_col+","+_row+" "+_cnt);
            new Thread(new Evaluator(_col, _row, _cnt)).start();
            pos += portion;
        }
        try {
//            System.out.println("process: wait");
            // ждем завершения работы
            locker.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("process: done");
    }

    public static void main(String[] args) throws Exception {
        String fileInput, fileOutput;
        int steps, threads;
        if (args.length < 4) {
            System.out.println("параметры: <input file> <output file> <steps> <threads>");
            fileInput = "test.dat";
            fileOutput = "result.dat";
            steps = 100;
            threads = 20;
        } else {
            steps = Integer.parseInt(args[2]);
            threads = Integer.parseInt(args[3]);
            fileInput = args[0];
            fileOutput = args[1];
        }
        MultiThreadGame game = new MultiThreadGame();
        game.threadsCount = threads;
        game.load(fileInput);
        game.process(steps);
        game.write(fileOutput);
    }

}
