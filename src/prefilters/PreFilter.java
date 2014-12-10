package prefilters;

public interface PreFilter {
    public void add(int value);
    public int getDivider();
    public void addListener(PreFilter listener);
}
