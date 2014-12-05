package dreamrec;

import bdf.RecordingBdfConfig;

/**
 *
 */
public class RemConfigurator {
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    private RecordingBdfConfig bdfConfig;
    private RemConfig remConfig;
    boolean[] activeChannels;


    public RemConfigurator(RecordingBdfConfig bdfConfig, RemConfig remConfig, int eogRemFrequency, int accelerometerRemFrequency) {
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
      * final duration Of joined data record should be or 1 or 1/RPS_MIN (if rps and all frequencies are divisible by RPS_MIN)
      * if durationOfDataRecord >= 1 we don´t change it
      */
    public int getNumberOfRecordsToJoin() {
        double normalizedDurationOfDataRecord = bdfConfig.getNormalizedDurationOfDataRecord();
        int RPS_MIN = 5;
        if(eogRemFrequency == 0 && accelerometerRemFrequency == 0) {
            return 1;
        }
        if(normalizedDurationOfDataRecord >= 1) {
            return 1;
        }
        int rps = (int) (1/normalizedDurationOfDataRecord);
        if((rps % RPS_MIN) != 0) {
            return rps;
        }
        if((eogRemFrequency % RPS_MIN) != 0 || (accelerometerRemFrequency % RPS_MIN) != 0) {
            return rps;
        }
        int[] frequencies = bdfConfig.getNormalizedSignalsFrequencies();
        for(int i = 0; i < frequencies.length; i++) {
            if((frequencies[i] % RPS_MIN) != 0) {
                return rps;
            }
        }
        return rps/RPS_MIN;
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
        int[] frequencies = bdfConfig.getNormalizedSignalsFrequencies();
        int[] dividers = new int[frequencies.length];
        for(int i = 0; i < dividers.length; i++) {
            dividers[i] = 0;
            if(activeChannels[i]) {
                dividers[i] = 1;
                if (eogRemFrequency != 0 && (frequencies[i] % eogRemFrequency) == 0) {
                    dividers[i] = frequencies[i] / eogRemFrequency;
                }
            }
            if(eogRemFrequency != 0 && i == remConfig.getEog() ) {
                if ((frequencies[i] % eogRemFrequency) == 0) {
                    dividers[i] = frequencies[i] / eogRemFrequency;
                } else {
                    String errorMsg = "Eog Frequency= " + frequencies[i] + "\n is not divisible by EogRemFrequency=" + eogRemFrequency;
                    throw new ApplicationException(errorMsg);
                }
            }
            if(accelerometerRemFrequency != 0 && (i == remConfig.getAccelerometerX() || i == remConfig.getAccelerometerY() || i == remConfig.getAccelerometerZ())) {
                if((frequencies[i] % accelerometerRemFrequency) == 0 ) {
                    dividers[i] = frequencies[i] / accelerometerRemFrequency;
                }
                else {
                    String errorMsg = "Accelerometer Frequency= "+frequencies[i] + "\n is not divisible by AccelerometerRemFrequency="+accelerometerRemFrequency;
                    throw new ApplicationException(errorMsg);
                }
            }
        }
        return dividers;
    }
}
