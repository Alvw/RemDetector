package com.dream.Filters;

import com.dream.Data.DataStream;
import com.dream.Graph.GraphsViewer;

/**
 *
 */
public class FilterFirst extends FilterBuffered {

    private GraphsViewer graphsViewer;
     private int offset = 640;
     int bufferSize = 100;

     public FilterFirst(DataStream inputData, GraphsViewer graphsViewer) {
         super(inputData);
         this.graphsViewer = graphsViewer;
     }

     // @Override
     protected int getData_(int index) {
         int offsetLevel = graphsViewer.getStartIndex() + offset;
         if(offsetLevel >= size())
         {
             offsetLevel = (size() - graphsViewer.getStartIndex())/2;
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

