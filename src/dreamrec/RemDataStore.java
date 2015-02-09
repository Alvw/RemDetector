package dreamrec;

import bdf.BdfProvider;
import data.DataDimension;
import data.DataSet;

public class RemDataStore extends DataStore {
    private RemChannels remChannels;
    private int movementLimit;

    public RemDataStore(BdfProvider bdfProvider, RemChannels remChannels) throws ApplicationException {
        super(bdfProvider);
        this.remChannels = remChannels;
        int numberOfSignals = bdfProvider.getBdfConfig().getSignalConfigs().length;
        if(remChannels.getEog() >= numberOfSignals) {
            String msg = "EOG rem channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerX() >= numberOfSignals) {
            String msg = "AccelerometerX rem channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerY() >= numberOfSignals) {
            String msg = "AccelerometerY rem channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerZ() >= numberOfSignals) {
            String msg = "AccelerometerZ rem channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        movementLimit = (int)(0.15 / getAccelerometerXData().getDataDimension().getGain());
    }


    /**
     * Определяем величину пропорциональную движению головы
     * (дельта между текущим и предыдущим значением сигналов акселерометра).
     * Суммируем амплитуды движений по трем осям.
     * За ноль принят шумовой уровень.
     */

    private int getAccMovement(int index) {
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
                maxX = Math.max(maxX, getAccelerometerXData().get(index - i));
                minX = Math.min(minX, getAccelerometerXData().get(index - i));
                maxY = Math.max(maxY, getAccelerometerYData().get(index - i));
                minY = Math.min(minY, getAccelerometerYData().get(index - i));
                maxZ = Math.max(maxZ, getAccelerometerZData().get(index - i));
                minZ = Math.min(minZ, getAccelerometerZData().get(index - i));
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
        return dXYZ;
    }

    public DataSet getAccMovement() {
        return new DataSet() {
            @Override
            public int size() {
                return getAccelerometerXData().size();
            }

            @Override
            public int get(int index) {
                return getAccMovement(index);
            }

            @Override
            public double getFrequency() {
                return getAccelerometerXData().getFrequency();
            }

            @Override
            public DataDimension getDataDimension() {
                return getAccelerometerXData().getDataDimension();
            }

            @Override
            public long getStartTime() {
                return getAccelerometerXData().getStartTime();
            }
        };
    }

    public DataSet getAccLimit() {
        return new DataSet() {
            @Override
            public int size() {
                return getAccelerometerXData().size();
            }

            @Override
            public int get(int index) {
                return movementLimit;
            }

            @Override
            public double getFrequency() {
                return getAccelerometerXData().getFrequency();
            }

            @Override
            public DataDimension getDataDimension() {
                return getAccelerometerXData().getDataDimension();
            }

            @Override
            public long getStartTime() {
                return getAccelerometerXData().getStartTime();
            }
        };
    }


    private boolean isMoved(int index) {
        if (getAccMovement(index) > movementLimit) {
            return true;
        }
        return false;
    }



    public DataSet getEogData() {
        return channelsList[remChannels.getEog()];
    }

    public DataSet getAccelerometerXData() {
        return channelsList[remChannels.getAccelerometerX()];
    }

    public DataSet getAccelerometerYData() {
        return channelsList[remChannels.getAccelerometerY()];
    }

    public DataSet getAccelerometerZData() {
        return channelsList[remChannels.getAccelerometerZ()];
    }

}
