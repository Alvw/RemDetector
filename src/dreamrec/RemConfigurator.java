package dreamrec;

import bdf.RecordingBdfConfig;
import prefilters.FrequencyDividingPreFilter;
import prefilters.HiPassPreFilter;
import prefilters.PreFilter;

/**
 *
 */
public class RemConfigurator {
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    private int eogRemCutoffPeriod;

    private RecordingBdfConfig bdfConfig;
    private RemConfig remConfig;
    boolean[] activeChannels;


    public RemConfigurator(RecordingBdfConfig bdfConfig, RemConfig remConfig) {
        this.bdfConfig = bdfConfig;
        this.remConfig = remConfig;
        activeChannels = new boolean[bdfConfig.getNumberOfSignals()];
        for(int i = 0; i < activeChannels.length; i++) {
            activeChannels[i] = false;
        }
     }

    public void setEogRemFrequency(int eogRemFrequency) {
        this.eogRemFrequency = eogRemFrequency;
    }

    public void setAccelerometerRemFrequency(int accelerometerRemFrequency) {
        this.accelerometerRemFrequency = accelerometerRemFrequency;
    }

    public void setEogRemCutoffPeriod(int eogRemCutoffPeriod) {
        this.eogRemCutoffPeriod = eogRemCutoffPeriod;
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
        if((rps % RPS_MIN) != 0 || (eogRemFrequency % RPS_MIN) != 0 || (accelerometerRemFrequency % RPS_MIN) != 0) {
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

    public PreFilter[] getPreFilters() throws ApplicationException {
        int rps = (int) (1/bdfConfig.getDurationOfDataRecord());
        if(rps > 10000) { // real max sps is about 2000... So if it is more 10000... something wrong and we just do nothing
            String errorMsg = "Frequencies are too high for REM mode";
            throw new ApplicationException(errorMsg);
        }
        int[] frequencies = bdfConfig.getNormalizedSignalsFrequencies();
        PreFilter[] preFilters = new PreFilter[frequencies.length];
        for(int i = 0; i < preFilters.length; i++) {
            if(activeChannels[i]) {
                if (eogRemFrequency != 0 && (frequencies[i] % eogRemFrequency) == 0) {
                    int divider = frequencies[i] / eogRemFrequency;
                    if(divider > 1) {
                        preFilters[i] = new FrequencyDividingPreFilter(divider);
                    }
                }
            }
            if(eogRemFrequency != 0 && i == remConfig.getEog() ) {
                if ((frequencies[i] % eogRemFrequency) == 0) {
                    int divider = frequencies[i] / eogRemFrequency;
                    if(divider > 1) {
                        int bufferSize = eogRemFrequency * eogRemCutoffPeriod;
                        preFilters[i] = new FrequencyDividingPreFilter(new HiPassPreFilter(bufferSize), divider);
                    }
                } else {
                    String errorMsg = "Eog Frequency= " + frequencies[i] + " is not divisible by EogRemFrequency=" + eogRemFrequency;
                    throw new ApplicationException(errorMsg);
                }
            }
            if(accelerometerRemFrequency != 0 && (i == remConfig.getAccelerometerX() || i == remConfig.getAccelerometerY() || i == remConfig.getAccelerometerZ())) {
                if((frequencies[i] % accelerometerRemFrequency) == 0 ) {
                    int divider = frequencies[i] / accelerometerRemFrequency;
                    if(divider > 1) {
                        preFilters[i] = new FrequencyDividingPreFilter(divider);
                    }
                }
                else {
                    String errorMsg = "Accelerometer Frequency= "+frequencies[i] + " is not divisible by AccelerometerRemFrequency="+accelerometerRemFrequency;
                    throw new ApplicationException(errorMsg);
                }
            }
        }
        return preFilters;
    }
}
