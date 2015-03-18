package tmp;

import data.DataDimension;
import data.DataList;
import data.DataSet;
import filters.*;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 10.05.14
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class ApparatModel {

    private static final int ACC_FREQUENCY = 10;
    private int frequency = 50;
    private int DERIVATIVE_MAX = 5000;

    private DataList chanel_1_data = new DataList();   //list with prefiltered incoming data of eye movements
    private DataList chanel_2_data = new DataList();   //list with prefiltered incoming chanel2 data
    private DataList acc_1_data = new DataList();   //list with accelerometer 1 chanel data
    private DataList acc_2_data = new DataList();   //list with accelerometer 2 chanel data
    private DataList acc_3_data = new DataList();   //list with accelerometer 3 chanel data

    private DataList sleep_patterns = new DataList();
    private DataList spindle_data = new DataList();
    private SaccadeDetector saccadeDetector = new SaccadeDetector(chanel_1_data);
    private SpindleDetector spindleDetector = new SpindleDetector(chanel_1_data);


    private long startTime; //time when data recording was started

    private int movementLimit = 2000;
    private final double MOVEMENT_LIMIT_CHANGE = 1.05;

    private final int FALLING_ASLEEP_TIME = 30; // seconds
    private int sleepTimer = 0;

    private final int SIN_90 = 1800 / 4;  // if (F(X,Y) = 4) arc_F(X,Y) = 180 Grad
    private final int ACC_X_NULL = -1088;
    private final int ACC_Y_NULL = 1630;
    private final int ACC_Z_NULL = 4500;
    int Z_mod = 90;

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void movementLimitUp() {
        movementLimit *= MOVEMENT_LIMIT_CHANGE;
        sleepTimer = 0;
        for (int i = 0; i < getDataSize(); i++) {
        }
    }

    public void movementLimitDown() {
        movementLimit /= MOVEMENT_LIMIT_CHANGE;
        sleepTimer = 0;
        for (int i = 0; i < getDataSize(); i++) {
        }
    }

    private boolean isStand(int index) {
        if (getAccPosition(index) == DataSet.STAND) {
            return true;
        }
        return false;
    }

    private boolean isSleep(int index) {
//        if(index < 18000) {   //выкидиваем первые полчаса от начала записи
//            return false;
//        }
        if (isStand(index)) {
            sleepTimer = FALLING_ASLEEP_TIME * frequency;
        }
        if (isMoved(index)) {
            sleepTimer = Math.max(sleepTimer, FALLING_ASLEEP_TIME * frequency);
        }

        boolean isSleep = true;

        if ((sleepTimer > 0)) {
            isSleep = false;
            sleepTimer--;
        }

        return isSleep;
    }



    /**
     * Определяем величину пропорциональную движению головы
     * (дельта между текущим и предыдущим значением сигналов акселерометра).
     * Суммируем амплитуды движений по трем осям.
     * За ноль принят шумовой уровень.
     */

    private int getAccMovement(int index) {
        int step = 2;
        int dX, dY, dZ;
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        if (index > step) {
            for (int i = 0; i <= step; i++) {
                maxX = Math.max(maxX, getNormalizedDataAcc1(index - i));
                minX = Math.min(minX, getNormalizedDataAcc1(index - i));
                maxY = Math.max(maxY, getNormalizedDataAcc2(index - i));
                minY = Math.min(minY, getNormalizedDataAcc2(index - i));
                maxZ = Math.max(maxZ, getNormalizedDataAcc3(index - i));
                minZ = Math.min(minZ, getNormalizedDataAcc3(index - i));
            }
            dX = maxX - minX;
            dY = maxY - minY;
            dZ = maxZ - minZ;
        } else {
            dX = 0;
            dY = 0;
            dZ = 0;
        }

        int dXYZ = Math.abs(dX) + Math.abs(dY) + Math.abs(dZ);
        return dXYZ;
    }


    public DataSet getSleepStream() {
        return sleep_patterns;
    }

    public DataSet getSpindleStream() {
        return spindle_data;
    }

    public DataSet getAccMovementStream() {
        return new DataSetAdapter() {
            @Override
            protected int getData(int index) {
                return getAccMovement(index);
            }
        };
    }


    public DataSet getAccPositionStream() {
        return new DataSetAdapter() {
            @Override
            protected int getData(int index) {
                return getAccPosition(index);
            }
        };
    }

    public DataSet getNotSleepEventsStream() {
        return new DataSetAdapter() {
            @Override
            protected int getData(int index) {
                return sleep_patterns.get(index);
            }
        };
    }


    private int getAccPosition(int index) {
        final int DATA_SIN_90 = 16000;

        final int DATA_SIN_45 = DATA_SIN_90 * 3363 / 4756; // sin(45) = sqrt(2)/2 ~= 3363/4756

        final int X_data_mod = DATA_SIN_90, Y_data_mod = DATA_SIN_90, Z_data_mod = DATA_SIN_90;
        final int X_mod = SIN_90, Y_mod = SIN_90;


        int XY_angle;
        int data_X = getNormalizedDataAcc1(index);
        int data_Y = getNormalizedDataAcc2(index);
        int data_Z = getNormalizedDataAcc3(index);

        if (data_Z > DATA_SIN_45) {   // Если человек не лежит
            return DataSet.STAND;
        }

        double Z = (double) data_Z / Z_data_mod;

        double ZZ = Z * Z;
        double sec_Z = 1 + ZZ * 0.43 + ZZ * ZZ * 0.77;

        double double_X = ((double) data_X / X_data_mod) * sec_Z;
        double double_Y = ((double) data_Y / Y_data_mod) * sec_Z;
        int X = (int) (double_X * X_mod);
        int Y = (int) (double_Y * Y_mod);

        XY_angle = angle(X, Y);

        return XY_angle;
    }

    private int angle(int X, int Y) {
        int XY_angle = 0;
        // XY_angle =  1 + sin(x) - cos(x); if (X >= 0 && Y >=0)
        // XY_angle =  3 - sin(x) - cos(x); if (X >= 0 && Y < 0)
        // XY_angle = -1 + sin(x) + cos(x); if (X <  0 && Y >=0)
        // XY_angle = -3 - sin(x) + cos(x); if (X <  0 && Y < 0)

        if (X >= 0 && Y >= 0) {
            XY_angle = SIN_90 + X - Y;
        } else if (X >= 0 && Y < 0) {
            XY_angle = 3 * SIN_90 - X - Y;
        } else if (X < 0 && Y >= 0) {
            XY_angle = -SIN_90 + X + Y;
        } else if (X < 0 && Y < 0) {
            XY_angle = -3 * SIN_90 - X + Y;
        }

        return XY_angle / 10;
    }


    private boolean isMoved(int index) {
        if (getAccMovement(index) > movementLimit) {
            return true;
        }
        return false;
    }


    public int getAccDivider() {
        return frequency / ACC_FREQUENCY;
    }

    private int getNormalizedDataAcc1(int index) {
        int accIndex = index / getAccDivider();
        return (acc_1_data.get(accIndex) - ACC_X_NULL);
    }


    private int getNormalizedDataAcc2(int index) {
        int accIndex = index / getAccDivider();
        return -(acc_2_data.get(accIndex) - ACC_Y_NULL);
    }


    private int getNormalizedDataAcc3(int index) {
        int accIndex = index / getAccDivider();
        return -(acc_3_data.get(accIndex) + ACC_Z_NULL);
    }


    public int getDataSize() {
        int accDivider = getAccDivider();
        int size = chanel_1_data.size();
        size = Math.min(size, chanel_2_data.size());
        size = Math.min(size, acc_1_data.size() * accDivider);
        size = Math.min(size, acc_2_data.size() * accDivider);
        size = Math.min(size, acc_3_data.size() * accDivider);

        return size;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }




    public DataSet getCh1DataStream_() {
        return new DataSetAdapter() {
            int thresholdIndex = -10000;
            int latency = 50;
            @Override
            protected int getData(int index) {
                int derivative = 0;
                if(index > 0) {
                    derivative = Math.abs(chanel_1_data.get(index) - chanel_1_data.get(index - 1));
                }
                if(derivative > DERIVATIVE_MAX) {
                    thresholdIndex = index;
                }
                if((index-thresholdIndex < latency) && (index >= thresholdIndex)) {
                    return FALSE;
                }

                return chanel_1_data.get(index);
            }
        };
    }


    public DataSet getCh1DataStream() {
        return chanel_1_data;
    }

    public DataSet getCh2DataStream() {
        return chanel_2_data;
    }

    public DataSet getAcc1DataStream() {
       return new DataSetAdapter() {
            @Override
            protected int getData(int index) {
                 int accIndex = index / getAccDivider();
                return acc_1_data.get(accIndex);
            }
        };
    }

    public DataSet getAcc2DataStream() {
       return new DataSetAdapter() {
            @Override
            protected int getData(int index) {
                 int accIndex = index / getAccDivider();
                return acc_2_data.get(accIndex);
            }
        };
    }

    public DataSet getAcc3DataStream() {
            return new DataSetAdapter() {
            @Override
            protected int getData(int index) {
                 int accIndex = index / getAccDivider();
                return acc_3_data.get(accIndex);
            }
        };
    }


    private void addData(int data, DataList dataStore) {
        dataStore.add(data);
    }

    private void addData_(int data, DataList dataStore) {
        int size = getDataSize();
        dataStore.add(data);
        int sizeNew = getDataSize();
        if (sizeNew > size) {
            sleep_patterns.add(0);
            if (saccadeDetector.isSaccadeDetected(sizeNew - 1)) {
                sleep_patterns.set(saccadeDetector.getSaccadeIndex(), saccadeDetector.getSaccadeValue());
            }
            spindle_data.add(0);
            int spindleIndex = Math.max(0, sizeNew - 36);
            if (spindleDetector.isSpindleDetected(spindleIndex)) {
                int value = spindleDetector.getSpindleValue();
                for(int i = spindleDetector.getSpindleBeginIndex(); i <= spindleDetector.getSpindleEndIndex(); i++)
                    spindle_data.set(i, value);
            }

      /*     if (getAccPosition(sizeNew - 1) == DataStream.STAND) {  // person is standing
                sleep_patterns.add(DataStream.STAND);
            } else if (isMoved(sizeNew - 1)) { // person is moving
                sleep_patterns.add(DataStream.MOVE);
            }
            else {
               sleep_patterns.add(0);
               if  (saccadeDetector.isSaccadeDetected(sizeNew - 1)) {
                   int saccadeIndex = saccadeDetector.getSaccadeBeginIndex();
                   sleep_patterns.set(saccadeIndex, saccadeDetector.getSaccadeValue());
               }
            } */
        }
    }

    public void addCh1Data(int data) {
        addData(data, chanel_1_data);
    }

    public void addCh2Data(int data) {
        addData(data, chanel_2_data);
    }

    public void addAcc1Data(int data) {
        addData(data, acc_1_data);
    }


    public void addAcc2Data(int data) {
        addData(data, acc_2_data);

    }

    public void addAcc3Data(int data) {
        addData(data, acc_3_data);
    }


    abstract class DataSetAdapter implements DataSet {
        protected abstract int getData(int index);

        public final int get(int index) {
            checkIndexBounds(index);
            return getData(index);
        }


        private void checkIndexBounds(int index) {
            if (index > size() || index < 0) {
                throw new IndexOutOfBoundsException("index:  " + index + ", available:  " + size());
            }
        }

        @Override
        public int size() {
            return getDataSize();
        }

        @Override
        public double getFrequency() {
            return 0;
        }

        @Override
        public DataDimension getDataDimension() {
            return new DataDimension();
        }

        @Override
        public long getStartTime() {
            return getStartTime();
        }
    }


    class SpindleDetector {
        private int THRESHOLD_PERIOD_MSEC = 500;
        private int N = 4; // Threshold to noise ratio
        private int MAX_RATIO = 5; // ratio to calculate max saccade amplitude on the base of velocityThreshold
        private int SPINDLE_WIDTH_MIN_MSEC = 500;
        private int SPINDLE_WIDTH_MAX_MSEC = 10000;

        private int SPINDLE_WIDTH_MIN_POINTS = (SPINDLE_WIDTH_MIN_MSEC * frequency) / 1000;
        private int SPINDLE_WIDTH_MAX_POINTS = (SPINDLE_WIDTH_MAX_MSEC * frequency) / 1000;
        private int THRESHOLD_PERIOD_POINTS = (THRESHOLD_PERIOD_MSEC * frequency) / 1000;
        private int THRESHOLD_SHIFT_POINTS = 25;

        private int lastThresholdIndex = - 2*THRESHOLD_PERIOD_POINTS;

        private DataSet inputData;
        private DataSet alfaData;
        private DataSet thresholdData;
        private DataSet thresholdData1;

        private int spindleBeginIndex = 0;
        private int spindleEndIndex = 0;
        private int spindleValue = 0;
        private int threshold = 0;

        private boolean isUnderDetection = false;

        SpindleDetector(DataSet inputData) {
            this.inputData = inputData;
            alfaData = new FilterAlfa(inputData);
            thresholdData = new FilterThresholdAvg(alfaData, THRESHOLD_PERIOD_POINTS, THRESHOLD_SHIFT_POINTS);
            thresholdData1 = new FilterThresholdAvg(alfaData, THRESHOLD_PERIOD_POINTS*2, THRESHOLD_SHIFT_POINTS);
        }

        private void setThresholds(int index) {
            if(!isUnderDetection) {
                threshold =  Math.min(thresholdData.get(index), thresholdData1.get(index));
            }

        }

        private void resetSpindle() {
            spindleBeginIndex = 0;
            spindleEndIndex = 0;
            spindleValue = 0;
            isUnderDetection = false;
        }

        public boolean isSpindleDetected(int index) {
            int alfa = alfaData.get(index);
            setThresholds(index);

            if ( alfa > threshold) {
                if(!isUnderDetection) {   // spindle begins
                    resetSpindle();
                    isUnderDetection = true;
                    spindleBeginIndex = index;
                }
                else {      // spindle continues
                    int alfaBefore = alfaData.get(index - 1);
                    if(alfa < alfaBefore) {
                        if(spindleValue == 0) {
                           spindleValue = alfaBefore;
                        }
                    }
                }

                if(alfa > MAX_RATIO * threshold) {
                    resetSpindle();
                    return false;
                }
            }
            else {
                if(isUnderDetection) {     // spindle finishes
                    spindleEndIndex = index - 1;
                    int spindleWidth = spindleEndIndex - spindleBeginIndex + 1;
                   if(spindleWidth < SPINDLE_WIDTH_MIN_POINTS || spindleWidth > SPINDLE_WIDTH_MAX_POINTS ) {
                        resetSpindle();
                        return false;
                    }
                    lastThresholdIndex = spindleEndIndex;
                    isUnderDetection = false;
                    return true;
                }
            }
            return false;
        }

        int getSpindleBeginIndex() {
            return spindleBeginIndex;
        }

        int getSpindleEndIndex() {
            return spindleEndIndex;
        }

        int getSpindleValue() {
            return spindleValue;
        }
    }



    /**
     * Saccade (step):
     * 1) MAX_LEVEL > abs(derivation) > SACCADE_LEVEL
     * 2) derivation don't change sign
     * 3) saccade duration > 40 msec (SACCADE_WIDTH_MIN_MSEC)
     * 4) before and after saccade eyes are in rest (when abs(derivation) < NOISE_LEVEL) > 100 msec (LATENCY_PERIOD_MSEC)
     */
    class SaccadeDetector {
        private int THRESHOLD_PERIOD_MSEC = 200;
        private int N = 8; // Threshold to noise ratio
        private int MAX_RATIO = 5; // ratio to calculate max saccade amplitude on the base of velocityThreshold
        private int SACCADE_WIDTH_MIN_MSEC = 40;
        private int SACCADE_WIDTH_MAX_MSEC = 200;

        private int SACCADE_WIDTH_MIN_POINTS = (SACCADE_WIDTH_MIN_MSEC * frequency) / 1000;
        private int SACCADE_WIDTH_MAX_POINTS = (SACCADE_WIDTH_MAX_MSEC * frequency) / 1000;
        private int THRESHOLD_PERIOD_POINTS = (THRESHOLD_PERIOD_MSEC * frequency) / 1000;

        private int lastThresholdIndex = - 2*THRESHOLD_PERIOD_POINTS;

        private DataSet inputData;
        private DataSet velocityData;
        private DataSet accelerationData;
        private DataSet velocityThresholdData;
        private DataSet accelerationThresholdData;

        private int saccadeBeginIndex = 0;
        private int saccadePeakIndex = 0;
        private int saccadeEndIndex = 0;
        private int saccadePeakVelocity = 0;
        private int saccadeSign = 0;
        private int velocityThreshold = 0;
        private int accelerationThreshold = 0;

        private boolean isUnderDetection = false;

        SaccadeDetector(DataSet inputData) {
            this.inputData = inputData;
            velocityData = new FilterDerivative(inputData);
            accelerationData =  new FilterDerivative_N(inputData, 1);
            velocityThresholdData = new FilterThresholdAvg(velocityData, THRESHOLD_PERIOD_POINTS);
            accelerationThresholdData = new FilterThresholdAvg(accelerationData, THRESHOLD_PERIOD_POINTS);

        }

        private void setThresholds(int index) {
            if(!isUnderDetection && (index - lastThresholdIndex > THRESHOLD_PERIOD_POINTS+2)) {
                velocityThreshold = N * velocityThresholdData.get(index);
                accelerationThreshold = N * accelerationThresholdData.get(index);
            }

        }

        private void resetSaccade() {
            saccadeBeginIndex = 0;
            saccadePeakIndex = 0;
            saccadeEndIndex = 0;
            saccadePeakVelocity = 0;
            saccadeSign = 0;
            isUnderDetection = false;
        }

        public boolean isSaccadeDetected(int index) {
            int velocity = velocityData.get(index);
            int acceleration = accelerationData.get(index);
            setThresholds(index);
            if ((Math.abs(velocity) > velocityThreshold)) {
                if(!isUnderDetection) {   // saccade begins
                    resetSaccade();
                    isUnderDetection = true;
                    saccadeBeginIndex = index;
                    saccadeSign = getSign(velocity);
                }
                else {      // saccade continues
                    int velocityBefore = velocityData.get(index - 1);
                    if(Math.abs(velocity) < Math.abs(velocityBefore)) {   // velocity should form the bell with single peak
                        if(saccadePeakIndex == 0) {
                            saccadePeakIndex = index - 1;
                            saccadePeakVelocity = velocityBefore;
                        }
                    }
                }

                if((Math.abs(velocity) > MAX_RATIO * velocityThreshold) || !isEqualSign(velocity, saccadeSign)) {
                    resetSaccade();
                    return false;
                }
            }
            else {
                if(isUnderDetection) {     // saccade finishes
                    saccadeEndIndex = index - 1;
                    int saccadeWidth = saccadeEndIndex - saccadeBeginIndex + 1;
                    if(saccadeWidth < SACCADE_WIDTH_MIN_POINTS || saccadeWidth > SACCADE_WIDTH_MAX_POINTS ) {
                        resetSaccade();
                        return false;
                    }
                    lastThresholdIndex = saccadeEndIndex;
                    isUnderDetection = false;
                    if(saccadePeakIndex == 0) {
                        saccadePeakIndex = saccadeEndIndex;
                        saccadePeakVelocity = velocityData.get(saccadePeakIndex);
                    }
                    // System.out.println("begin: "+saccadeBeginIndex + " end: "+saccadeEndIndex);
                    return true;
                }
            }
            return false;
        }


        public int getSaccadeIndex() {
            return saccadeEndIndex;
        }

        public int getSaccadeValue() {
            return saccadePeakVelocity;
        }


        private int getSign(int a) {
            if (a >= 0) {
                return 1;
            }
            return -1;
        }

        protected boolean isEqualSign(int a, int b) {
            if ((a >= 0) && (b >= 0)) {
                return true;
            }

            if ((a <= 0) && (b <= 0)) {
                return true;
            }

            return false;
        }
    }
}

