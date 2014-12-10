package prefilters;

import java.util.LinkedList;

public class HiPassPreFilter extends AbstractPreFilter{
    private LinkedList<Integer> bufferedValues;
    private int bufferSize;
    private int counter;
    private long sum;

    public HiPassPreFilter(int bufferSize) {
        this(null, bufferSize);
    }

    public HiPassPreFilter(PreFilter input, int bufferSize) {
        super(input);
        this.bufferSize = bufferSize;
        bufferedValues = new LinkedList<Integer>();
    }

    @Override
    public void add(int value) {
        if(bufferSize == 0) {
            notifyListeners(value);
        }
        else{
            bufferedValues.add(value);
            sum += value;
            int resultValue;
            if(counter < bufferSize) {
                counter++;
                resultValue = value - (int)(sum / counter);
                notifyListeners(resultValue);
            }
            else {
                sum -= bufferedValues.poll();
                resultValue = value - (int)(sum / bufferSize);
                notifyListeners(resultValue);
            }
        }
    }
}
