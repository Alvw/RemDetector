package rem;

import java.util.ArrayList;
import java.util.List;

public class SaccadesBatch {
    private static final int MIN_NUMBER_OF_SACCADES = 3;
    private List<Saccade> saccadeList = new ArrayList<Saccade>();
    private int saccadeDistanceMaxPoints;
    private boolean hasBigSaccade = false;
    private boolean isApproved = false;

    public SaccadesBatch(Saccade saccade, int saccadeDistanceMaxPoints) {
        saccadeList.add(saccade);
        this.saccadeDistanceMaxPoints = saccadeDistanceMaxPoints;
    }

    public boolean addSaccade(Saccade saccade) {
        Saccade lastSaccade = saccadeList.get(saccadeList.size() - 1);
        if(saccade.getBeginIndex() - lastSaccade.getEndIndex() < saccadeDistanceMaxPoints) {
            saccadeList.add(saccade);
            if (getNumberOfSaccades() >= MIN_NUMBER_OF_SACCADES){
                isApproved = true;
            }
            return true;
        }
        return false;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public int getNumberOfSaccades() {
        return saccadeList.size();
    }

    public Saccade getSaccade(int saccadeNumber) {
        return saccadeList.get(saccadeNumber);
    }

    private void addSaccadeToPackage(Saccade saccade) {
        int saccadesDistance = 0;
        if(saccadeList.size() > 0) {
            Saccade saccadePreviose = saccadeList.get(saccadeList.size() - 1);
            saccadesDistance = saccade.getBeginIndex() - saccadePreviose.getEndIndex();
        }

        if(saccadesDistance > saccadeDistanceMaxPoints) {  // create new package
            saccadeList = new ArrayList<Saccade>();
            isApproved = false;
            hasBigSaccade = false;
        }

        saccadeList.add(saccade);

        if(!isApproved) { // we are not sure that package has saccades and not sumValue
            if(saccade.getPeakToThresholdRatio() >=  2) {
                hasBigSaccade = true;
            }

            if(hasBigSaccade && saccadeList.size() > 1) { // approve package as saccades package
                isApproved = true;
            }
        }
        else { // we are  sure that package has saccades and not sumValue
            addSaccade(saccade);
        }
    }
}
