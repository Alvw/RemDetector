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
            if(counter <= bufferSize) {
                counter++;
                notifyListeners(value - (int)(sum / counter));
            }
            else {
                sum -= bufferedValues.poll();
                notifyListeners(value - (int)(sum / (bufferSize + 1)));
            }
        }
    }
}
