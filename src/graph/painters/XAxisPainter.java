package graph.painters;


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

    private boolean isTimeAxis = true;


    public XAxisPainter(boolean isTimeAxis) {
        this.isTimeAxis = isTimeAxis;
    }

    public void isGridPaint(boolean isGridPaint) {
        this.isGridPaint = isGridPaint;
    }


    public void isAxisPaint(boolean isAxisPaint) {
        this.isAxisPaint = isAxisPaint;
    }

    public void isValuesPaint(boolean isTimeStampsPaint) {
        this.isValuesPaint = isTimeStampsPaint;
    }


    private void prepareSimpleAxis_(Graphics g, int startIndex, double frequency) {
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

    private void prepareSimpleAxis(Graphics g, int startIndex, double frequency) {
        int width = g.getClipBounds().width;
        double pointDistance = 1;
        if(frequency > 0) {
            pointDistance = 1 / frequency;
        }

        int minPointsNumber = 50;
        double minStep = minPointsNumber * pointDistance;

        int exponent = (int) Math.log10(minStep);
        if(Math.log10(minStep) < 0) {
            exponent = exponent - 1;
        }


        double minStepNormalized = minStep / Math.pow(10, exponent);
        int firstFigure = (int) (minStep / Math.pow(10, exponent));

        int[] steps = {2, 5, 10};
        double step = 0;
        int j=0;
        while(step == 0 && j < steps.length) {
            if (minStepNormalized == steps[j] || firstFigure < steps[j]) {
                step = steps[j];
            }
            if(step == 10){
                step = 1;
                exponent = exponent + 1;
            }
            j++;
        }

        step = step * Math.pow(10, exponent);
        String stringFormat = "%.0f";
        if(exponent < 0) {
            stringFormat = "%."+Math.abs(exponent)+"f";
        }

        valueStamps = new HashMap<Integer, String>();
        valueIndexes = new ArrayList<Integer>();
        gridIndexes = new ArrayList<Integer>();
        interGridIndexes = new ArrayList<Integer>();

        double stepPointsNumber = step/pointDistance;
        for(int i = 0; i <= width; i++) {
            int index = startIndex + i;
            int divider = (int)(index / stepPointsNumber);
            double value = step * divider;
            if(index - divider * stepPointsNumber < 1) {
                valueIndexes.add(index);
                valueStamps.put(index, String.format(stringFormat, value));
            }
            double fifthStepPointsNumber = stepPointsNumber / 5;
            divider =  (int)(index / fifthStepPointsNumber);
            if(index - divider * fifthStepPointsNumber < 1) {
                gridIndexes.add(index);
            }
            double tenthStepPointsNumber = stepPointsNumber / 10;
            divider =  (int)(index / tenthStepPointsNumber);
            if(index - divider * tenthStepPointsNumber < 1) {
                interGridIndexes.add(index);
            }
        }
    }

    private void prepareTimeAxis(Graphics g, int startIndex,  double frequency, long startTime) {
        int width = g.getClipBounds().width;
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

        int TIME_INTERVAL_DIVIDER_MAX = 5;
        int TIME_INTERVAL_DIVIDER_MIN = 3;

        String DATE_FORMAT_FULL = "HH:mm:ss-SSS";
        String DATE_FORMAT_SEC = "HH:mm:ss";
        String DATE_FORMAT_MIN = "HH:mm";
        String DATE_FORMAT_HOUR = "HH";

        int timeInterval = MSECOND;
        int timeIntervalDivider = TIME_INTERVAL_DIVIDER_MAX;
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_FULL);

        int timeIntervalMin = (int) (NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN * 1000 / frequency);
        int timeIntervalMax = (int) (NUMBER_OF_POINTS_PER_TIME_INTERVAL_MAX * 1000 / frequency);

        for (int i = TIME_INTERVALS.length - 1; i >= 0; i--) {
            int interval = TIME_INTERVALS[i];
            if (timeIntervalMin <= interval && interval <= timeIntervalMax) {
                timeInterval = interval;
            }
        }

        if (timeInterval >= SECOND) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_SEC);
        }
        if (timeInterval >= MINUTE) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_MIN);
        }
        if (timeInterval >= HOUR) {
            dateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR);
        }
        if (timeInterval == MINUTE || timeInterval == HOUR || timeInterval == SECONDS_30
                || timeInterval == MINUTES_30) {
            timeIntervalDivider = TIME_INTERVAL_DIVIDER_MIN;
        }


        int timeIntervalPoints = (int) (timeInterval * frequency / 1000);
        int indexFrom = startIndex - timeIntervalPoints;
        int indexTill = startIndex + width + timeIntervalPoints;

        for (int i = indexFrom; i <= indexTill; i++) {
            long iTime = startTime + (long) (i * 1000 / frequency);
            long nextTime = startTime + (long) ((i + 1) * 1000 / frequency);
            if (iTime < 0) {
                iTime = 0;
                if (valueIndexes.size() == 0)
                    valueIndexes.add(0);
            }
            if (nextTime < 0) {
                nextTime = 0;
            }
            long n = iTime / timeInterval;
            long n_next = nextTime / timeInterval;
            if (n_next != n) { // means that time mark is located between points i and i+1
                String timeStamp = dateFormat.format(new Date((n + 1) * timeInterval));
                valueIndexes.add(i);
                valueStamps.put(i, timeStamp);
            }
        }
        for (int i = 0; i < valueIndexes.size() - 1; i++) {
            int delta = valueIndexes.get(i + 1) - valueIndexes.get(i);
            for (int j = 0; j < timeIntervalDivider; j++) {
                int markIndex = valueIndexes.get(i) + delta * j / timeIntervalDivider;
                gridIndexes.add(markIndex);
            }
        }

        for (int i = 0; i < gridIndexes.size() - 1; i++) {
            int delta = gridIndexes.get(i + 1) - gridIndexes.get(i);
            int markIndex = gridIndexes.get(i) + delta / 2;
            interGridIndexes.add(markIndex);
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


    public void paint(Graphics g, int startIndex,  double frequency, long startTime) {
        if(isTimeAxis && frequency != 0) {
            prepareTimeAxis(g, startIndex, frequency, startTime);
        }
        else {
            prepareSimpleAxis(g, startIndex, frequency);
        }
        paint(g, startIndex);
    }
}
