package dreamrec;

import data.DataDimension;
import data.DataList;
import data.DataSet;
import fft.Fourie;
import filters.FilterDerivative;
import filters.FilterDerivativeRem;
import filters.FilterLowPass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class SaccadeDetector implements DataSet{

    private static final int SACCADE_DISTANCE_MAX = 12; // sec

    private static final int FOURIER_TIME = 11; // sec

    private List<Peak> peakList = new ArrayList<Peak>();
    private List<Peak> peakPackageList = new ArrayList<Peak>();
    private HashMap<Integer, Integer> output = new HashMap<Integer, Integer>();
    private boolean isPackageApproved = false;
    private boolean hasPackageBigSaccade = false;
    private int saccadeMaxDistancePoints;
    private int currentIndex = -1;
    private DataSet inputData;
    private PeakDetector peakDetector;


    SaccadeDetector(DataSet inputData) {
        this.inputData = inputData;
        DataSet derivativeRem = new FilterDerivativeRem(inputData);
        DataSet derivative = new FilterDerivativeRem(new FilterDerivativeRem(inputData));
        peakDetector = new PeakDetector(derivative);
        saccadeMaxDistancePoints = (int)(SACCADE_DISTANCE_MAX * derivativeRem.getFrequency());
    }


    private void detectSaccade(int index) {
        currentIndex = index;
        Peak peak = peakDetector.detect(index);
        if(peak != null) {
            if(isSaccadePossible(peak)) {
                addSaccade(peak);
            }
        }
    }


    private boolean isSaccadePossible(Peak peak) {
        DataSet fourier =  Fourie.fftBackward( new FilterDerivative(inputData), peak.getEndIndex(), FOURIER_TIME);
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

    public DataSet getThresholds() {
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


