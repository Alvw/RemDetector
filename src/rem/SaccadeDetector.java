package rem;

import data.DataDimension;
import data.DataSeries;
import dreamrec.FourierAnalizer;
import fft.Fourie;
import filters.FilterDerivative;
import filters.FilterDerivativeRem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SaccadeDetector implements DataSeries {

    private static final int SACCADE_DISTANCE_MAX = 12; // sec

    private static final int FOURIER_TIME = 11; // sec

    private List<Peak> peakList = new ArrayList<Peak>();
    private List<Peak> peakPackageList = new ArrayList<Peak>();
    private HashMap<Integer, Integer> output = new HashMap<Integer, Integer>();
    private boolean isPackageApproved = false;
    private boolean hasPackageBigSaccade = false;
    private int saccadeMaxDistancePoints;
    private int currentIndex = -1;
    private DataSeries inputData;
    private PeakDetector peakDetector;


    public SaccadeDetector(DataSeries inputData) {
        this.inputData = inputData;
        DataSeries derivativeRem = new FilterDerivativeRem(inputData);
       // derivativeRem = new FilterLowPass(new FilterDerivativeRem(inputData), 25.0);
        DataSeries derivative = new FilterDerivativeRem(new FilterDerivativeRem(inputData));
        peakDetector = new PeakDetector(derivativeRem);
        saccadeMaxDistancePoints = (int)(SACCADE_DISTANCE_MAX * derivativeRem.getFrequency());
    }


    private void detectSaccade(int index) {
        currentIndex = index;
        Peak peak = peakDetector.detect(index);
        if(peak != null) {
            addSaccade(peak);
           /* if(isSaccadePossible(peak)) {
                addSaccade(peak);
            } */
        }
    }


    private boolean isSaccadePossible(Peak peak) {
        DataSeries fourier =  Fourie.fftBackward( new FilterDerivative(inputData), peak.getEndIndex(), FOURIER_TIME);
        if(FourierAnalizer.hasAlfa(fourier)) {
            return false;
        }
        return true;
    }

    private void addSaccadeToPackage(Peak peak) {
        int saccadesDistance = 0;
        if(peakPackageList.size() > 0) {
            Peak peakPreviose = peakPackageList.get(peakPackageList.size() - 1);
            saccadesDistance = peak.getBeginIndex() - peakPreviose.getEndIndex();
        }

        if(saccadesDistance > saccadeMaxDistancePoints) {  // create new package
            peakPackageList = new ArrayList<Peak>();
            isPackageApproved = false;
            hasPackageBigSaccade = false;
        }

        peakPackageList.add(peak);

        if(!isPackageApproved) { // we are not sure that package has saccades and not sumValue
            if(peak.getPeakRatio() >=  2) {
                hasPackageBigSaccade = true;
            }

            if(hasPackageBigSaccade && peakPackageList.size() > 1) { // approve package as saccades package
                isPackageApproved = true;
                for(Peak peakPackage : peakPackageList) { // copy saccades from approved Package
                    addSaccade(peakPackage);
                }

            }
        }
        else { // we are  sure that package has saccades and not sumValue
            addSaccade(peak);
        }

    }

    private void addSaccade(Peak peak) {
        peakList.add(peak);
        for(int i = peak.getBeginIndex(); i <= peak.getEndIndex(); i++ ) {
            output.put(i, Math.abs(peak.getPeakValue()));
        }
    }

    @Override
    public int get(int index) {
        if(currentIndex < index){
            for(int i = currentIndex + 1; i <= index; i++) {
                detectSaccade(i);
            }
        }
        Integer value = output.get(index);
        if(value != null) {
            return value;
        }
        return 0;
    }

    public DataSeries getThresholds() {
        return peakDetector.getThresholds();
    }

    @Override
    public int size() {
        return inputData.size();
    }

    @Override
    public double getFrequency() {
        return inputData.getFrequency();
    }

    @Override
    public long getStartTime() {
        return inputData.getStartTime();
    }

    @Override
    public DataDimension getDataDimension() {
        return null;
    }
}


