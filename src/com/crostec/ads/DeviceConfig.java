package com.crostec.ads;

/**
 *
 */
public class DeviceConfig {
    private String comPortName;
    private int comPortSpeed;

    public int getComPortSpeed() {
        return comPortSpeed;
    }

    public void setComPortSpeed(int comPortSpeed) {
        this.comPortSpeed = comPortSpeed;
    }

    public String getComPortName() {
        return comPortName;
    }

    public void setComPortName(String comPortName) {
        this.comPortName = comPortName;
    }
}
