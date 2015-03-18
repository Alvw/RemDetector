package functions;

import data.DataSet;

/**
 * Определяем разницу между макс и мин значениями на заданном колличестве точек
 */
public class Rising extends Function {
    int step;

    public Rising(DataSet inputData) {
        this(inputData, 2);
    }

    public Rising(DataSet inputData, int step) {
        super(inputData);
        this.step = step;
    }

    @Override
    public int get(int index) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i <= Math.min(step, index); i++) {
            max = Math.max(max, inputData.get(index - i));
            min = Math.min(min, inputData.get(index - i));
        }
        return Math.abs(max - min);
    }

}
