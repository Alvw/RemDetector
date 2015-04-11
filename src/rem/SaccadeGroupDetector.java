package rem;

import data.DataDimension;
import data.DataSeries;
import dreamrec.FourierAnalizer;
import fft.Fourie;
import filters.FilterDerivative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SaccadeGroupDetector implements DataSeries {

    private static final int SACCADE_DISTANCE_MAX = 12; // sec

    private static final int FOURIER_TIME = 11; // sec

    private List<Saccade> saccadeList = new ArrayList<Saccade>();
    private List<Saccade> saccadePackageList = new ArrayList<Saccade>();
    private HashMap<Integer, Integer> output = new HashMap<Integer, Integer>();
    private boolean isPackageApproved = false;
    private boolean hasPackageBigSaccade = false;
    private int saccadeMaxDistancePoints;
    private int currentIndex = -1;
    private DataSeries inputData;
    private SaccadeDetector saccadeDetector;


    public SaccadeGroupDetector(DataSeries inputData) {
        this.inputData = inputData;

        saccadeDetector = new SaccadeDetector(inputData);
        saccadeMaxDistancePoints = (int)(SACCADE_DISTANCE_MAX * inputData.getFrequency());
    }


    private void detectSaccade(int index) {
        currentIndex = index;
        Saccade saccade = saccadeDetector.getNext();
        if(saccade != null) {
            addSaccade(saccade);
           /* if(isSaccadePossible(saccade)) {
                addSaccade(saccade);
            } */
        }
    }


    private boolean isSaccadePossible(Saccade saccade) {
        DataSeries fourier =  Fourie.fftBackward( new FilterDerivative(inputData), saccade.getEndIndex(), FOURIER_TIME);
        if(FourierAnalizer.hasAlfa(fourier)) {
            return false;
        }
        return true;
    }

    private void addSaccadeToPackage(Saccade saccade) {
        int saccadesDistance = 0;
        if(saccadePackageList.size() > 0) {
            Saccade saccadePreviose = saccadePackageList.get(saccadePackageList.size() - 1);
            saccadesDistance = saccade.getBeginIndex() - saccadePreviose.getEndIndex();
        }

        if(saccadesDistance > saccadeMaxDistancePoints) {  // create new package
            saccadePackageList = new ArrayList<Saccade>();
            isPackageApproved = false;
            hasPackageBigSaccade = false;
        }

        saccadePackageList.add(saccade);

        if(!isPackageApproved) { // we are not sure that package has saccades and not sumValue
            if(saccade.getPeakRatio() >=  2) {
                hasPackageBigSaccade = true;
            }

            if(hasPackageBigSaccade && saccadePackageList.size() > 1) { // approve package as saccades package
                isPackageApproved = true;
                for(Saccade saccadePackage : saccadePackageList) { // copy saccades from approved Package
                    addSaccade(saccadePackage);
                }

            }
        }
        else { // we are  sure that package has saccades and not sumValue
            addSaccade(saccade);
        }

    }

    private void addSaccade(Saccade saccade) {
        saccadeList.add(saccade);
        for(int i = saccade.getBeginIndex(); i <= saccade.getEndIndex(); i++ ) {
            output.put(i, Math.abs(saccade.getPeakValue()));
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
        return saccadeDetector.getThresholds();
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


