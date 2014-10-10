package com.dream.Filters;

import com.dream.Data.DataStream;

public class FilterOffset_2 extends Filter {
    private static int offsetLevel = 0;
    private static int offsetLevel_1 = offsetLevel;
    private static int timerMin = 150;
    private int noiseLevelMin = 100;


    public FilterOffset_2(DataStream inputData) {
        super(inputData);
    }

    protected int getData(int index) {
        if (index < timerMin + 1) {
            offsetLevel   = inputData.get(index);
            offsetLevel_1 = offsetLevel;
            return 0;
        }

        int n = 0;
        int sum1 = 0;
        int y;
        int y_1;
        int dyAbs;
        for(int i = 0; i < timerMin; i++){
            y   = inputData.get(index - i);
            y_1 = inputData.get(index - i - 1);
            dyAbs = Math.abs(y - y_1);

            if(dyAbs < noiseLevelMin){
              sum1 += y;
              n++;
        }
        if(n > 0){
            offsetLevel_1 = sum1/(n);
        }

    }
        return inputData.get(index) - offsetLevel_1;
    }
}
