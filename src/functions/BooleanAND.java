package functions;

import data.DataDimension;
import data.DataSet;
import dreamrec.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class BooleanAND implements DataSet {
        private List<DataSet> inputDataList = new ArrayList<DataSet>();
        private String errMsg = "It is only possible to make conjunction with signals which: \n" +
                "- have the same startTime \n"+
                "- have the same samplerate \n";


    public void add(DataSet inputData) throws ApplicationException {
        if(inputDataList.size() > 0) {
            if(getStartTime() != inputData.getStartTime()){
                throw new ApplicationException(errMsg);
            }
            if(getFrequency() != inputData.getFrequency()){
                throw new ApplicationException(errMsg);
            }
        }
        inputDataList.add(inputData);
    }

    @Override
    public int get(int index) {
        for (int i = 0; i < inputDataList.size(); i++) {
            if(index < inputDataList.get(i).size()) {
                if(inputDataList.get(i).get(index) == 0) {
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
    public double getFrequency() {
        if (inputDataList.size() > 0) {
            return inputDataList.get(0).getFrequency();
        }
        return 0;
    }

    @Override
    public long getStartTime() {
        if (inputDataList.size() > 0) {
            return inputDataList.get(0).getStartTime();
        }
        return 0;
    }

    @Override
    public DataDimension getDataDimension() {
        return null;
    }
}
