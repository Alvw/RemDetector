package functions;

import data.DataDimension;
import data.DataSet;
import dreamrec.ApplicationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Композиция (сумма или вычитание) входных данных
 */
public class Composition implements DataSet {
    private List<DataSet> inputDataList = new ArrayList<DataSet>();
    private String errMsg = "It is only possible to make combinations with signals which: \n" +
            "- have the same startTime \n"+
            "- have the same samplerate \n"+
            "- have the same physical dimension (e.g uV) \n" +
            "- have the same sensitivity";

    public void add(DataSet inputData) throws ApplicationException {
        if(inputDataList.size() > 0) {
            DataDimension dataDimension1 = getDataDimension();
            DataDimension dataDimension2 = inputData.getDataDimension();
            if(getStartTime() != inputData.getStartTime()){
                System.out.println("start time ");
                //throw new ApplicationException(errMsg);
            }
            if(getFrequency() != inputData.getFrequency()){
                System.out.println(getFrequency()+" fr "+inputData.getFrequency());
                throw new ApplicationException(errMsg);
            }
            if(!dataDimension1.getPhysicalDimension().equals(dataDimension2.getPhysicalDimension())){
                throw new ApplicationException(errMsg);
            }
            if(dataDimension1.getGain() != dataDimension2.getGain()){
                throw new ApplicationException(errMsg);
            }
        }
        inputDataList.add(inputData);
    }

    public void subtract(DataSet inputData) throws ApplicationException{
        if(inputDataList.size() > 0) {
            DataDimension dataDimension1 = getDataDimension();
            DataDimension dataDimension2 = inputData.getDataDimension();
            if(getStartTime() != inputData.getStartTime()){
                throw new ApplicationException(errMsg);
            }
            if(getFrequency() != inputData.getFrequency()){
                throw new ApplicationException(errMsg);
            }
            if(!dataDimension1.getPhysicalDimension().equals(dataDimension2.getPhysicalDimension())){
                throw new ApplicationException(errMsg);
            }
            if(dataDimension1.getGain() != dataDimension2.getGain()){
                throw new ApplicationException(errMsg);
            }
        }
         inputDataList.add(new Inverter(inputData));
    }


    @Override
    public int get(int index) {
        int result = 0;
        for (int i = 0; i < inputDataList.size(); i++) {
            int value = 0;
            if(index < inputDataList.get(i).size()) {
                value = inputDataList.get(i).get(index);
            }
            if(value == DataSet.FALSE)  {
                return FALSE;
            }
            if(value == DataSet.TRUE || result == DataSet.TRUE) {
                result = DataSet.TRUE;
            }
            else {
                result += value;
            }
        }
        return result;
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
        if (inputDataList.size() > 0) {
            return inputDataList.get(0).getDataDimension();
        }
        return new DataDimension();
    }
}
