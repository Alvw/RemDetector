package device.ads2ch_v1;

import bdf.BdfListener;
import device.general.Ads;
import dreamrec.ApplicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 06/03/15.
 */
public class AdsCh2V1  extends Ads {

    private List<Byte> pingCommand = new ArrayList<Byte>();
    private Timer pingTimer;

    public AdsCh2V1() {
        super(new AdsConfiguratorCh2V1());
        pingCommand.add((byte)0xFB);
    }

    @Override
    public void startReading() throws ApplicationException {
        super.startReading();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                comPort.writeToPort(pingCommand);
            }
        };
        pingTimer = new Timer();
        pingTimer.schedule(timerTask, 1000, 1000);
    }

    public void stopReading() {
        super.stopReading();
        if (!isRecording) return;
        pingTimer.cancel();
    }
}
