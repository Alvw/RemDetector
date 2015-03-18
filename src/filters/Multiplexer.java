package filters;
import data.DataSet;
import functions.Function;

/**
 *
 */
public class Multiplexer extends Function {
    protected final DataSet selectorData;
    public Multiplexer(DataSet inputData, DataSet selectorData) {
        super(inputData);
        this.selectorData = selectorData;
    }
    @Override
    public int get(int index) {
        if (selectorData.get(index) != 0) {
            return inputData.get(index);
        }
        else {
            return FALSE;
        }
    }
}