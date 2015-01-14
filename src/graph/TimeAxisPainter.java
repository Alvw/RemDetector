package graph;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class TimeAxisPainter {
    private static final Color AXIS_COLOR = Color.GREEN;
    private static final Color BASE_GRID_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_GRID_COLOR = new Color(25, 25, 25);

    private static void paintTimeMark(Graphics g, int i) {
        g.setColor(BASE_GRID_COLOR);
        g.drawLine(i, 0, i, -g.getClipBounds().height);
        // Paint Rectangle
        g.setColor(AXIS_COLOR);
        g.fillRect(i - 1, -4, 3, 9);
    }
    private static  void paintGridBaseMark(Graphics g, int i) {
        g.setColor(LIGHT_GRID_COLOR);
        g.drawLine(i, 0, i, -g.getClipBounds().height);

        // Paint Stroke
        g.setColor(AXIS_COLOR);
        g.drawLine(i, -2, i, +2);
    }

    private static  void paintGridSmallMark(Graphics g, int i) {
        // Paint Point
        g.setColor(AXIS_COLOR);
        g.drawLine(i, 0, i, 0);
    }

    private static void paintTimeStamp(Graphics g, int i, String timeStamp) {
        // Paint Time Stamp
        g.setColor(AXIS_COLOR);
        g.drawString(timeStamp, i - 15, +18);
    }

    static void paint(Graphics g, long startTime, int startIndex, double timeFrequency) {
        if(timeFrequency == 0) {
            return;
        }
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

        int NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN = 100;
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

        int timeIntervalMin = (int)(NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN * 1000 / timeFrequency);
        int timeIntervalMax = (int)(NUMBER_OF_POINTS_PER_TIME_INTERVAL_MAX * 1000 / timeFrequency);

        for(int i = TIME_INTERVALS.length - 1; i >=0; i--) {
            int interval = TIME_INTERVALS[i];
            if( timeIntervalMin <= interval && interval <= timeIntervalMax) {
                timeInterval = interval;
            }
        }

        if(timeInterval >= SECOND){
            dateFormat = new SimpleDateFormat(DATE_FORMAT_SEC);
        }
        if(timeInterval >= MINUTE){
            dateFormat = new SimpleDateFormat(DATE_FORMAT_MIN);
        }
        if(timeInterval >= HOUR){
            dateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR);
        }
        if(timeInterval == MINUTE || timeInterval == HOUR || timeInterval == SECONDS_30
                || timeInterval == MINUTES_30){
            timeIntervalDivider = TIME_INTERVAL_DIVIDER_MIN;
        }

        int width = g.getClipBounds().width;
        ArrayList<Integer> timeMark = new ArrayList<Integer>();
        ArrayList<Integer> baseGridMark = new ArrayList<Integer>();
        ArrayList<Integer> smallGridMark = new ArrayList<Integer>();
        int timeIntervalPoints = (int)(timeInterval * timeFrequency / 1000);
        int indexFrom = startIndex - timeIntervalPoints;
        int indexTill = startIndex + width + timeIntervalPoints;

        g.setColor(AXIS_COLOR);
        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
        for (int i = indexFrom; i  <= indexTill; i++) {
            long iTime = startTime + (long)(i * 1000 / timeFrequency);
            long nextTime = startTime + (long)((i +1) * 1000 / timeFrequency);
            if(iTime < 0) {
                iTime = 0;
                if(timeMark.size() == 0)
                    timeMark.add(0);
            }
            if(nextTime < 0) {
                nextTime = 0;
            }
            long n = iTime/timeInterval;
            long n_next = nextTime/timeInterval;
            if(n_next != n) { // means that time mark is located between points i and i+1
                timeMark.add(i);
            }
        }
        for(int i = 0; i < timeMark.size() - 1; i++) {
            int delta = timeMark.get(i+1)  - timeMark.get(i);
            for(int j = 0; j < timeIntervalDivider; j++) {
                int markIndex = timeMark.get(i) + delta * j / timeIntervalDivider;
                baseGridMark.add(markIndex);
            }
        }

        for(int i = 0; i < baseGridMark.size() - 1; i++) {
            int delta = baseGridMark.get(i+1)  - baseGridMark.get(i);
            int markIndex = baseGridMark.get(i) + delta/2;
            smallGridMark.add(markIndex);
        }

        for(int i = 0; i < smallGridMark.size() - 1; i++) {
            int point = smallGridMark.get(i) - startIndex;
            if(point >= 0) {
                paintGridSmallMark(g, point);
            }
        }
        for(int i = 0; i < baseGridMark.size() - 1; i++) {
            int point = baseGridMark.get(i) - startIndex;
            if(point >= 0) {
                paintGridBaseMark(g, point);
            }
        }
        for(int i = 0; i < timeMark.size() - 1; i++) {
            int markIndex = timeMark.get(i);
            int point = markIndex - startIndex;
            if(point >= 0) {
                long markTime = startTime + (long)(i * 1000 / timeFrequency);
                long n = markTime/timeInterval;
                String timeStamp = dateFormat.format(new Date((n+1)*timeInterval));

                paintTimeMark(g, point);
                paintTimeStamp(g, point, timeStamp);
            }

        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }
}
