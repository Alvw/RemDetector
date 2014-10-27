package dreamrec;

import device.BdfConfig;
import device.BdfDataListener;
import device.BdfDataSource;
import device.BdfSignalConfig;

import java.util.ArrayList;
import java.util.List;

public class FrequencyDivider implements BdfDataSource, BdfDataListener {
    private ArrayList<BdfDataListener> bdfDataListenersList = new ArrayList<BdfDataListener>();
    private int counter;
    private BdfConfig bdfConfigNew;
    private int[] dividers;
    private int numberOfSignals;
    private BdfDataSource bdfDataSource;


    public FrequencyDivider(BdfDataSource bdfDataSource, double maxFrequency) {
        bdfDataSource.addBdfDataListener(this);
        this.bdfDataSource = bdfDataSource;
        BdfConfig bdfConfig = bdfDataSource.getBdfConfig();
        int maxNumberOfSamples = (int) (maxFrequency * bdfConfig.getDurationOfADataRecord());
        bdfConfigNew = bdfDataSource.getBdfConfig().clone();
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
        for(BdfDataListener bdfDataListener : bdfDataListenersList){
            bdfDataListener.onDataRecordReceived(dataRecordNew);
        }
    }

    @Override
    public void onStopReading() {
        for (BdfDataListener bdfDataListener : bdfDataListenersList) {
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
    public void addBdfDataListener(BdfDataListener bdfDataListener) {
        bdfDataListenersList.add(bdfDataListener);
    }

    @Override
    public BdfConfig getBdfConfig() {
        return bdfConfigNew;
    }

    @Override
    public void removeBdfDataListener(BdfDataListener bdfDataListener) {

    }
}