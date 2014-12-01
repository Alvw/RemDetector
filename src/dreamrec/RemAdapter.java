package dreamrec;

import bdf.BdfConfig;
import bdf.RecordingBdfConfig;

/**
 *
 */
public class RemAdapter {
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    private BdfConfig bdfConfig;
    private RemConfig remConfig;
    boolean[] activeChannels;


    public RemAdapter(BdfConfig bdfConfig, RemConfig remConfig, int eogRemFrequency, int accelerometerRemFrequency) {
        this.eogRemFrequency = eogRemFrequency;
        this.accelerometerRemFrequency = accelerometerRemFrequency;
        this.bdfConfig = bdfConfig;
        this.remConfig = remConfig;
        activeChannels = new boolean[bdfConfig.getNumberOfSignals()];
        for(int i = 0; i < activeChannels.length; i++) {
            activeChannels[i] = false;
        }
    }

    public void setActiveChannels (boolean[] activeChannels)  {
        int length = Math.min(this.activeChannels.length, activeChannels.length);
        for(int i = 0; i < length; i++) {
            this.activeChannels[i] = activeChannels[i];
        }
    }

    public int[] getDividers() throws ApplicationException {
        int[] dividers = new int[bdfConfig.getNumberOfSignals()];
        double durationOfDataRecord = bdfConfig.getDurationOfDataRecord();
        divide(durationOfDataRecord, 1); // check if durationOfDataRecord is int
        int[] numbersOfSamplesInEachDataRecord = bdfConfig.getNumbersOfSamplesInEachDataRecord();
        for(int i = 0; i < dividers.length; i++) {
            if(i == remConfig.getAccelerometerX() || i == remConfig.getAccelerometerY() || i == remConfig.getAccelerometerZ()) {
                 double frequency = numbersOfSamplesInEachDataRecord[i]/durationOfDataRecord;
                dividers[i] = divide(frequency, accelerometerRemFrequency);
            }
            else if(i == remConfig.getEog()) {
                double frequency = numbersOfSamplesInEachDataRecord[i]/durationOfDataRecord;
                dividers[i] = divide(frequency, eogRemFrequency);
            }
            else if(activeChannels[i]) {
                double frequency = numbersOfSamplesInEachDataRecord[i]/durationOfDataRecord;
                dividers[i] = divide(frequency, eogRemFrequency);
            }
            else{
                dividers[i] = 0;
            }
        }
        return dividers;
    }

    private int divide(double frequency, int resultFrequency) throws ApplicationException {
        String errorMsg = "Data Provider frequencies are not compatible with REM frequencies";
        int intFrequency = (int) frequency;
        if(frequency == intFrequency) {
            if(intFrequency%resultFrequency == 0) {
                return intFrequency/resultFrequency;
            }
        }
        throw new ApplicationException(errorMsg);
    }



}
