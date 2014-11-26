package dreamrec;

public class RemConfig {
    private int accelerometerX;
    private int accelerometerY;
    private int accelerometerZ;
    private int eog;
    private double accelerometerRemFrequency;
    private double eogRemFrequency;

    public RemConfig(double eogRemFrequency , double accelerometerRemFrequency,
                     int eog, int accelerometerX, int accelerometerY, int accelerometerZ) {
        this.accelerometerX = accelerometerX;
        this.accelerometerY = accelerometerY;
        this.accelerometerZ = accelerometerZ;
        this.eog = eog;
        this.accelerometerRemFrequency = accelerometerRemFrequency;
        this.eogRemFrequency = eogRemFrequency;
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

    public double getAccelerometerRemFrequency() {
        return accelerometerRemFrequency;
    }

    public double getEogRemFrequency() {
        return eogRemFrequency;
    }
}
