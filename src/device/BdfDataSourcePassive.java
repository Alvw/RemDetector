package device;

public interface BdfDataSourcePassive {
    public BdfConfig getBdfConfig();
    public boolean isBdfDataRecordAvailable();
    public int[] readBdfDataRecord();
}
