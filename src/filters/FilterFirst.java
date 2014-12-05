package filters;

import data.DataSet;
import graph.GraphsView;

/**
 *
 */
public class FilterFirst extends FilterBuffered {

    private GraphsView graphsView;
     private int offset = 640;
     int bufferSize = 100;

     public FilterFirst(DataSet inputData, GraphsView graphsView) {
         super(inputData);
         this.graphsView = graphsView;
     }

     // @Override
     protected int getData_(int index) {
         int offsetLevel = graphsView.getStartIndex() + offset;
         if(offsetLevel >= size())
         {
             offsetLevel = (size() - graphsView.getStartIndex())/2;
         }
         return inputData.get(index) - inputData.get(offsetLevel);
     }

     protected int getData(int index) {
        int sum = 0;
         if(index <= bufferSize)
         {
             return 0;
         }
        for (int i = (index - bufferSize); i < index; i++) {
            sum += inputData.get(i);
        }
        return inputData.get(index) - sum/(bufferSize);
     }
 }

