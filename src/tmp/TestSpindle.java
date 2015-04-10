package tmp;


import data.DataList;
import data.DataSeries;

public class TestSpindle {

    private int spindleWidth = 64;
    private int rectangleWidth = 128;
    private int distance = 256;
    int SCALE = 200;
    int constant = 0;

    private DataList dataList = new DataList();

    public TestSpindle() {
        for (int i = 0; i < distance; i++) {
            dataList.add(constant);
        }

        for (int i = 0; i < spindleWidth; i = i + 4) {
            dataList.add(constant);
            dataList.add(getTriangle(i + 1, spindleWidth) + constant);
            dataList.add(constant);
            dataList.add(-getTriangle(i + 3, spindleWidth) + constant);
        }

        for (int i = 0; i < distance; i++) {
            dataList.add(constant);
        }

        for (int i = 0; i < spindleWidth; i = i + 4) {
            dataList.add(getTriangle(i, spindleWidth) + constant);
            dataList.add(getTriangle(i + 1, spindleWidth) + constant);

            dataList.add(-getTriangle(i + 2, spindleWidth) + constant);
            dataList.add(-getTriangle(i + 3, spindleWidth) + constant);
        }

        for (int i = 0; i < distance; i++) {
            dataList.add(constant);
        }

        for (int i = 0; i < rectangleWidth; i++) {
            dataList.add(SCALE + constant);
        }

        for (int i = 0; i < distance; i++) {
            dataList.add(constant);
        }

        dataList.add(SCALE + constant);

        for (int i = 0; i < distance; i++) {
            dataList.add(constant);
        }
    }

    private int getTriangle(int index, int width) {
        int half = width / 2;
        if (index < half) {
            return SCALE * index / half;
        }
        if (index >= half) {
            return SCALE * (width - index - 1) / half;
        }
        return 0;
    }


    public DataSeries getDataStream() {
        return dataList;
    }
}

