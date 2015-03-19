package dreamrec;

import bdf.BdfConfig;
import bdf.BdfProvider;
import bdf.BdfRecordsJoiner;
import data.*;
import filters.FilterDerivativeRem;
import functions.BooleanAND;
import functions.Composition;
import functions.Rising;
import functions.Trigger;
import prefilters.PreFilter;

import java.util.ArrayList;

/**
 * Created by mac on 17/02/15.
 */
public class RemDataStore  implements DataStoreListener {

    private double accMovementLimit = 0.15;
    private double eogRemDerivativeMax = 600;

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

            bdfProvider.removeBdfDataListener(dataStore);

            dataStore = new DataStore(bdfProviderNew);
            dataStore.setPreFilters(prefilters);
            dataStore.addListener(this);

            int bufferSize = (int)(getEogFullData().getFrequency() * remConfigurator.getEogRemCutoffPeriod());
            if(remConfigurator.getEogRemFrequency() > 0) {
                bufferSize = remConfigurator.getEogRemFrequency() * remConfigurator.getEogRemCutoffPeriod();
            }

            if(bufferSize > 0) {
                eogFilteredData = new DataList();
                eogFilteredData.setFrequency(getEogFullData().getFrequency());
                eogFilteredData.setDataDimension(getEogFullData().getDataDimension());
                eogFilter = new EogFilter(bufferSize);
            }
        }
    }

    public double getAccMovementLimit() {
        // movementLimit = (int)(0.15 / getAccXData().getDataDimension().getGain());
        return accMovementLimit;
    }

    public double getEogRemDerivativeMax() {
        return eogRemDerivativeMax;
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
        eogFilteredData.setStartTime(startTime);
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

    /**
     * Определяем величину пропорциональную движению головы
     * (дельта между max и min значением сигналов акселерометра на 3 точках).
     * Суммируем амплитуды движений по трем осям.
     * За ноль принят шумовой уровень.
     */
    public DataSet getAccMovementData() {
        Composition accMovement = new Composition();
        try {
            accMovement.add(new Rising(getAccXData()));
            accMovement.add(new Rising(getAccYData()));
            accMovement.add(new Rising(getAccZData()));
        } catch (ApplicationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        return accMovement;
    }

    private DataSet isNotMove() {
        return new Trigger(getAccMovementData(), accMovementLimit);
    }

    private DataSet isEogOk() {
        return new Trigger(new FilterDerivativeRem(getEogData()), eogRemDerivativeMax);
    }

public DataSet isSleep() {
        BooleanAND isSleep = new BooleanAND();
        try {
            FrequencyConverter isNotMove = new FrequencyConverterRuntime(isNotMove(), CompressionType.BOOLEAN);
            isNotMove.setFrequency(isEogOk().getFrequency());
            isSleep.add(isEogOk());
            isSleep.add(isNotMove);
            return isSleep;
        } catch (ApplicationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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






