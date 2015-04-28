package rem;

import data.DataSeries;
import data.Scaling;
import fft.Fourie;
import filters.FilterDerivative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SaccadeBatchDetector implements DataSeries {
    /**
     * Normal fixations: 150-900ms, but  also much longer fixations can occur -
     * overlong fixations: 4900 ms
     * SACCADE_DISTANCE_MAX = 5-6 sec
     */
    static final int SACCADE_DISTANCE_MAX = 6; // [sec]
    private static final int FOURIER_TIME = 11; // [sec]
    private SaccadesBatch tmpBatch;
    private List<SaccadesBatch> saccadesBatchList = new ArrayList<SaccadesBatch>();
    private HashMap<Integer, Integer> resultHash = new HashMap<Integer, Integer>();

    private int saccadeMaxDistancePoints;
    private int currentIndex = -1;
    private DataSeries inputData;
    private SaccadeDetector saccadeDetector;

    public SaccadeBatchDetector(DataSeries inputData) {
        double samplingRate = 1;
        if(inputData.getScaling() != null) {
            samplingRate = 1 / inputData.getScaling().getSamplingInterval();
        }
        this.inputData = inputData;
        saccadeDetector = new SaccadeDetector(inputData);
        saccadeMaxDistancePoints = (int)(SACCADE_DISTANCE_MAX * samplingRate );
    }

    public int getSaccadeValueMaxDigital() {
        return saccadeDetector.getSaccadeValueMaxDigital();
    }

    public double getSaccadeMaxValuePhysical() {
        return saccadeDetector.getSaccadeValueMaxPhysical();
    }


    private void detectNext() {
        currentIndex++;
        Saccade saccade = saccadeDetector.getNext();
        if(saccade != null) {
            SaccadesBatch lastBatch = null;
            if(saccadesBatchList.size() > 0) {
                lastBatch = saccadesBatchList.get(saccadesBatchList.size() - 1);
            }
            if(lastBatch != null && lastBatch.addSaccade(saccade)) {
                 addSaccadeToResult(saccade);
            }
            else{
                if(tmpBatch == null || !tmpBatch.addSaccade(saccade)) {
                    tmpBatch = new SaccadesBatch(saccade, saccadeMaxDistancePoints);
                }
                if(tmpBatch.isApproved()) {
                    addBatchToList(tmpBatch);
                    tmpBatch = null;
                }
            }
        }
    }

    private void addBatchToList(SaccadesBatch saccadesBatch) {
        saccadesBatchList.add(saccadesBatch);
        for(int i = 0; i < saccadesBatch.getNumberOfSaccades(); i++) {
            addSaccadeToResult(saccadesBatch.getSaccade(i));
        }
    }

    private void addSaccadeToResult(Saccade saccade) {
        for(int i = saccade.getBeginIndex(); i <= saccade.getEndIndex(); i++ ) {
            resultHash.put(i, Math.abs(saccade.getPeakValue()));
        }
     /*   DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        long time = inputData.getStart() + (long) (saccade.getPeakIndex() * 1000 / getSamplingRate());
        String timeStamp = dateFormat.format(new Date(time));
        System.out.println(timeStamp + ": |" + saccade.getWidth() + "| "  + saccade.getPeakToEnergyRatio());   */

    }


    private boolean isBatchPossible(SaccadesBatch batch) {
        Saccade lastSaccade = batch.getSaccade(batch.getNumberOfSaccades() - 1);
        DataSeries fourier =  Fourie.fftBackward( new FilterDerivative(inputData), lastSaccade.getEndIndex(), FOURIER_TIME);
        if(FourierAnalizer.hasAlfa(fourier)) {
            return false;
        }
        return true;
    }

    private void update() {
        if(currentIndex < inputData.size()){
            for(int i = currentIndex + 1; i < inputData.size(); i++) {
                detectNext();
            }
        }
    }

    @Override
    public int get(int index) {
        update();
        Integer value = resultHash.get(index);
        if(value != null) {
            return value;
        }
        return 0;
    }

    public DataSeries getThresholds() {
        return saccadeDetector.getThresholds();
    }

    @Override
    public int size() {
        update();
        if(saccadesBatchList.size() > 0) {
            SaccadesBatch lastBatch = saccadesBatchList.get(saccadesBatchList.size() -1);
            Saccade lastSaccade =  lastBatch.getSaccade(lastBatch.getNumberOfSaccades() - 1);
            return lastSaccade.getEndIndex();
        }
        return 0;
    }


    @Override
    public Scaling getScaling() {
        return inputData.getScaling();
    }
}


