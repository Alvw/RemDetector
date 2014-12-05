package dreamrec;

import bdf.BdfConfig;
import bdf.BdfProvider;
import bdf.DeviceBdfConfig;

/**
 * Created by mac on 04/12/14.
 */
public interface BdfDevice  extends BdfProvider {
    @Override
    DeviceBdfConfig getBdfConfig();
}
