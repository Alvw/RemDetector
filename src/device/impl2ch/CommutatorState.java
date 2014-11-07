package device.impl2ch;

/**
 *
 */
public enum CommutatorState {
    INPUT(0),
    INPUT_SHORT(1),
    TEST_SIGNAL(5);

    private int registerBits;

    private CommutatorState(int registerBits) {
        this.registerBits = registerBits;
    }

    public int getRegisterBits(){
        return registerBits;
    }
}
