package filters;

import data.DataStream;
import graph.GraphsViewer;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 07.08.14
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class FilterOffset_1 extends Filter {
    private GraphsViewer graphsViewer;
    private int offset = 640;

    public FilterOffset_1(DataStream inputData, GraphsViewer graphsViewer) {
        super(inputData);
        this.graphsViewer = graphsViewer;
    }

    // @Override
    protected int getData(int index) {
        if (index >= size()- 1920) {
            return 0;
        }

        int offsetLevel = graphsViewer.getStartIndex() + offset;
        if(inputData.get(offsetLevel)== UNDEFINED ) {
            return UNDEFINED;
        }
        return inputData.get(index) - inputData.get(offsetLevel);
    }
}
