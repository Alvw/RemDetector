package graph;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class TimeAxisPainter {
    private Color axisColor = Color.GREEN;
    private Color baseGridColor = new Color(50, 50, 50);
    private Color lightGridColor = new Color(25, 25, 25);


    public void setAxisColor(Color axisColor) {
        this.axisColor = axisColor;
    }

    public void setBaseGridColor(Color baseGridColor) {
        this.baseGridColor = baseGridColor;
    }

    public void setLightGridColor(Color lightGridColor) {
        this.lightGridColor = lightGridColor;
    }

    private void paintTimeMark(Graphics g, int i) {
        // Paint Rectangle
        g.fillRect(i - 1, -4, 3, 9);
    }

    private void paintBaseMark(Graphics g, int i) {
        // Paint Stroke
        g.drawLine(i, -2, i, +2);
    }

    private void paintSmallMark(Graphics g, int i) {
        // Paint Point
        g.drawLine(i, 0, i, 0);
    }

    private void paintTimeStamp(Graphics g, int i, String timeStamp) {
        // Paint Time Stamp
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int stampWidth = fm.stringWidth(timeStamp);
        int stampHeight = fm.getHeight();
        int stampShift = stampWidth / 2;
        g.drawString(timeStamp, i - stampShift, - 10);
    }

    private void paintGrid(Graphics g, Color gridColor, int startIndex, List<Integer> gridMarks) {
        g.setColor(gridColor);
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                g.drawLine(point, g.getClipBounds().height, point, -g.getClipBounds().height);
            }
        }
    }

    private void paintAxis(Graphics g, int startIndex, List<Integer> timeMarks, List<Integer> baseMarks, List<Integer> smallMarks) {
        g.setColor(axisColor);
        for (int i = 0; i < smallMarks.size() - 1; i++) {
            int point = smallMarks.get(i) - startIndex;
            if (point >= 0) {
                paintSmallMark(g, point);
            }
        }
        for (int i = 0; i < baseMarks.size() - 1; i++) {
            int point = baseMarks.get(i) - startIndex;
            if (point >= 0) {
                paintBaseMark(g, point);
            }
        }
        for (int i = 0; i < timeMarks.size() - 1; i++) {
            int markIndex = timeMarks.get(i);
            int point = markIndex - startIndex;
            if (point >= 0) {
                paintTimeMark(g, point);
            }
        }
    }

    private void paintTime(Graphics g, int startIndex, HashMap<Integer, String> timeStamps) {
        g.setColor(axisColor);
        for (int i : timeStamps.keySet()) {
            int point = i - startIndex;
            if (point >= 0) {
                paintTimeStamp(g, point, timeStamps.get(i));
            }
        }
    }


    void paint(Graphics g, long startTime, int startIndex, double timeFrequency) {
        if (timeFrequency == 0) {
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

        int timeIntervalMin = (int) (NUMBER_OF_POINTS_PER_TIME_INTERVAL_MIN * 1000 / timeFrequency);
        int timeIntervalMax = (int) (NUMBER_OF_POINTS_PER_TIME_INTERVAL_MAX * 1000 / timeFrequency);

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

        int width = g.getClipBounds().width;

        HashMap<Integer, String> timeStamps = new HashMap<Integer, String>();
        ArrayList<Integer> timeMarks = new ArrayList<Integer>();
        ArrayList<Integer> baseMarks = new ArrayList<Integer>();
        ArrayList<Integer> smallMarks = new ArrayList<Integer>();

        int timeIntervalPoints = (int) (timeInterval * timeFrequency / 1000);
        int indexFrom = startIndex - timeIntervalPoints;
        int indexTill = startIndex + width + timeIntervalPoints;

        for (int i = indexFrom; i <= indexTill; i++) {
            long iTime = startTime + (long) (i * 1000 / timeFrequency);
            long nextTime = startTime + (long) ((i + 1) * 1000 / timeFrequency);
            if (iTime < 0) {
                iTime = 0;
                if (timeMarks.size() == 0)
                    timeMarks.add(0);
            }
            if (nextTime < 0) {
                nextTime = 0;
            }
            long n = iTime / timeInterval;
            long n_next = nextTime / timeInterval;
            if (n_next != n) { // means that time mark is located between points i and i+1
                String timeStamp = dateFormat.format(new Date((n + 1) * timeInterval));
                timeMarks.add(i);
                timeStamps.put(i, timeStamp);
            }
        }
        for (int i = 0; i < timeMarks.size() - 1; i++) {
            int delta = timeMarks.get(i + 1) - timeMarks.get(i);
            for (int j = 0; j < timeIntervalDivider; j++) {
                int markIndex = timeMarks.get(i) + delta * j / timeIntervalDivider;
                baseMarks.add(markIndex);
            }
        }

        for (int i = 0; i < baseMarks.size() - 1; i++) {
            int delta = baseMarks.get(i + 1) - baseMarks.get(i);
            int markIndex = baseMarks.get(i) + delta / 2;
            smallMarks.add(markIndex);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
        paintGrid(g, lightGridColor, startIndex, baseMarks);
        paintGrid(g, baseGridColor, startIndex, timeMarks);
        paintAxis(g, startIndex, timeMarks, baseMarks, smallMarks);
        paintTime(g, startIndex, timeStamps);
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }
}
