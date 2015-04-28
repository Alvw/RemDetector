package graph.painters;


import data.Scaling;
import data.ScalingImpl;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class XAxisPainter  {

    private XAxisBasePainter painter = new XAxisBasePainter();
    private boolean  isGridPaint = true;
    private boolean  isAxisPaint = true;
    private boolean isValuesPaint = true;

    private HashMap<Integer, String> valueStamps;
    private ArrayList<Integer> valueIndexes;
    private ArrayList<Integer> gridIndexes;
    private ArrayList<Integer> interGridIndexes;


    public void isGridPaint(boolean isGridPaint) {
        this.isGridPaint = isGridPaint;
    }


    public void isAxisPaint(boolean isAxisPaint) {
        this.isAxisPaint = isAxisPaint;
    }

    public void isValuesPaint(boolean isTimeStampsPaint) {
        this.isValuesPaint = isTimeStampsPaint;
    }


    private void prepareSimpleAxis_(Graphics g, int startIndex, double samplingRate) {
        int width = g.getClipBounds().width;

        valueStamps = new HashMap<Integer, String>();
        valueIndexes = new ArrayList<Integer>();
        gridIndexes = new ArrayList<Integer>();
        interGridIndexes = new ArrayList<Integer>();

        for(int i = 0; i <= width; i++) {
            int index = startIndex + i;
            if(index % 50 == 0) {
                valueIndexes.add(index);
                valueStamps.put(index, String.valueOf(index));
            }
            if(index % 10 == 0) {
                gridIndexes.add(index);
            }
            if(index % 5 == 0) {
                interGridIndexes.add(index);
            }
        }
    }

    private void prepareSimpleAxis(Graphics g, int startIndex, Scaling scaling) {
        double pointDistance = 1;
        double start = 0;
        if(scaling != null) {
            pointDistance = scaling.getSamplingInterval();
            start = scaling.getStart();
        }
        int width = g.getClipBounds().width;

        int minPointsNumber = 60;
        double stepMin = minPointsNumber * pointDistance;

        int exponent = (int) Math.log10(stepMin);
        if(Math.log10(stepMin) < 0) {
            exponent = exponent - 1;
        }


        double stepMinNormalized = stepMin / Math.pow(10, exponent);
        int firstFigure = (int) (stepMin / Math.pow(10, exponent));

        int[] possibleSteps = {2, 5, 10};
        double valueStampsStep = 0;
        int j=0;
        while(valueStampsStep == 0 && j < possibleSteps.length) {
            if (stepMinNormalized == possibleSteps[j] || firstFigure < possibleSteps[j]) {
                valueStampsStep = possibleSteps[j];
            }
            if(valueStampsStep == 10){
                valueStampsStep = 1;
                exponent = exponent + 1;
            }
            j++;
        }

        valueStampsStep = valueStampsStep * Math.pow(10, exponent);
        String stringFormat = "%.0f";
        if(exponent < 0) {
            stringFormat = "%."+Math.abs(exponent)+"f";
        }

        valueStamps = new HashMap<Integer, String>();
        valueIndexes = new ArrayList<Integer>();
        gridIndexes = new ArrayList<Integer>();
        interGridIndexes = new ArrayList<Integer>();

        for(int index = startIndex; index <= startIndex + width; index++) {
            double indexValue = start + index * pointDistance;
            double nextValue = start + (index + 1) * pointDistance;
            long n_index = (long)(indexValue / valueStampsStep);
            long n_next = (long)(nextValue / valueStampsStep);
            if(n_index != n_next) {
                valueIndexes.add(index);
                valueStamps.put(index, String.format(stringFormat, n_next * valueStampsStep));
            }

            double gridStep = valueStampsStep / 5;
            n_index = (long)(indexValue / gridStep);
            n_next = (long)(nextValue / gridStep);
            if(n_index != n_next) {
                gridIndexes.add(index);
            }

            double interGridStep = gridStep / 2;
            n_index = (long)(indexValue / interGridStep);
            n_next = (long)(nextValue / interGridStep);
            if(n_index != n_next) {
                interGridIndexes.add(index);
            }
        }
    }


    private void prepareTimeAxis(Graphics g, int startIndex,  Scaling scaling) {
        int width = g.getClipBounds().width;
        double samplingInterval = scaling.getSamplingInterval();
        long startTime = (long)scaling.getStart();
        valueStamps = new HashMap<Integer, String>();
        valueIndexes = new ArrayList<Integer>();
        gridIndexes = new ArrayList<Integer>();
        interGridIndexes = new ArrayList<Integer>();
        int MSECOND = 1; //milliseconds
        int MSECONDS_2 = 2;
        int MSECONDS_5 = 5;
        int MSECONDS_10 = 10;
        int MSECONDS_20 = 20;
        int MSECONDS_50 = 50;
        int MSECONDS_100 = 100;
        int MSECONDS_200 = 200;
        int MSECONDS_500 = 500;
        int SECOND = 1000;
        int SECONDS_2 = 2 * SECOND;
        int SECONDS_5 = 5 * SECOND;
        int SECONDS_10 = 10 * SECOND;
        int SECONDS_30 = 30 * SECOND;
        int MINUTE = 60 * SECOND;
        int MINUTES_2 = 2 * MINUTE;
        int MINUTES_5 = 5 * MINUTE;
        int MINUTES_10 = 10 * MINUTE;
        int MINUTES_30 = 30 * MINUTE;
        int HOUR = 60 * MINUTE;
        int HOURS_2 = 2 * HOUR;
        int HOURS_5 = 5 * HOUR;

        int[] TIME_INTERVALS = {MSECOND, MSECONDS_2, MSECONDS_5, MSECONDS_10, MSECONDS_20, MSECONDS_50,
                MSECONDS_100, MSECONDS_200, MSECONDS_500, SECOND, SECONDS_2, SECONDS_5, SECONDS_10,
                SECONDS_30, MINUTE, MINUTES_2, MINUTES_5, MINUTES_10, MINUTES_30, HOUR, HOURS_2, HOURS_5};

        int NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN = 80;
        // as the ratio between nearby time intervals always <= 3
        int NUMBER_OF_POINTS_PER_TIME_INTERVAL_MAX = NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN * 3;

        int GRIDS_DIVIDER_MAX = 5;
        int GRIDS_DIVIDER_MIN = 3;

        String DATE_FORMAT_FULL = "HH:mm:ss-SSS";
        String DATE_FORMAT_SEC = "HH:mm:ss";
        String DATE_FORMAT_MIN = "HH:mm";
        String DATE_FORMAT_HOUR = "HH";

        int timeStampsInterval = MSECOND;
        int gridsDivider = GRIDS_DIVIDER_MAX;
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FULL);

        int timeIntervalMin = (int) (NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN * samplingInterval * 1000);
        int timeIntervalMax = (int) (NUMBER_OF_POINTS_PER_TIME_INTERVAL_MAX * samplingInterval * 1000);

        for (int i = TIME_INTERVALS.length - 1; i >= 0; i--) {
            int interval = TIME_INTERVALS[i];
            if (timeIntervalMin <= interval && interval <= timeIntervalMax) {
                timeStampsInterval = interval;
            }
        }

        if (timeStampsInterval >= SECOND) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_SEC);
        }
        if (timeStampsInterval >= MINUTE) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_MIN);
        }
        if (timeStampsInterval >= HOUR) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR);
        }
        if (timeStampsInterval == MINUTE || timeStampsInterval == HOUR || timeStampsInterval == SECONDS_30
                || timeStampsInterval == MINUTES_30) {
            gridsDivider = GRIDS_DIVIDER_MIN;
        }

        for (int index = startIndex; index <= startIndex + width; index++) {
            long indexTime = startTime + (long) (index * samplingInterval * 1000);
            long nextTime = startTime + (long) ((index + 1) * samplingInterval * 1000 );
            long n_index = indexTime / timeStampsInterval;
            long n_next = nextTime / timeStampsInterval;
            if (n_next != n_index) { // means that time mark is located between points i and i+1
                String timeStamp = dateFormat.format(new Date(n_next * timeStampsInterval));
                valueIndexes.add(index);
                valueStamps.put(index, timeStamp);
            }

            double gridsInterval = timeStampsInterval / gridsDivider;
            n_index = (long)(indexTime / gridsInterval);
            n_next = (long)(nextTime / gridsInterval);
            if (n_next != n_index) {
                gridIndexes.add(index);
            }

            double interGridsInterval = gridsInterval / 2;
            n_index = (long)(indexTime / interGridsInterval);
            n_next = (long)(nextTime / interGridsInterval);
            if (n_next != n_index) {
                interGridIndexes.add(index);
            }
        }
    }


    private void paint(Graphics g, int startIndex) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        if(isGridPaint) {
            painter.paintGrid(g, startIndex, gridIndexes);
            painter.paintStrongGrid(g, startIndex, valueIndexes);
        }
        if(isAxisPaint) {
            painter.paintAxis(g, startIndex, valueIndexes, gridIndexes, interGridIndexes);
        }
        if(isValuesPaint) {
            painter.paintValues(g, startIndex, valueStamps);
        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }


    public void paint(Graphics g, int startIndex, Scaling scaling) {
        if(scaling == null) {
            scaling = new ScalingImpl();
        }
        if(scaling.isTimeSeries()) {
            prepareTimeAxis(g, startIndex, scaling);
        }
        else {
            prepareSimpleAxis(g, startIndex, scaling);
        }
        paint(g, startIndex);
    }
}
