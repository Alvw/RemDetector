package graph;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TimeAxisPainter {
    private Color axisColor = Color.GREEN;
    private Color gridColor = new Color(0, 40, 0);
    private Color strongGridColor = new Color(0, 60, 0);
    private Color zebraColor = new Color(40, 40, 40);
    private boolean  isGridPaint = true;
    private boolean  isAxisPaint = true;
    private boolean isValuesPaint = true;


    public void isGridPaint(boolean isGridPaint) {
        this.isGridPaint = isGridPaint;
    }


    public void isAxisPaint(boolean isAxisPaint) {
        this.isAxisPaint = isAxisPaint;
    }

    public void isValuesPaint(boolean isTimeStampsPaint) {
        this.isValuesPaint = isTimeStampsPaint;
    }


    private void paintValueMark(Graphics g, int i) {
        // Paint Stroke 2-3 times
        g.drawLine(i, -3, i, +3);
        g.drawLine(i + 1, -3, i + 1, +3);
        g.drawLine(i - 1, -3, i - 1, +3);
    }

    private void paintGridMark(Graphics g, int i) {
        // Paint Stroke
        g.drawLine(i, -2, i, +2);
    }

    private void paintInterGridMark(Graphics g, int i) {
        // Paint Point
        g.drawLine(i, 0, i, 0);
    }

    private void paintValueStamp(Graphics g, int i, String valueStamp) {
        // Paint Time Stamp
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int stampWidth = fm.stringWidth(valueStamp);
        int stampHeight = fm.getHeight();
        int stampShift = stampWidth / 2;
        // g.drawString(timeStamp, i - stampShift, stampHeight+2);
        g.drawString(valueStamp, i - stampShift, - 6);
    }

    private void paintGrid(Graphics g, int startIndex, List<Integer> gridMarks) {
        g.setColor(gridColor);
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                g.drawLine(point, g.getClipBounds().height, point, -g.getClipBounds().height);
            }
        }
    }

    private void paintStrongGrid(Graphics g, int startIndex, List<Integer> gridMarks) {
        g.setColor(strongGridColor);
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                g.drawLine(point, g.getClipBounds().height, point, -g.getClipBounds().height);
               // g.drawLine(point - 1, g.getClipBounds().height, point - 1, -g.getClipBounds().height);
                g.drawLine(point + 1, g.getClipBounds().height, point + 1, -g.getClipBounds().height);
            }
        }
    }


    private void paintZebra(Graphics g, int startIndex, List<Integer> gridMarks) {
        g.setColor(zebraColor);
        boolean flag = false;
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point1 = gridMarks.get(i) - startIndex;
            int point2 = gridMarks.get(i+1) - startIndex;
            if(point1 < 0 && point2 > 0) {
                g.fillRect(0, -g.getClipBounds().height, point2, 2*g.getClipBounds().height);
                flag = true;
            }

            if (flag && point1 >= 0) {
                g.fillRect(point1, -g.getClipBounds().height, point2-point1, 2*g.getClipBounds().height);
            }
            flag = !flag;
        }
    }

    private void paintAxis(Graphics g, int startIndex, List<Integer> valueMarks, List<Integer> gridMarks, List<Integer> interGridMarks) {
        g.setColor(axisColor);
        for (int i = 0; i < interGridMarks.size() - 1; i++) {
            int point = interGridMarks.get(i) - startIndex;
            if (point >= 0) {
                paintInterGridMark(g, point);
            }
        }
        for (int i = 0; i < gridMarks.size() - 1; i++) {
            int point = gridMarks.get(i) - startIndex;
            if (point >= 0) {
                paintGridMark(g, point);
            }
        }
        for (int i = 0; i < valueMarks.size() - 1; i++) {
            int markIndex = valueMarks.get(i);
            int point = markIndex - startIndex;
            if (point >= 0) {
                paintValueMark(g, point);
            }
        }
    }

    private void paintValues(Graphics g, int startIndex, HashMap<Integer, String> valueStamps) {
        g.setColor(axisColor);
        for (int i : valueStamps.keySet()) {
            int point = i - startIndex;
            if (point >= 0) {
              //  paintValueMark(g, point);
                paintValueStamp(g, point, valueStamps.get(i));
            }
        }
    }


    public void paint(Graphics g, long startTime, int startIndex, double timeFrequency) {

        int width = g.getClipBounds().width;

        HashMap<Integer, String> valueStamps = new HashMap<Integer, String>();
        ArrayList<Integer> valueIndexes = new ArrayList<Integer>();
        ArrayList<Integer> gridIndexes = new ArrayList<Integer>();
        ArrayList<Integer> interGridIndexes = new ArrayList<Integer>();

        if (timeFrequency == 0) {
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
        else{
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


            int timeIntervalPoints = (int) (timeInterval * timeFrequency / 1000);
            int indexFrom = startIndex - timeIntervalPoints;
            int indexTill = startIndex + width + timeIntervalPoints;

            for (int i = indexFrom; i <= indexTill; i++) {
                long iTime = startTime + (long) (i * 1000 / timeFrequency);
                long nextTime = startTime + (long) ((i + 1) * 1000 / timeFrequency);
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

        Graphics2D g2d = (Graphics2D) g;
        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation

        if(isGridPaint) {
            paintGrid(g, startIndex, gridIndexes);
            paintStrongGrid(g, startIndex, valueIndexes);
        }
        if(isAxisPaint) {
            paintAxis(g, startIndex, valueIndexes, gridIndexes, interGridIndexes);
        }
        if(isValuesPaint) {
            paintValues(g, startIndex, valueStamps);
        }

        g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0)); // flip transformation
    }
}
