package device;

/**
 * 
 */
public enum Divider {
    D1(1),
    D2(2),
    D5(5),
    D10(10),
    D25(25),
    D50(50);

    private int value;

    private Divider(int value) {
        this.value = value;
    }

    public static Divider valueOf(int value) throws IllegalArgumentException {
        for (Divider divider : Divider.values()) {
            if (divider.getValue() == value) {
                return divider;
            }
        }
        String msg = "Invalid Divider value";
        throw new IllegalArgumentException(msg);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString(){
        return new Integer(value).toString();
    }
}
