package functions;

import data.DataSeries;
import data.Scaling;
import data.ScalingImpl;
import dreamrec.ApplicationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Композиция (сумма или вычитание) входных данных
 */
public class Composition implements DataSeries {
    private List<DataSeries> inputDataList = new ArrayList<DataSeries>();
    private Scaling scaling;
    private String errMsg = "It is only possible to make combinations with signals which: \n" +
            "- have the same startTime \n"+
            "- have the same samplerate \n"+
            "- have the same physical dimension (e.g uV) \n" +
            "- have the same sensitivity";

    public void add(DataSeries inputData) throws ApplicationException {
        if(inputDataList.size() == 0 && inputData.getScaling() != null) {
            scaling = new ScalingImpl(inputData.getScaling());

        }
        if(! ScalingImpl.isCompatible(getScaling(), inputData.getScaling())){
            throw new ApplicationException(errMsg);
        }
        inputDataList.add(inputData);
    }

    public void subtract(DataSeries inputData) throws ApplicationException{
        add(new Inverter(inputData));
    }


    @Override
    public int get(int index) {
        int result = 0;
        for (int i = 0; i < inputDataList.size(); i++) {
            int value = 0;
            if(index < inputDataList.get(i).size()) {
                value = inputDataList.get(i).get(index);
            }
            result += value;
        }
        return result;
    }

    @Override
    public int size() {
        int size = 0;
        for(DataSeries dataSeries : inputDataList) {
            size = Math.max(size, dataSeries.size());
        }
        return size;
    }

    @Override
    public Scaling getScaling() {
        return scaling;
    }
}
