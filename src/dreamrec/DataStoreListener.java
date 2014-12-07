package dreamrec;

public interface DataStoreListener {
    public void onDataUpdate();
    public void onStart(long startTime);
}
