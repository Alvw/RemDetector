package filters;

import data.DataDimension;
import data.DataSet;

public class FilterMovementTreshhold extends Filter{
    private DataSet accZ;
    private DataSet accY;
    int digitalLimit;
    public FilterMovementTreshhold(DataSet inputData, DataSet accY, DataSet accZ, double physLimit) {
        super(inputData);
        this.accY  = accY;
        this.accZ = accZ;
        digitalLimit = (int)(physLimit / inputData.getDataDimension().getGain());
    }

    @Override
    public int get(int index) {
        int step = 2;
        int dX, dY, dZ;
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        if (index > step) {
            for (int i = 0; i <= step; i++) {
                maxX = Math.max(maxX, inputData.get(index - i));
                minX = Math.min(minX, inputData.get(index - i));
                maxY = Math.max(maxY, accY.get(index - i));
                minY = Math.min(minY, accY.get(index - i));
                maxZ = Math.max(maxZ, accZ.get(index - i));
                minZ = Math.min(minZ, accZ.get(index - i));
            }
            dX = maxX - minX;
            dY = maxY - minY;
            dZ = maxZ - minZ;
        } else {
            dX = 0;
            dY = 0;
            dZ = 0;
        }

        int dXYZ = Math.abs(dX) + Math.abs(dY) + Math.abs(dZ);
        if(dXYZ >= digitalLimit) {
            return 0;
        }
        return 1;
    }

    @Override
    public DataDimension getDataDimension() {
        return new DataDimension();
    }
}
