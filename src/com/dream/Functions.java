package com.dream;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 01.08.14
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class Functions {
    private int periodPointsNumber;
    public static final int SCALE = 1000;

    public Functions(int periodPointsNumber) {
        this.periodPointsNumber = periodPointsNumber;
    }

    public static int getCos(int index, int periodPointsNumber) {
        double tmp = (double)index / periodPointsNumber;
        double x =  (tmp - (int)tmp) * 2 * Math.PI ;
        return (int) (SCALE * Math.cos(x));
    }

    public static int getRectangle(int index, int periodPointsNumber) {
        int x = Math.abs(index % periodPointsNumber);
        int quarter = periodPointsNumber / 4;

        if((periodPointsNumber%4) != 0) {
            if((x == quarter) || (x == (periodPointsNumber - quarter-1))) {
                return 0;
            }
        }

        if( x < quarter || (x >= (periodPointsNumber - quarter))) {
            return SCALE;
        }
        return (- SCALE);
    }


    public static int getTriangle(int index, int periodPointsNumber) {
        int x = Math.abs(index % periodPointsNumber);
        int dy = (SCALE * 4 * x) / periodPointsNumber;

        if(x < periodPointsNumber / 2) {
            return SCALE - dy;
        }
        return dy - 3 * SCALE;
    }

    public int getTriangle(int index) {
        return getTriangle(index, periodPointsNumber);
    }

    public int getCos(int index) {
        return getCos(index, periodPointsNumber);
    }

    public int getRectangle(int index) {
        return getRectangle(index, periodPointsNumber);
    }
}
