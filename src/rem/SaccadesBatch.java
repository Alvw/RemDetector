package rem;

import java.util.ArrayList;
import java.util.List;

public class SaccadesBatch {
    private static final int MIN_NUMBER_OF_SACCADES = 3;
    private List<Saccade> saccadeList = new ArrayList<Saccade>();
    private int saccadeDistanceMaxPoints;
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

}
