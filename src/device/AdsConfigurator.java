package device;

import java.util.List;

/**
 * Created by mac on 05/03/15.
 */
public interface AdsConfigurator {
    public List<Byte> writeAdsConfiguration();
    public AdsConfiguration getAdsConfiguration();
    public FrameDecoder getFrameDecoder();
}
