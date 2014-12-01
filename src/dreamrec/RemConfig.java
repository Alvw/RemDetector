package dreamrec;

public class RemConfig {
    private int accelerometerX;
    private int accelerometerY;
    private int accelerometerZ;
    private int eog;

    private static final String EOG = "EOG";
    private static final String ACCELEROMETER_X = "Accelerometer X";
    private static final String ACCELEROMETER_Y = "Accelerometer Y";
    private static final String ACCELEROMETER_Z = "Accelerometer Z";

    public RemConfig(int eog, int accelerometerX, int accelerometerY, int accelerometerZ) throws ApplicationException{
        init(eog, accelerometerX, accelerometerY, accelerometerZ);
    }

    public RemConfig(String[] signalsLabels) throws ApplicationException{
        int eogNumber = -1;
        int accelerometerXNumber = -1;
        int accelerometerYNumber = -1;
        int accelerometerZNumber = -1;
        for (int i = 0; i < signalsLabels.length; i++) {
            if (signalsLabels[i].equals(EOG)) {
                eogNumber = i;
            }
            if (signalsLabels[i].equals(ACCELEROMETER_X)) {
                accelerometerXNumber = i;
            }
            if (signalsLabels[i].equals(ACCELEROMETER_Y)) {
                accelerometerYNumber = i;
            }
            if (signalsLabels[i].equals(ACCELEROMETER_Z)) {
                accelerometerZNumber = i;
            }
        }
        init(eogNumber, accelerometerXNumber, accelerometerYNumber, accelerometerZNumber);

    }

    public static boolean[] isRemLabels(String[] labels) {
        boolean[] isRemLabels = new boolean[labels.length];
        for (int i = 0; i < labels.length; i++) {
            isRemLabels[i] = false;
            if (labels[i].equals(EOG) || labels[i].equals(ACCELEROMETER_X)  || labels[i].equals(ACCELEROMETER_Y)  || labels[i].equals(ACCELEROMETER_Z)){
                isRemLabels[i] = true;
            }
        }
        return isRemLabels;
    }

    private void init(int eog, int accelerometerX, int accelerometerY, int accelerometerZ) throws ApplicationException{
        if(accelerometerX < 0) {
            throw new ApplicationException("AccelerometerX channel number is not specified");
        }
        if(accelerometerY < 0) {
            throw new ApplicationException("AccelerometerY channel number is not specified");
        }
        if(accelerometerZ < 0) {
            throw new ApplicationException("AccelerometerZ channel number is not specified");
        }
        if(eog < 0) {
            throw new ApplicationException("EOG channel number is not specified");
        }

        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
        this.eog = eog;
    }



    public int getAccelerometerX() {
        return accelerometerX;
    }

    public int getAccelerometerY() {
        return accelerometerY;
    }

    public int getAccelerometerZ() {
        return accelerometerZ;
    }

    public int getEog() {
        return eog;
    }

}
