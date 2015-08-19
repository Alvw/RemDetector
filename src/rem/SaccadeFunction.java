package rem;

import data.DataSeries;
import functions.Function;


/**
 * Based on Teager Kaiser Energy operator :
 * E_TKE =  x(n)*x(n) - x(n-1) * x(n+1)
 */

public class SaccadeFunction extends Function {
    int latencyTime = 100; // ms
    int saccadeHalfTime = 120; // ms
    int latencyMinPoints;
    int saccadeMinPoints;

    public SaccadeFunction(DataSeries inputData) {
        super(inputData);
        double frequency = 1 / inputData.getScaling().getSamplingInterval();
        latencyMinPoints = Math.round((float)(latencyTime *  frequency / 1000));
        saccadeMinPoints = Math.round((float)(saccadeHalfTime *  frequency / 1000));
    }

    @Override
    public int get(int index) {
        if (index < latencyMinPoints + saccadeMinPoints) {
            return 0;
        }
        double latency1 = 0;
        for(int i = index - latencyMinPoints-saccadeMinPoints; i < index-saccadeMinPoints; i ++) {
            latency1 += Math.abs(inputData.get(i));
        }
        latency1 = latency1 / latencyMinPoints;

        double latency2 = 0;
        for(int i = index + saccadeMinPoints; i < index + latencyMinPoints + saccadeMinPoints; i ++) {
            latency2 += Math.abs(inputData.get(i));
        }
        latency2 = latency2 / latencyMinPoints;

        double value1 = inputData.get(index) * inputData.get(index + 1);
        double value2 = inputData.get(index) * inputData.get(index - 1);
        double value = 0;
        if(value1 > 0 && value2 > 0) {
            value = value1 * value2;
            value =  Math.sqrt(value);
        }


     //  double energy = value -  4*(latency1 + latency2 )*(latency1 + latency2 );
       double energy = value -  16 * latency1 * latency2;


        if (energy > 0) {
            return (int) Math.sqrt(energy);
        }
        return 0;
    }

    @Override
    public int size() {
        return inputData.size() - saccadeMinPoints - latencyMinPoints -1;
    }

}


