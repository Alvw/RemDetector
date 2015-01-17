package dreamrec;

import bdf.BdfProvider;
import data.DataSet;

public class RemDataStore extends DataStore {
    private RemChannels remChannels;

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
