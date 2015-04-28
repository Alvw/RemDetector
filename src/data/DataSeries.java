package data;

/**
 *  Scaling gives us actual dependency dataValue(indexValue)
 *  based on the given  dependency of integers: data(index)
 *
 *  indexValue = index * stepInterval + startOffset
 *  where:
 *  stepInterval = getScaling().getSamplingInterval();
 *  startOffset = getScaling().getStart();
 *
 *  dataValue(indexValue) = get(index) * gain + offset
 *  where:
 *  gain = getScaling().getGain();
 *  offset = getScaling().getOffset();
 */


public interface DataSeries {
    public int size();
    public int get(int index);
    public Scaling getScaling();
}
