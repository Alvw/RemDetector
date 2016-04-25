package device.ads8ch_v1;


import bdf.BdfParser;
import comport.ComPortListener;
import device.ads2ch_v1.FrameDecoderCh2V1;
import device.general.AdsConfiguration;
import device.general.FrameDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class FrameDecoderCh8V1 extends FrameDecoderCh2V1 {

    private static final Log log = LogFactory.getLog(FrameDecoderCh8V1.class);




    public FrameDecoderCh8V1(AdsConfiguration configuration) {
        super(configuration);
    }


}
