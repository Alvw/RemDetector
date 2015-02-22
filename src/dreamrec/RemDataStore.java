package dreamrec;

import bdf.BdfConfig;
import bdf.BdfProvider;
import bdf.BdfRecordsJoiner;
import data.BufferedData;
import data.DataDimension;
import data.DataList;
import data.DataSet;
import filters.Filter;
import filters.FilterHiPass;
import prefilters.PreFilter;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by mac on 17/02/15.
 */
public class RemDataStore  implements DataStoreListener {

    private double accMovementLimit = 0.15;
    private double eogDerivativeLimit = 400;

    private DataStore dataStore;
    private RemChannels remChannels;
    private BdfProvider bdfProvider;

    private DataList eogFilteredData;
    private EogFilter eogFilter;

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
    }

    public void setChannelsMask(boolean[] channelsMask) throws ApplicationException {
        if((remChannels.getEog() < channelsMask.length) && channelsMask[remChannels.getEog()] == false) {
            String errorMsg = "EOG channel should be enable";
            throw new ApplicationException(errorMsg);
        }
        if((remChannels.getAccelerometerX() < channelsMask.length) && channelsMask[remChannels.getAccelerometerX()] == false) {
            String errorMsg = "AccelerometerX channel should be enable";
            throw new ApplicationException(errorMsg);
        }
        if((remChannels.getAccelerometerY() < channelsMask.length) && channelsMask[remChannels.getAccelerometerY()] == false) {
            String errorMsg = "AccelerometerY channel should be enable";
            throw new ApplicationException(errorMsg);
        }
        if((remChannels.getAccelerometerZ() < channelsMask.length) && channelsMask[remChannels.getAccelerometerZ()] == false) {
            String errorMsg = "AccelerometerZ channel should be enable";
            throw new ApplicationException(errorMsg);
        }

        dataStore.setChannelsMask(channelsMask);
    }


    public void configure(final RemConfigurator remConfigurator) throws ApplicationException {
        if (remConfigurator != null) {
            BdfConfig bdfConfig = bdfProvider.getBdfConfig();
            int numberOfRecordsToJoin = remConfigurator.getNumberOfRecordsToJoin(bdfConfig);
            BdfProvider bdfProviderNew = new BdfRecordsJoiner(bdfProvider, numberOfRecordsToJoin);
            PreFilter[] prefilters = remConfigurator.getPreFilters(bdfConfig, remChannels);
            int bufferSize = remConfigurator.getEogRemFrequency() * remConfigurator.getEogRemCutoffPeriod();
            if(bufferSize > 0) {
                eogFilteredData = new DataList();
                eogFilteredData.setFrequency(getEogFullData().getFrequency());
                eogFilteredData.setDataDimension(getEogFullData().getDataDimension());
                eogFilter = new EogFilter(bufferSize);
            }
            dataStore = new DataStore(bdfProviderNew);
            dataStore.setPreFilters(prefilters);
            dataStore.addListener(this);

        }
    }

    public double getAccMovementLimit() {
        // movementLimit = (int)(0.15 / getAccXData().getDataDimension().getGain());
        return accMovementLimit;
    }

    public double getEogDerivativeLimit() {
        return eogDerivativeLimit;
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

    private void updateEogFilteredData() {
        if( eogFilter != null) {
            while(eogFilteredData.size() < getEogFullData().size()) {
                eogFilteredData.add(eogFilter.getNext());
            }
            eogFilteredData.setStartTime(getEogFullData().getStartTime());
        }
    }


    @Override
    public void onDataUpdate() {
        updateEogFilteredData();
        fireDataUpdated();
    }

    public DataSet getEogFullData() {
        return dataStore.getSignalData(remChannels.getEog());
    }

    public DataSet getEogData() {
        if(eogFilteredData != null) {
            return eogFilteredData;
        }
        return getEogFullData();
    }

    public DataSet getAccXData() {
        return dataStore.getSignalData(remChannels.getAccelerometerX());
    }

    public DataSet getAccYData() {
        return dataStore.getSignalData(remChannels.getAccelerometerY());
    }

    public DataSet getAccZData() {
        return dataStore.getSignalData(remChannels.getAccelerometerZ());
    }

    public DataSet getAccMovementData() {
        return new Filter(getAccXData()) {
            /**
             * Определяем величину пропорциональную движению головы
             * (дельта между текущим и предыдущим значением сигналов акселерометра).
             * Суммируем амплитуды движений по трем осям.
             * За ноль принят шумовой уровень.
             */
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
                        maxX = Math.max(maxX, getAccXData().get(index - i));
                        minX = Math.min(minX, getAccXData().get(index - i));
                        maxY = Math.max(maxY, getAccYData().get(index - i));
                        minY = Math.min(minY, getAccYData().get(index - i));
                        maxZ = Math.max(maxZ, getAccZData().get(index - i));
                        minZ = Math.min(minZ, getAccZData().get(index - i));
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
        };
    }

    private class EogFilter  {
        private int index;
        private long sum;
        int bufferSize;

        public EogFilter(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        public int getNext() {
            if(bufferSize == 0) {
                return getEogFullData().get(index++);
            }
            if (index <= bufferSize) {
                sum += getEogFullData().get(index);
                return getEogFullData().get(index++) - (int) (sum / (index));
            }
            else {
                sum += getEogFullData().get(index) - getEogFullData().get(index - bufferSize - 1);
            }

            return getEogFullData().get(index++) - (int) (sum / (bufferSize+1));
        }
    }


}






