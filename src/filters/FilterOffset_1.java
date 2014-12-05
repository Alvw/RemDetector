package filters;

import data.DataSet;
import graph.GraphsView;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 07.08.14
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class FilterOffset_1 extends Filter {
    private GraphsView graphsView;
    private int offset = 640;

    public FilterOffset_1(DataSet inputData, GraphsView graphsView) {
        super(inputData);
        this.graphsView = graphsView;
    }

    // @Override
    protected int getData(int index) {
        if (index >= size()- 1920) {
            return 0;
        }

        int offsetLevel = graphsView.getStartIndex() + offset;
        if(inputData.get(offsetLevel)== UNDEFINED ) {
            return UNDEFINED;
        }
        return inputData.get(index) - inputData.get(offsetLevel);
    }
}
