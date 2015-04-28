package dreamrec;

import bdf.BdfConfig;
import bdf.BdfNormalizer;
import prefilters.FrequencyDividingPreFilter;
import prefilters.PreFilter;

/**
 *
 */
public class RemConfigurator {
    private int eogRemFrequency;
    private int accelerometerRemFrequency;
    // real max sps (Samples per seconds) is about 2000...
    // rps(records per seconds) <= sps
    private final int RPS_MAX = 20000;


    public RemConfigurator(int eogRemFrequency, int accelerometerRemFrequency) {
        this.eogRemFrequency = eogRemFrequency;
        this.accelerometerRemFrequency = accelerometerRemFrequency;
    }

    /*
              * final duration Of joined data record should be or 1 or 1/RPS_MIN (if rps and all frequencies are divisible by RPS_MIN)
              * if durationOfDataRecord >= 1 we don´t change it
              */
    public int getNumberOfRecordsToJoin(BdfConfig bdfConfig) {
        double normalizedDurationOfDataRecord = BdfNormalizer.getNormalizedDurationOfDataRecord(bdfConfig);
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
        int[] frequencies = BdfNormalizer.getNormalizedSignalsFrequencies(bdfConfig);
        for(int i = 0; i < frequencies.length; i++) {
            if((frequencies[i] % RPS_MIN) != 0) {
                return rps;
            }
        }
        return rps/RPS_MIN;
    }


    public PreFilter[] getPreFilters(BdfConfig bdfConfig, RemChannels remChannels) throws ApplicationException {
        int rps = (int) (1/bdfConfig.getDurationOfDataRecord());
        if(rps > RPS_MAX) { // something wrong and we just do nothing
            String errorMsg = "Frequencies are too high for REM mode";
            throw new ApplicationException(errorMsg);
        }
        int[] frequencies = BdfNormalizer.getNormalizedSignalsFrequencies(bdfConfig);
        PreFilter[] preFilters = new PreFilter[frequencies.length];
        for(int i = 0; i < preFilters.length; i++) {
            if(eogRemFrequency != 0 && i == remChannels.getEog() ) {
                if ((frequencies[i] % eogRemFrequency) == 0) {

                    int divider = frequencies[i] / eogRemFrequency;
                    if(divider > 1) {
                       preFilters[i] = new FrequencyDividingPreFilter(divider);

                    }
                } else {
                    String errorMsg = "Eog Frequency= " + frequencies[i] + " is not divisible by EogRemFrequency=" + eogRemFrequency;
                    throw new ApplicationException(errorMsg);
                }
            }
            if(accelerometerRemFrequency != 0 && (i == remChannels.getAccelerometerX() || i == remChannels.getAccelerometerY() || i == remChannels.getAccelerometerZ())) {
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
