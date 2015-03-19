package tmp;


import data.DataList;
import data.DataSet;

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
                   // periodList.add(DataSet.WORKSPACE + pointNumber_int);
                } else {
                   // periodList.add(DataSet.FALSE);
                }
            }
            pointNumber = pointNumber *0.99;
            pointNumber_int = ((int)(pointNumber));
        }
    }

    public DataSet getCosStream(){
        return cosList;
    }

    public DataSet getTriangleStream(){
        return triangleList;
    }

    public DataSet getRectangleStream(){
        return rectangleList;
    }

    public DataSet getConstantStream(){
        return constantList;
    }

    public DataSet getPeriodStream(){
        return periodList;
    }
}
