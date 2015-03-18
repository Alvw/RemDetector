package filters;

import data.DataSet;
import functions.Function;
import graph.GraphViewer;

/**
 * Created with IntelliJ IDEA.
 * User: GENA
 * Date: 07.08.14
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class FilterOffset_1 extends Function {
    private GraphViewer graphViewer;
    private int offset = 640;

    public FilterOffset_1(DataSet inputData, GraphViewer graphViewer) {
        super(inputData);
        this.graphViewer = graphViewer;
    }

    // @Override
    public int get(int index) {
        if (index >= size()- 1920) {
            return 0;
        }

        int offsetLevel = 0; //graphViewer.getStartIndex() + offset;
        if(inputData.get(offsetLevel)== FALSE) {
            return FALSE;
        }
        return inputData.get(index) - inputData.get(offsetLevel);
    }
}
