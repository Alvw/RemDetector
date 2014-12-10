package prefilters;

public class FrequencyDividingPreFilter extends AbstractPreFilter {
    private int counter;
    private long sum;

    public FrequencyDividingPreFilter(int divider) {
        this(null, divider);
    }

    public FrequencyDividingPreFilter(PreFilter input, int divider) {
        super(input);
        this.divider = divider;
    }

    @Override
    public void add(int value) {
        counter++;
        if (counter != divider) {
            sum += value;
        }
        else {
            notifyListeners((int)(sum / divider));
            counter = 0;
            sum = 0;
        }
    }
}
