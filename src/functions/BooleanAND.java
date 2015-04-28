package functions;

import data.DataSeries;
import data.Scaling;
import data.ScalingImpl;
import dreamrec.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class BooleanAND implements DataSeries {
    private List<DataSeries> inputDataList = new ArrayList<DataSeries>();
    private ScalingImpl scaling = new ScalingImpl();
    private String errMsg = "It is only possible to make conjunction with signals which: \n" +
            "- have the same startTime \n" +
            "- have the same samplerate \n";


    public void add(DataSeries inputData) throws ApplicationException {
        if (inputDataList.size() == 0 && inputData.getScaling() != null) {
            scaling = new ScalingImpl();
            scaling.setSamplingInterval(inputData.getScaling().getSamplingInterval());
            scaling.setStart(inputData.getScaling().getStart());
            scaling.setTimeSeries(inputData.getScaling().isTimeSeries());
        }
        if (!isCompatible(getScaling(), inputData.getScaling())) {
            throw new ApplicationException(errMsg);
        }
        inputDataList.add(inputData);
    }

    private boolean isCompatible(Scaling scaling1, Scaling scaling2) {
        if (scaling1 != null && scaling2 == null) {
            return false;
        }
        if (scaling1 == null && scaling2 != null) {
            return false;
        }
        if (scaling1 != null && scaling2 != null) {
            if (scaling1.getSamplingInterval() != scaling2.getSamplingInterval()) return false;
            if (scaling1.getStart() != scaling2.getStart()) return false;
            if (scaling1.isTimeSeries() != scaling2.isTimeSeries()) return false;
        }
        return true;
    }

    @Override
    public int get(int index) {
        for (int i = 0; i < inputDataList.size(); i++) {
            if (index < inputDataList.get(i).size()) {
                if (inputDataList.get(i).get(index) == 0) {
                    return 0;
                }
            }
        }
        return 1;
    }

    @Override
    public int size() {
        if (inputDataList.size() > 0) {
            return inputDataList.get(0).size();
        }
        return 0;
    }

    @Override
    public Scaling getScaling() {
        return scaling;
    }
}
