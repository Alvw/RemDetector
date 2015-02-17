package dreamrec;

import bdf.BdfConfig;
import bdf.BdfProvider;
import bdf.BdfRecordsJoiner;
import bdf.SignalConfig;
import data.DataDimension;
import data.DataList;
import data.DataSet;
import prefilters.PreFilter;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by mac on 17/02/15.
 */
public class RemDataStore  implements DataStoreListener {
    private DataStore dataStore;
    private RemChannels remChannels;
    private BdfProvider bdfProvider;
    private int movementLimit;

    private ArrayList<DataStoreListener> updateListeners = new ArrayList<DataStoreListener>();

    public RemDataStore(BdfProvider bdfProvider, RemChannels remChannels) throws ApplicationException {
        this.remChannels = remChannels;
        this.bdfProvider = bdfProvider;
        dataStore = new DataStore(bdfProvider);
        dataStore.addListener(this);
        int numberOfSignals = bdfProvider.getBdfConfig().getSignalConfigs().length;
        if(remChannels.getEog() >= numberOfSignals) {
            String msg = "EOG channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerX() >= numberOfSignals) {
            String msg = "AccelerometerX channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerY() >= numberOfSignals) {
            String msg = "AccelerometerY channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }
        if(remChannels.getAccelerometerZ() >= numberOfSignals) {
            String msg = "AccelerometerZ channel number should be less then total number of channels";
            throw new ApplicationException(msg);
        }

        movementLimit = (int)(0.15 / getAccelerometerXData().getDataDimension().getGain());
    }

    public void setChannelsMask(boolean[] channelsMask) {
        boolean[] resultingMask = new boolean[channelsMask.length];
        for(int i = 0; i < channelsMask.length; i++) {
            if(i == remChannels.getEog() || i == remChannels.getAccelerometerX() || i == remChannels.getAccelerometerY() || i == remChannels.getAccelerometerZ()) {
                resultingMask[i] = true;
            }
            else {
                resultingMask[i] = channelsMask[i];
            }
        }
        dataStore.setChannelsMask(resultingMask);
    }


   public void configure(RemConfigurator remConfigurator) throws ApplicationException {
       if (remConfigurator != null) {
           BdfConfig bdfConfig = bdfProvider.getBdfConfig();
           int numberOfRecordsToJoin = remConfigurator.getNumberOfRecordsToJoin(bdfConfig);
           BdfProvider bdfProviderNew = new BdfRecordsJoiner(bdfProvider, numberOfRecordsToJoin);
           PreFilter[] prefilters = remConfigurator.getPreFilters(bdfConfig, remChannels);
           dataStore = new DataStore(bdfProviderNew);
           dataStore.setPreFilters(prefilters);
           dataStore.addListener(this);
       }
   }


    public int getNumberOfChannels() {
        return dataStore.getNumberOfChannels();
    }

    public void addListener(DataStoreListener dataStoreListener) {
        updateListeners.add(dataStoreListener);
    }

    public DataList getChannelData(int channelNumber) {
        return dataStore.getChannelData(channelNumber);
    }

    private void fireDataUpdated() {
        for (DataStoreListener listener : updateListeners) {
            listener.onDataUpdate();
        }
    }


    public void setStartTime(long startTime) {
       dataStore.setStartTime(startTime);
    }


    @Override
    public void onDataUpdate() {
        fireDataUpdated();
    }

    public DataSet getEogData() {
        return dataStore.getSignalData(remChannels.getEog());
    }

    public DataSet getAccelerometerXData() {
        return dataStore.getSignalData(remChannels.getAccelerometerX());
    }

    public DataSet getAccelerometerYData() {
        return dataStore.getSignalData(remChannels.getAccelerometerY());
    }

    public DataSet getAccelerometerZData() {
        return dataStore.getSignalData(remChannels.getAccelerometerZ());
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

}
