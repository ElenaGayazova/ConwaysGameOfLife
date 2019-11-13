package life;

public class SingeThreadGame extends GameProcessor {

    public SingeThreadGame() {
    }

    public SingeThreadGame(int size) {
        super(size);
    }

    /**
     * выполняет step шагов
     *
     * @param steps
     */
    public void process(int steps) {
        for (int i = 0; i < steps; i++) {
            int size = getSize();
            for (int col = 0; col < size; col++) {
                for (int row = 0; row < size; row++) {
                    // пересчитываем каждую ячейку поочередно
                    evaluateCell(col, row);
                }
            }
            // начинаем следующий шаг
            nextStep();
        }
    }

    public static void main(String[] args) throws Exception {
        String fileInput, fileOutput;
        int steps;
        if (args.length < 3) {
            System.out.println("параметры: <input file> <output file> <steps>");
            fileInput = "test.dat";
            fileOutput = "result.dat";
            steps = 2;
        } else {
            steps = Integer.parseInt(args[2]);
            fileInput = args[0];
            fileOutput = args[1];
        }
        SingeThreadGame game = new SingeThreadGame();
        game.load(fileInput);
        game.process(steps);
        game.write(fileOutput);
    }
}
