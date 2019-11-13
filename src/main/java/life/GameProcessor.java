package life;

import java.io.*;
import java.util.Arrays;

public class GameProcessor {
    private byte[] pane; // квадратное поле size*size
    private byte[] back; // квадратное поле size*size для расчета следующего шага
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        pane = new byte[size * size];
        Arrays.fill(pane, (byte) 0);
        back = new byte[size * size];
        Arrays.fill(back, (byte) 0);
    }

    public GameProcessor() {
        setSize(0);
    }

    public GameProcessor(int size) {
        setSize(size);
    }

    /**
     * копируем массив
     *
     * @param src
     * @param dst
     */
    private void copyArray(byte[] src, byte[] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i];
        }
    }

    /**
     * задаем конфигурацию поля
     *
     * @param data
     */
    public void setPane(byte[] data) {
        if (data == null || data.length != pane.length) throw new IllegalArgumentException("incorrect size");
        copyArray(data, pane);
        copyArray(data, back);
    }

    /**
     * переходим на следующий шаг
     */
    public void nextStep() {
        copyArray(back, pane);
    }

    /**
     * вычисляем позицию в массиве по координатам клетки
     *
     * @param col
     * @param row
     * @return
     */
    private int calcPosition(int col, int row) {
        return col + row * size;
    }

    /**
     * возвращает 0 если клетка пуста или 1 если клетка занята
     *
     * @param col
     * @param row
     * @return
     */
    public int getCell(int col, int row) {
        return pane[calcPosition(col, row)];
    }

    /**
     * устанавливаем значение клетки
     *
     * @param col
     * @param row
     * @param state
     */
    public void setCell(int col, int row, int state) {
        back[calcPosition(col, row)] = (byte) state;
    }

    /**
     * вычисляем сколько клеток занято вокруг данной клетки
     *
     * @param col
     * @param row
     * @return
     */
    public int getSideCount(int col, int row) {
        int count = 0;
        int leftCol = (col == 0) ? (size - 1) : col - 1;
        int rightCol = (col == size - 1) ? 0 : col + 1;
        int upperRow = (row == 0) ? (size - 1) : row - 1;
        int bottomRow = (row == size - 1) ? 0 : row + 1;

        count = getCell(leftCol, upperRow);
        count += getCell(col, upperRow);
        count += getCell(rightCol, upperRow);
        count += getCell(leftCol, row);

        count += getCell(rightCol, row);
        count += getCell(leftCol, bottomRow);
        count += getCell(col, bottomRow);
        count += getCell(rightCol, bottomRow);

        return count;
    }

    /**
     * рассчитываем значение клетки на следующем шаге
     *
     * @param col
     * @param row
     */
    public void evaluateCell(int col, int row) {
        int cell = getCell(col, row);
        int around = getSideCount(col, row);

        // если клетка пуста и вокруг ровно 3 соседей, то клетка заселяется
        // если клетка занята и вокруг больше 3 или меньше 2 соседей, клетка освобождается
        // меняем только изменившиеся клетки
        if (cell == 0) {
            if (around == 3) setCell(col, row, 1);
        } else {
            if (around < 2 || around > 3) setCell(col, row, 0);
        }
    }

    /**
     * Загружаем конфигурацию из файла
     *
     * @param fname
     * @throws IOException
     */
    public void load(String fname) throws IOException {
        File fIn = new File(fname);
        if (!fIn.exists() || !fIn.isFile()) throw new IOException("File not found: " + fname);

        FileReader fr = new FileReader(fIn);
        BufferedReader br = new BufferedReader(fr);
        // на первой строке размерность поля
        String sSize = br.readLine().trim();
        int iSize = Integer.parseInt(sSize); // при ошибке будет NumberFormatException
        int len = iSize * iSize;
        byte[] buf = new byte[len];
        // заполняем весь массив 0
        Arrays.fill(buf, (byte) 0);

        for (int row = 0; row < iSize; row++) {
            String line = br.readLine();
            if (line == null) break; // конец файла
            byte[] line_buf = line.getBytes();
            for (int col = 0; col < iSize && col < line_buf.length; col++) {
                // занятые клетки можно отмечать '1' или '*'
                if (line_buf[col] == '1' || line_buf[col] == '*') buf[row * iSize + col] = 1;
            }
        }

        setSize(iSize);
        setPane(buf);
    }

    /**
     * записываем конфигурацию в файл
     *
     * @param fname
     * @throws IOException
     */
    public void write(String fname) throws IOException {
        File fIn = new File(fname);
        FileWriter fw = new FileWriter(fIn);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(String.valueOf(size));
        for (int row = 0; row < size; row++) {
            bw.write('\n');
            for (int col = 0; col < size; col++) {
                bw.write((pane[row * size + col] == 1) ? '*' : ' ');
            }
            bw.write('|');
        }
        bw.flush();
        bw.close();
        fw.close();
    }

}
