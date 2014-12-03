package dreamrec;

import bdf.BdfConfig;
import bdf.RecordingBdfConfig;

/**
 *
 */
public class RemConfigurator {
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    private BdfConfig bdfConfig;
    private RemConfig remConfig;
    boolean[] activeChannels;


    public RemConfigurator(BdfConfig bdfConfig, RemConfig remConfig, int eogRemFrequency, int accelerometerRemFrequency) {
        this.eogRemFrequency = eogRemFrequency;
        this.accelerometerRemFrequency = accelerometerRemFrequency;
        this.bdfConfig = bdfConfig;
        this.remConfig = remConfig;
        activeChannels = new boolean[bdfConfig.getNumberOfSignals()];
        for(int i = 0; i < activeChannels.length; i++) {
            activeChannels[i] = false;
        }
    }

    /*
     * we suppose:
     * 1)  that number of records per seconds(rps) = 1/durationOfDataRecord is integer. Or durationOfDataRecord is already integer
     * 2) real durationOfDataRecord is only slightly different from its supposed theoretical value
     * So for example instead of 500 Hz real frequency will be 503 Hz or so on
     *
     * Here we calculate that theoretical normalized DurationOfData record on the base of its real value
     */
    private double normalizeDurationOfDataRecord(double durationOfDataRecord) {
        double normalizedDurationOfDataRecord;
        if(durationOfDataRecord > 3/4) { // case durationOfDataRecord is integer
            normalizedDurationOfDataRecord = Math.round(durationOfDataRecord);
        }
        else { // duration of data record is 1/2, 1/3, 1/4 ....
            long rps = Math.round(1 / durationOfDataRecord);
            normalizedDurationOfDataRecord = (1.0 / rps);
        }
        return normalizedDurationOfDataRecord;
    }

    private int[] getNormalizedFrequencies() {
        double normalizedDurationOfDataRecord = normalizeDurationOfDataRecord(bdfConfig.getDurationOfDataRecord());
        int[] numbersOfSamplesInEachDataRecord = bdfConfig.getNumbersOfSamplesInEachDataRecord();
        int[] normalizedFrequencies = new int[numbersOfSamplesInEachDataRecord.length];
        for(int i = 0; i < numbersOfSamplesInEachDataRecord.length; i++) {
            normalizedFrequencies[i] = (int) (numbersOfSamplesInEachDataRecord[i] / normalizedDurationOfDataRecord);
        }
        return normalizedFrequencies;

    }

     /*
      * final duration Of joined data record should be or 1 or 1/RPS_MIN (if rps and all frequencies are divisible by RPS_MIN)
      * if durationOfDataRecord >= 1 we don´t change it
      */
    public int getNumberOfRecordsToJoin() {
        double normalizedDurationOfDataRecord = normalizeDurationOfDataRecord(bdfConfig.getDurationOfDataRecord());
        int RPS_MIN = 5;
        if(normalizedDurationOfDataRecord >= 1) {
            return 1;
        }
        else {
            int rps = (int) (1/normalizedDurationOfDataRecord);
            if((rps % RPS_MIN) != 0) {
                return rps;
            }
            if((eogRemFrequency % RPS_MIN) != 0 || (accelerometerRemFrequency % RPS_MIN) != 0) {
                return rps;
            }
            int[] frequencies = getNormalizedFrequencies();
            for(int i = 0; i < frequencies.length; i++) {
                if((frequencies[i] % RPS_MIN) != 0) {
                    return rps;
                }
            }
            return rps/RPS_MIN;
        }
    }

    public void setActiveChannels (boolean[] activeChannels)  {
        int length = Math.min(this.activeChannels.length, activeChannels.length);
        for(int i = 0; i < length; i++) {
            this.activeChannels[i] = activeChannels[i];
            if(i == remConfig.getEog() || i == remConfig.getAccelerometerX() || i == remConfig.getAccelerometerY() || i == remConfig.getAccelerometerZ()) {
                this.activeChannels[i] = true;
            }
        }
    }

    public int[] getDividers() throws ApplicationException {
        int[] frequencies = getNormalizedFrequencies();
        int[] dividers = new int[frequencies.length];
        for(int i = 0; i < dividers.length; i++) {
            if(i == remConfig.getAccelerometerX() || i == remConfig.getAccelerometerY() || i == remConfig.getAccelerometerZ()) {
                 if((frequencies[i] % accelerometerRemFrequency) == 0 ) {
                     dividers[i] = frequencies[i] / accelerometerRemFrequency;
                 }
                else {
                     String errorMsg = "Accelerometer Frequency= "+frequencies[i] + "\n is not divisible by AccelerometerRemFrequency="+accelerometerRemFrequency;
                     throw new ApplicationException(errorMsg);
                 }
            }
            else if(i == remConfig.getEog() ) {
                if((frequencies[i] % eogRemFrequency) == 0 ) {
                    dividers[i] = frequencies[i] / eogRemFrequency;
                }
                else {
                    String errorMsg = "Eog Frequency= "+frequencies[i] + "\n is not divisible by EogRemFrequency="+eogRemFrequency;
                    throw new ApplicationException(errorMsg);
                }
            }
            else if(activeChannels[i]) {
                if((frequencies[i] % eogRemFrequency) == 0 ) {
                    dividers[i] = frequencies[i] / eogRemFrequency;
                }
                else {
                    dividers[i] = 1;
                }
            }
            else{
                dividers[i] = 0;
            }
        }
        return dividers;
    }
}
