package device.implementation.impl2ch;


public class ComPortParams {

    private int speed;
    private int datatBits;
    private int stopBits;
    private int parity;

    public ComPortParams(int speed, int datatBits, int stopBits, int parity) {
        this.speed = speed;
        this.datatBits = datatBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDatatBits() {
        return datatBits;
    }

    public void setDatatBits(int datatBits) {
        this.datatBits = datatBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }
}
