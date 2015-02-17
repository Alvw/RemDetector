package bdf;

/**
 * Created by mac on 17/02/15.
 */
public class BdfNormalizer {
/*
* we suppose:
*
* 1)  that "ideal/theoretical" number of records per seconds(rps) = 1/durationOfDataRecord is integer.
* Or durationOfDataRecord is already integer
*
* 2) Real durationOfDataRecord is only slightly different from its supposed theoretical value
* So for example instead of 500 Hz real frequency will be 503 Hz or so on
*
* Here we calculate that theoretical normalized DurationOfData record on the base of its real value
*/
    public static double getNormalizedDurationOfDataRecord(BdfConfig bdfConfig) {
        double durationOfDataRecord = bdfConfig.getDurationOfDataRecord();
        double normalizedDurationOfDataRecord;
        if(durationOfDataRecord > 3.0/4) { // case durationOfDataRecord is integer
            normalizedDurationOfDataRecord = Math.round(durationOfDataRecord);
        }
        else { // duration of data record is 1/2, 1/3, 1/4 ....
            long rps = Math.round(1 / durationOfDataRecord);
            normalizedDurationOfDataRecord = (1.0 / rps);
        }
        return normalizedDurationOfDataRecord;
    }

    public static int[] getNormalizedSignalsFrequencies(BdfConfig bdfConfig) {
        double normalizedDurationOfDataRecord = getNormalizedDurationOfDataRecord(bdfConfig);
        SignalConfig[] signalConfigs = bdfConfig.getSignalConfigs();
        int numberOfSignals = signalConfigs.length;
        int[] normalizedFrequencies = new int[numberOfSignals];
        for(int i = 0; i < numberOfSignals; i++) {
            normalizedFrequencies[i] = (int) (signalConfigs[i].getNumberOfSamplesInEachDataRecord() / normalizedDurationOfDataRecord);
        }
        return normalizedFrequencies;
    }
}
