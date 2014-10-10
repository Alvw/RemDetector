package com.dream;


import com.dream.Data.DataList;
import com.dream.Data.DataStream;

public class TestData {
    int MIN = 2;
    int MAX = 300;
    int pointNumber_int = MAX;
    double pointNumber = MAX+0.9;

    private DataList cosList = new DataList();
    private DataList rectangleList = new DataList();
    private DataList triangleList = new DataList();
    private DataList periodList = new DataList();
    private DataList constantList = new DataList();


    public TestData() {
        while(pointNumber_int >= MIN) {

                for(int i = 0; i< pointNumber_int; i++) {
                cosList.add(Functions.getCos(i, pointNumber_int));
                rectangleList.add(Functions.getRectangle(i, pointNumber_int));
                triangleList.add(Functions.getTriangle(i, pointNumber_int));
                constantList.add(1000);
                if(i==0) {
                    periodList.add(DataStream.WORKSPACE + pointNumber_int);
                } else {
                    periodList.add(DataStream.UNDEFINED);
                }
            }
            pointNumber = pointNumber *0.99;
            pointNumber_int = ((int)(pointNumber));
        }
    }

    public DataStream getCosStream(){
        return cosList;
    }

    public DataStream getTriangleStream(){
        return triangleList;
    }

    public DataStream getRectangleStream(){
        return rectangleList;
    }

    public DataStream getConstantStream(){
        return constantList;
    }

    public DataStream getPeriodStream(){
        return periodList;
    }
}
