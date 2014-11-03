package dreamrec;

import device.BdfConfig;
import device.BdfSignalConfig;
import device.DataListener;
import device.DataSource;

import java.util.ArrayList;
import java.util.List;

public class FrequencyDivider implements DataSource, DataListener {
    private ArrayList<DataListener> bdfDataListenersList = new ArrayList<DataListener>();
    private int counter;
    private BdfConfig bdfConfigNew;
    private int[] dividers;
    private int numberOfSignals;
    private DataSource bdfDataSource;


    public FrequencyDivider(DataSource dataSource, double maxFrequency) {
        bdfDataSource.addDataListener(this);
        this.bdfDataSource = bdfDataSource;
        BdfConfig bdfConfig = dataSource.getBdfConfig();
        int maxNumberOfSamples = (int) (maxFrequency * bdfConfig.getDurationOfADataRecord());
        bdfConfigNew = dataSource.getBdfConfig().clone();
        List<BdfSignalConfig> signalsConfigListNew = bdfConfigNew.getSignalsConfigList();
        numberOfSignals = signalsConfigListNew.size();
        dividers = new int[numberOfSignals];
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            BdfSignalConfig signalConfig = signalsConfigListNew.get(signalNumber);
            int numberOfSamples = signalConfig.getNrOfSamplesInEachDataRecord();
            if (numberOfSamples > maxNumberOfSamples) {
                dividers[signalNumber] =  numberOfSamples/maxNumberOfSamples;
                signalConfig.setNrOfSamplesInEachDataRecord(maxNumberOfSamples);
            } else {
                dividers[signalNumber] = 1;
            }
        }
    }

    @Override
    public void onDataRecordReceived(int[][] dataRecord) {
        int[][] dataRecordNew = new int[numberOfSignals][];
        for (int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
            if(dividers[signalNumber] == 1) {
                dataRecordNew[signalNumber] = dataRecord[signalNumber];
            }
            else{
                int numberOfSamplesInSignal = dataRecord[signalNumber].length;
                dataRecordNew[signalNumber] = new int[numberOfSamplesInSignal/dividers[signalNumber]];
                int sum = 0;
                for(int sampleNumber = 0; sampleNumber < numberOfSamplesInSignal; sampleNumber++){
                    sum += dataRecord[signalNumber][sampleNumber];
                    if((sampleNumber +1)%dividers[signalNumber] == 0) {
                        int sampleNumberNew = (sampleNumber +1)/dividers[signalNumber] -1;
                        dataRecordNew[signalNumber][sampleNumberNew] = sum/dividers[signalNumber];
                        sum = 0;
                    }
                }
            }
        }
        for(DataListener bdfDataListener : bdfDataListenersList){
            bdfDataListener.onDataRecordReceived(dataRecordNew);
        }
    }

    @Override
    public void onStopReading() {
        for (DataListener bdfDataListener : bdfDataListenersList) {
            bdfDataListener.onStopReading();
        }
    }

    @Override
    public void startReading() throws ApplicationException {
         bdfDataSource.startReading();
    }

    @Override
    public void stopReading() throws ApplicationException {
        bdfDataSource.stopReading();
    }

    @Override
    public void addDataListener(DataListener bdfDataListener) {
        bdfDataListenersList.add(bdfDataListener);
    }

    @Override
    public BdfConfig getBdfConfig() {
        return bdfConfigNew;
    }
}